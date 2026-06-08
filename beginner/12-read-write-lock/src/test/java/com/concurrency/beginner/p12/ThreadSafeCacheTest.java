package com.concurrency.beginner.p12;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ThreadSafeCacheTest {

    private ThreadSafeCache<String, Integer> cache;

    @BeforeEach
    void setUp() { cache = new ThreadSafeCache<>(); }

    @Test
    void putAndGet() {
        cache.put("a", 1);
        cache.put("b", 2);
        assertEquals(1, cache.get("a"));
        assertEquals(2, cache.get("b"));
        assertNull(cache.get("missing"));
    }

    @Test
    void removeDeletesEntry() {
        cache.put("x", 99);
        cache.remove("x");
        assertNull(cache.get("x"));
        assertFalse(cache.containsKey("x"));
    }

    @Test
    void sizeAndContainsKey() {
        assertEquals(0, cache.size());
        cache.put("k1", 1);
        cache.put("k2", 2);
        assertEquals(2, cache.size());
        assertTrue(cache.containsKey("k1"));
        assertFalse(cache.containsKey("k99"));
    }

    @Test
    void readCountTracksAllReadOps() {
        cache.put("a", 1);
        cache.get("a");
        cache.get("b");
        cache.containsKey("a");
        cache.size();
        assertEquals(4, cache.getReadCount(), "get×2 + containsKey×1 + size×1 = 4 reads");
        assertEquals(1, cache.getWriteCount());
    }

    @Test
    void concurrentReadsAllSucceed() throws InterruptedException {
        cache.put("key", 42);
        int readers = 30;
        AtomicInteger successes = new AtomicInteger();
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(readers);
        for (int i = 0; i < readers; i++) {
            new Thread(() -> {
                try {
                    go.await();
                    Integer v = cache.get("key");
                    if (v != null && v == 42) successes.incrementAndGet();
                } catch (InterruptedException ignored) {}
                finally { done.countDown(); }
            }).start();
        }
        go.countDown();
        done.await();
        assertEquals(readers, successes.get(),
                "All " + readers + " concurrent readers should see the correct value");
    }

    @Test
    void concurrentWritesAndReadsAreThreadSafe() throws InterruptedException {
        int writers = 5, readers = 10, opsEach = 100;
        List<Thread> all = new ArrayList<>();
        for (int w = 0; w < writers; w++) {
            final int wid = w;
            all.add(new Thread(() -> {
                for (int i = 0; i < opsEach; i++) cache.put("key-" + wid, i);
            }));
        }
        for (int r = 0; r < readers; r++) {
            all.add(new Thread(() -> {
                for (int i = 0; i < opsEach; i++) cache.get("key-" + (i % writers));
            }));
        }
        all.forEach(Thread::start);
        for (Thread t : all) t.join();
        assertEquals(writers, cache.size(), "Should have one entry per writer key");
    }
}
