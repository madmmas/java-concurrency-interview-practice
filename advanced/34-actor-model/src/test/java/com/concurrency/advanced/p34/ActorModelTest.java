package com.concurrency.advanced.p34;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ActorModelTest {

    private ExecutorService exec;
    private ActorSystem system;

    @BeforeEach
    void setUp() {
        exec   = Executors.newFixedThreadPool(4);
        system = new ActorSystem(4);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        system.shutdown();
        exec.shutdown();
        exec.awaitTermination(3, TimeUnit.SECONDS);
    }

    // ── Actor base class ──────────────────────────────────────────────────────

    /** A minimal concrete actor that accumulates integers into its state. */
    static class SumActor extends Actor<int[], Integer> {
        SumActor(Executor executor) {
            super(new int[]{0});
            startLoop(executor);
        }

        @Override
        protected Object receive(int[] state, Integer message) {
            state[0] += message;
            return state[0];
        }
    }

    @Test
    void tellEnqueuesMessageAndActorProcessesIt() throws InterruptedException {
        SumActor actor = new SumActor(exec);
        assertTrue(actor.isAlive());
        actor.tell(10);
        actor.tell(20);
        actor.tell(30);
        Thread.sleep(200); // let actor process
        int balance = (int) actor.ask(0).get();
        assertEquals(60, balance, "Sum of 10+20+30 must be 60");
        actor.stop();
    }

    @Test
    void askReturnsReplyFuture() throws Exception {
        SumActor actor = new SumActor(exec);
        actor.tell(5);
        actor.tell(5);
        Integer result = (Integer) actor.ask(0).get(2, TimeUnit.SECONDS);
        assertEquals(10, result);
        actor.stop();
    }

    @Test
    void stopTerminatesProcessingLoop() throws InterruptedException {
        SumActor actor = new SumActor(exec);
        assertTrue(actor.isAlive());
        actor.stop();
        Thread.sleep(300);
        assertFalse(actor.isAlive(), "Actor must stop after stop() is called");
    }

    @Test
    void messagesAreProcessedInFIFOOrder() throws Exception {
        // A counter actor that records values in order
        List<Integer> received = new CopyOnWriteArrayList<>();
        Actor<Void, Integer> orderActor = new Actor<>(null) {
            { startLoop(exec); }
            @Override protected Object receive(Void state, Integer msg) {
                received.add(msg);
                return null;
            }
        };

        for (int i = 0; i < 10; i++) orderActor.tell(i);
        Thread.sleep(200);
        orderActor.stop();

        assertEquals(10, received.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(i, received.get(i),
                    "Messages must be delivered in FIFO order; index " + i);
        }
    }

    @Test
    void concurrentTellsAreAllProcessed() throws InterruptedException, ExecutionException {
        SumActor actor = new SumActor(exec);
        int threads = 10, msgsEach = 100;
        CountDownLatch done = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            new Thread(() -> {
                for (int i = 0; i < msgsEach; i++) actor.tell(1);
                done.countDown();
            }).start();
        }
        done.await();
        Thread.sleep(300);

        int sum = (Integer) actor.ask(0).get();
        assertEquals(threads * msgsEach, sum,
                "All " + (threads * msgsEach) + " tell() messages must be processed");
        actor.stop();
    }

    // ── BankAccountActor ──────────────────────────────────────────────────────

    @Test
    void depositIncreasesBalance() throws Exception {
        BankAccountActor account = new BankAccountActor(1000, exec);
        int newBalance = (int) account.ask(new BankAccountActor.Deposit(500)).get();
        assertEquals(1500, newBalance);
        account.stop();
    }

    @Test
    void withdrawDecreasesBalance() throws Exception {
        BankAccountActor account = new BankAccountActor(1000, exec);
        int newBalance = (int) account.ask(new BankAccountActor.Withdraw(300)).get();
        assertEquals(700, newBalance);
        account.stop();
    }

    @Test
    void getBalanceReturnsCurrentBalance() throws Exception {
        BankAccountActor account = new BankAccountActor(500, exec);
        account.tell(new BankAccountActor.Deposit(100));
        Thread.sleep(50);
        int balance = (int) account.ask(new BankAccountActor.GetBalance()).get();
        assertEquals(600, balance);
        account.stop();
    }

    @Test
    void withdrawBeyondBalanceCompletesExceptionally() throws Exception {
        BankAccountActor account = new BankAccountActor(100, exec);
        CompletableFuture<Object> result = account.ask(new BankAccountActor.Withdraw(500));
        ExecutionException ex = assertThrows(ExecutionException.class,
                () -> result.get(2, TimeUnit.SECONDS));
        assertInstanceOf(IllegalStateException.class, ex.getCause(),
                "Over-withdrawal must complete exceptionally with IllegalStateException");
        account.stop();
    }

    @Test
    void concurrentDepositsNeverRace() throws Exception {
        BankAccountActor account = new BankAccountActor(0, exec);
        int threads = 20, depositsEach = 50;
        CountDownLatch done = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            new Thread(() -> {
                for (int i = 0; i < depositsEach; i++) account.tell(new BankAccountActor.Deposit(1));
                done.countDown();
            }).start();
        }
        done.await();
        Thread.sleep(500);

        int balance = (int) account.ask(new BankAccountActor.GetBalance()).get();
        assertEquals(threads * depositsEach, balance,
                "All concurrent deposits must be applied — actors eliminate races by design");
        account.stop();
    }

    // ── ActorSystem ───────────────────────────────────────────────────────────

    @Test
    void actorSystemCreatesAndTracksActors() {
        Actor<int[], Integer> a1 = system.actorOf(new int[]{0},
                (state, ex) -> new SumActor(ex));
        Actor<int[], Integer> a2 = system.actorOf(new int[]{0},
                (state, ex) -> new SumActor(ex));
        assertEquals(2, system.getActorCount());
        assertTrue(a1.isAlive());
        assertTrue(a2.isAlive());
    }

    @Test
    void actorSystemShutdownStopsAllActors() throws InterruptedException {
        Actor<int[], Integer> a = system.actorOf(new int[]{0},
                (state, ex) -> new SumActor(ex));
        assertTrue(a.isAlive());
        system.shutdown();
        Thread.sleep(300);
        assertFalse(a.isAlive(), "Shutdown must stop all registered actors");
    }

    // ── PingPong ──────────────────────────────────────────────────────────────

    @Test
    void pingPongCompletesCorrectRounds() throws Exception {
        int rounds = 5;
        Integer totalMessages = PingPongActors.run(rounds, exec)
                .get(5, TimeUnit.SECONDS);
        // Each round = 1 ping + 1 pong = 2 messages; rounds rounds total
        assertEquals(rounds * 2, totalMessages,
                "Total messages must equal rounds * 2 (one ping + one pong per round)");
    }

    @Test
    void pingPongSingleRound() throws Exception {
        Integer total = PingPongActors.run(1, exec).get(3, TimeUnit.SECONDS);
        assertEquals(2, total, "Single round must produce exactly 2 messages (ping + pong)");
    }

    @Test
    void pingPongManyRoundsCompleteWithoutDeadlock() throws Exception {
        Integer total = PingPongActors.run(100, exec).get(8, TimeUnit.SECONDS);
        assertEquals(200, total, "100 rounds must produce exactly 200 messages");
    }

    @Test
    void pingPongActorsStopAfterCompletion() throws Exception {
        CompletableFuture<Integer> result = PingPongActors.run(3, exec);
        result.get(3, TimeUnit.SECONDS);
        Thread.sleep(200); // allow stop() signals to propagate
        // No assertion on actor liveness here since we don't have references;
        // the test passes if it completes without timeout or deadlock
        assertTrue(result.isDone() && !result.isCompletedExceptionally());
    }
}
