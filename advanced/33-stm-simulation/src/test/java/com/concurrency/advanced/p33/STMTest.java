package com.concurrency.advanced.p33;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class STMTest {

    // ── Single-threaded correctness ───────────────────────────────────────────

    @Test
    void readDirectReturnsInitialValue() {
        TVar<Integer> v = new TVar<>(42);
        assertEquals(42, v.readDirect());
    }

    @Test
    void singleTVarReadAndWrite() {
        TVar<Integer> v = new TVar<>(10);
        STM.atomically((Transaction tx) -> {
            int val = v.read(tx);
            v.write(tx, val + 5);
        });
        assertEquals(15, v.readDirect(), "Value must be 15 after adding 5");
    }

    @Test
    void versionIncrementsOnCommit() {
        TVar<String> v = new TVar<>("hello");
        long before = v.getVersion();
        STM.atomically((Transaction tx) -> v.write(tx, "world"));
        assertEquals(before + 1, v.getVersion(), "Version must increment by 1 per commit");
    }

    @Test
    void noWriteMeansNoVersionChange() {
        TVar<Integer> v = new TVar<>(99);
        long before = v.getVersion();
        STM.atomically((Transaction tx) -> v.read(tx));  // read-only
        assertEquals(before, v.getVersion(), "Read-only transaction must not change version");
    }

    @Test
    void twoTVarAtomicUpdate() {
        TVar<Integer> a = new TVar<>(100);
        TVar<Integer> b = new TVar<>(200);
        STM.atomically((Transaction tx) -> {
            int va = a.read(tx);
            int vb = b.read(tx);
            a.write(tx, va + 50);
            b.write(tx, vb - 50);
        });
        assertEquals(150, a.readDirect());
        assertEquals(150, b.readDirect());
    }

    @Test
    void uncommittedWriteNotVisibleToDirectRead() {
        TVar<Integer> v = new TVar<>(1);
        Transaction tx = new Transaction();
        v.write(tx, 999);
        // Not yet committed — direct read must still see original value
        assertEquals(1, v.readDirect(),
                "Buffered write must not be visible before commit");
    }

    @Test
    void writeSetShadowsReadSet() {
        TVar<Integer> v = new TVar<>(5);
        Transaction tx = new Transaction();
        v.write(tx, 100);
        // Reading through the same transaction should see the buffered write
        assertEquals(100, v.read(tx),
                "Read through same transaction must see its own buffered write");
    }

    // ── transfer() ────────────────────────────────────────────────────────────

    @Test
    void transferMovesMoneyCorrectly() {
        TVar<Integer> alice = new TVar<>(1000);
        TVar<Integer> bob   = new TVar<>(500);
        STM.transfer(alice, bob, 200);
        assertEquals(800,  alice.readDirect(), "Alice must have 800 after transferring 200");
        assertEquals(700,  bob.readDirect(),   "Bob must have 700 after receiving 200");
    }

    @Test
    void transferPreservesTotalBalance() {
        TVar<Integer> a = new TVar<>(1000);
        TVar<Integer> b = new TVar<>(1000);
        int total = a.readDirect() + b.readDirect();
        STM.transfer(a, b, 300);
        assertEquals(total, a.readDirect() + b.readDirect(),
                "Total money must be conserved");
    }

    @Test
    void transferThrowsWhenInsufficientBalance() {
        TVar<Integer> a = new TVar<>(50);
        TVar<Integer> b = new TVar<>(0);
        assertThrows(IllegalStateException.class,
                () -> STM.transfer(a, b, 100),
                "Transfer of more than available balance must throw");
    }

    // ── swap() ────────────────────────────────────────────────────────────────

    @Test
    void swapExchangesValues() {
        TVar<String> x = new TVar<>("hello");
        TVar<String> y = new TVar<>("world");
        STM.swap(x, y);
        assertEquals("world", x.readDirect());
        assertEquals("hello", y.readDirect());
    }

    @Test
    void swapIsIdempotentWhenRunTwice() {
        TVar<Integer> a = new TVar<>(1);
        TVar<Integer> b = new TVar<>(2);
        STM.swap(a, b);
        STM.swap(a, b);
        assertEquals(1, a.readDirect(), "Double swap must restore original values");
        assertEquals(2, b.readDirect());
    }

    // ── Concurrent correctness ────────────────────────────────────────────────

    @Test
    void concurrentTransfersPreserveTotalBalance() throws InterruptedException {
        int initial = 1000;
        TVar<Integer> a = new TVar<>(initial);
        TVar<Integer> b = new TVar<>(initial);
        TVar<Integer> c = new TVar<>(initial);

        int threads = 10, txnsEach = 50;
        List<Thread> workers = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            workers.add(new Thread(() -> {
                for (int i = 0; i < txnsEach; i++) {
                    try { STM.transfer(a, b, 10); } catch (Exception ignored) {}
                    try { STM.transfer(b, c, 10); } catch (Exception ignored) {}
                    try { STM.transfer(c, a, 10); } catch (Exception ignored) {}
                }
            }));
        }
        workers.forEach(Thread::start);
        for (Thread w : workers) w.join();

        int total = a.readDirect() + b.readDirect() + c.readDirect();
        assertEquals(3 * initial, total,
                "Total balance must be conserved across all concurrent transfers; got: " + total);
    }

    @Test
    void concurrentSwapsNeverCorruptValues() throws InterruptedException {
        TVar<Integer> x = new TVar<>(1);
        TVar<Integer> y = new TVar<>(2);
        int threads = 20, ops = 100;
        CountDownLatch done = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            new Thread(() -> {
                for (int i = 0; i < ops; i++) STM.swap(x, y);
                done.countDown();
            }).start();
        }
        done.await();

        // After an even number of threads each doing 100 swaps (= 2000 total),
        // values must still be from {1, 2}
        int xv = x.readDirect(), yv = y.readDirect();
        assertTrue((xv == 1 && yv == 2) || (xv == 2 && yv == 1),
                "After concurrent swaps, values must still be {1,2}; got x=" + xv + " y=" + yv);
    }

    @Test
    void conflictingTransactionsRetryAutomatically() throws InterruptedException {
        TVar<Integer> counter = new TVar<>(0);
        int threads = 10, incEach = 100;
        List<Thread> workers = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            workers.add(new Thread(() -> {
                for (int i = 0; i < incEach; i++) {
                    STM.atomically((Transaction tx) -> {
                        int v = counter.read(tx);
                        counter.write(tx, v + 1);
                    });
                }
            }));
        }
        workers.forEach(Thread::start);
        for (Thread w : workers) w.join();

        assertEquals(threads * incEach, counter.readDirect(),
                "STM retry mechanism must ensure all increments are applied; "
                + "got: " + counter.readDirect());
    }
}
