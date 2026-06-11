package com.concurrency.advanced.p30;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class CustomThreadPoolTest {

    // ── InstrumentedThreadPool ────────────────────────────────────────────────

    private InstrumentedThreadPool pool;

    @BeforeEach
    void setUp() {
        pool = new InstrumentedThreadPool(
                2, 4, 30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100));
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.SECONDS);
    }

    @Test
    void totalTasksRunCountsAllCompletions() throws InterruptedException {
        int n = 10;
        CountDownLatch done = new CountDownLatch(n);
        for (int i = 0; i < n; i++) {
            pool.execute(() -> {
                try { Thread.sleep(10); }
                catch (InterruptedException ignored) {}
                finally { done.countDown(); }
            });
        }
        done.await();
        assertEquals(n, pool.getTotalTasksRun(),
                "All " + n + " task completions must be counted");
    }

    @Test
    void tasksThatThrowAreStillCounted() throws InterruptedException {
        int n = 3;
        CountDownLatch done = new CountDownLatch(n);
        for (int i = 0; i < n; i++) {
            pool.execute(() -> {
                try { throw new RuntimeException("intentional"); }
                finally { done.countDown(); }
            });
        }
        done.await();
        Thread.sleep(50);
        assertEquals(n, pool.getTotalTasksRun(),
                "Tasks that throw exceptions must still be counted");
    }

    @Test
    void latencyStatsAreRecordedAfterExecution() throws InterruptedException {
        int n = 5;
        CountDownLatch done = new CountDownLatch(n);
        for (int i = 0; i < n; i++) {
            pool.execute(() -> {
                try { Thread.sleep(20); }
                catch (InterruptedException ignored) {}
                finally { done.countDown(); }
            });
        }
        done.await();

        assertTrue(pool.getAverageLatencyMs() >= 10,
                "Average latency must be ≥10 ms for 20 ms tasks, got: " + pool.getAverageLatencyMs());
        assertTrue(pool.getMinLatencyMs() >= 0,
                "Min latency must be non-negative");
        assertTrue(pool.getMaxLatencyMs() >= pool.getMinLatencyMs(),
                "Max latency must be ≥ min latency");
        assertEquals(n, pool.getTotalTasksRun());
    }

    @Test
    void terminatedAtIsNegativeBeforeShutdown() {
        assertEquals(-1, pool.getTerminatedAt(),
                "terminatedAt must be -1 before pool terminates");
    }

    @Test
    void terminatedAtIsSetAfterFullTermination() throws InterruptedException {
        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.SECONDS);
        assertTrue(pool.getTerminatedAt() > 0,
                "terminatedAt must be set to a positive epoch ms after termination");
    }

    @Test
    void averageLatencyIsZeroWithNoTasks() {
        assertEquals(0.0, pool.getAverageLatencyMs(), 1e-9);
        assertEquals(0.0, pool.getMinLatencyMs(),     1e-9);
        assertEquals(0.0, pool.getMaxLatencyMs(),     1e-9);
    }

    // ── WorkerThreadFactory ───────────────────────────────────────────────────

    @Test
    void threadNamesFollowConvention() throws InterruptedException {
        WorkerThreadFactory factory = new WorkerThreadFactory("mypool", false);
        String n1 = factory.newThread(() -> {}).getName();
        String n2 = factory.newThread(() -> {}).getName();
        String n3 = factory.newThread(() -> {}).getName();
        assertEquals("pool-mypool-worker-1", n1);
        assertEquals("pool-mypool-worker-2", n2);
        assertEquals("pool-mypool-worker-3", n3);
    }

    @Test
    void daemonFlagIsRespected() {
        WorkerThreadFactory daemonFactory    = new WorkerThreadFactory("d", true);
        WorkerThreadFactory nonDaemonFactory = new WorkerThreadFactory("nd", false);
        assertTrue(daemonFactory.newThread(() -> {}).isDaemon(),
                "Threads from daemon factory must be daemon");
        assertFalse(nonDaemonFactory.newThread(() -> {}).isDaemon(),
                "Threads from non-daemon factory must not be daemon");
    }

    @Test
    void createdCountTracksAllCreations() {
        WorkerThreadFactory factory = new WorkerThreadFactory("cnt", true);
        assertEquals(0, factory.getCreatedCount());
        factory.newThread(() -> {});
        factory.newThread(() -> {});
        assertEquals(2, factory.getCreatedCount());
    }

    @Test
    void uncaughtExceptionHandlerRecordsThrowable() throws InterruptedException {
        WorkerThreadFactory factory = new WorkerThreadFactory("ex", true);
        ThreadPoolExecutor testPool = new ThreadPoolExecutor(
                1, 1, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), factory);

        CountDownLatch done = new CountDownLatch(1);
        testPool.execute(() -> {
            done.countDown();
            throw new RuntimeException("test-uncaught");
        });
        done.await();
        Thread.sleep(200);  // give the UncaughtExceptionHandler time to fire
        testPool.shutdown();

        assertFalse(factory.getUncaughtExceptions().isEmpty(),
                "UncaughtExceptionHandler must record the thrown exception");
        assertEquals("test-uncaught",
                factory.getUncaughtExceptions().get(0).getMessage());
    }

    @Test
    void getUncaughtExceptionsIsUnmodifiable() {
        WorkerThreadFactory factory = new WorkerThreadFactory("ro", true);
        assertThrows(UnsupportedOperationException.class,
                () -> factory.getUncaughtExceptions().add(new RuntimeException()));
    }

    // ── BoundedCallerRunsPool ─────────────────────────────────────────────────

    @Test
    void normalSubmissionsCompleteWithoutCallerRuns() throws InterruptedException {
        BoundedCallerRunsPool bcrp = new BoundedCallerRunsPool(2, 4, 20);
        int tasks = 10;
        CountDownLatch done = new CountDownLatch(tasks);
        AtomicInteger ran = new AtomicInteger(0);
        for (int i = 0; i < tasks; i++) {
            bcrp.submit(() -> { ran.incrementAndGet(); done.countDown(); });
        }
        done.await();
        assertEquals(tasks, ran.get(), "All tasks must complete");
        assertEquals(0, bcrp.getCallerRunCount(),
                "No caller-runs expected when pool has capacity");
        bcrp.shutdown();
    }

    @Test
    void saturatedPoolRunsTasksOnCallerThread() throws InterruptedException {
        // 1 thread, queue of 1 → 3rd task will be rejected → caller-runs
        BoundedCallerRunsPool bcrp = new BoundedCallerRunsPool(1, 1, 1);
        CountDownLatch blocker = new CountDownLatch(1);
        AtomicInteger completed = new AtomicInteger(0);

        // Block the single pool thread
        bcrp.submit(() -> { try { blocker.await(); } catch (InterruptedException ignored) {} });
        // Fill the queue
        bcrp.submit(completed::incrementAndGet);
        // This should be caller-run
        bcrp.submit(completed::incrementAndGet);

        assertTrue(bcrp.getCallerRunCount() >= 1,
                "At least one task must be caller-run when pool + queue are saturated");
        blocker.countDown();
        bcrp.shutdown();
    }

    @Test
    void allTasksCompleteWithCallerRuns() throws InterruptedException {
        BoundedCallerRunsPool bcrp = new BoundedCallerRunsPool(1, 2, 2);
        AtomicInteger total = new AtomicInteger(0);
        int tasks = 20;
        for (int i = 0; i < tasks; i++) bcrp.submit(total::incrementAndGet);
        bcrp.shutdown();
        assertEquals(tasks, total.get(),
                "All tasks must complete — either on pool threads or caller thread");
    }
}
