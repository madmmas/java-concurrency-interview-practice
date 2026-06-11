package com.concurrency.intermediate.p24;

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
class StampedCacheTest {

    private StampedCache<String, Integer> cache;

    @BeforeEach void setUp() { cache = new StampedCache<>(); }

    @Test
    void putAndGetReturnsValue() {
        cache.put("a", 1);
        assertEquals(1, cache.get("a"));
    }

    @Test
    void getMissingKeyReturnsNull() {
        assertNull(cache.get("missing"));
    }

    @Test
    void computeIfAbsentComputesValue() {
        Integer val = cache.computeIfAbsent("key", k -> k.length());
        assertEquals(3, val);
        assertEquals(3, cache.get("key"));
    }

    @Test
    void computeIfAbsentDoesNotOverwriteExisting() {
        cache.put("k", 99);
        Integer val = cache.computeIfAbsent("k", k -> 0);
        assertEquals(99, val, "computeIfAbsent must not overwrite an existing value");
    }

    @Test
    void sizeIsAccurate() {
        assertEquals(0, cache.size());
        cache.put("a", 1);
        cache.put("b", 2);
        assertEquals(2, cache.size());
    }

    @Test
    void concurrentReadsAreCorrect() throws InterruptedException {
        cache.put("shared", 42);
        int readers = 20;
        AtomicInteger errors = new AtomicInteger(0);
        CountDownLatch done = new CountDownLatch(readers);

        for (int i = 0; i < readers; i++) {
            new Thread(() -> {
                try {
                    Integer v = cache.get("shared");
                    if (v == null || v != 42) errors.incrementAndGet();
                } finally { done.countDown(); }
            }).start();
        }
        done.await();
        assertEquals(0, errors.get(), "All concurrent readers must see the correct value");
    }

    @Test
    void concurrentComputeIfAbsentIsIdempotent() throws InterruptedException {
        AtomicInteger computeCount = new AtomicInteger(0);
        int threads = 20;
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    go.await();
                    cache.computeIfAbsent("once", k -> { computeCount.incrementAndGet(); return 1; });
                } catch (InterruptedException ignored) {}
                finally { done.countDown(); }
            }).start();
        }
        go.countDown();
        done.await();

        assertEquals(1, cache.size(), "Only one entry must be inserted");
        // computeCount may be > 1 due to race, but final value must be 1
        assertEquals(1, cache.get("once"));
    }

    @Test
    void optimisticHitsAreTrackedForUncontendedReads() {
        cache.put("x", 10);
        for (int i = 0; i < 10; i++) cache.get("x");
        // Under no contention all reads should be optimistic hits
        assertTrue(cache.getOptimisticHits() > 0,
                "Optimistic reads should succeed under no contention");
    }
}
