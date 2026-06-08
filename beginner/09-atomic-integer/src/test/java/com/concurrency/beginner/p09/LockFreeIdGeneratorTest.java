package com.concurrency.beginner.p09;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class LockFreeIdGeneratorTest {

    @Test
    void idsStartAtOne() {
        LockFreeIdGenerator gen = new LockFreeIdGenerator();
        assertEquals(1, gen.nextId());
    }

    @Test
    void idsAreMonotonicallyIncreasing() {
        LockFreeIdGenerator gen = new LockFreeIdGenerator();
        assertEquals(1, gen.nextId());
        assertEquals(2, gen.nextId());
        assertEquals(3, gen.nextId());
    }

    @Test
    void currentIdReflectsLastIssuedId() {
        LockFreeIdGenerator gen = new LockFreeIdGenerator();
        assertEquals(0, gen.currentId());
        gen.nextId();
        assertEquals(1, gen.currentId());
        gen.nextId();
        assertEquals(2, gen.currentId());
    }

    @Test
    void concurrentCallsProduceUniqueIds() throws InterruptedException {
        LockFreeIdGenerator gen = new LockFreeIdGenerator();
        int threads = 20, idsPerThread = 500;
        Set<Integer> allIds = ConcurrentHashMap.newKeySet();
        AtomicInteger collisions = new AtomicInteger(0);

        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            workers.add(new Thread(() -> {
                for (int j = 0; j < idsPerThread; j++) {
                    int id = gen.nextId();
                    if (!allIds.add(id)) collisions.incrementAndGet();
                }
            }));
        }
        workers.forEach(Thread::start);
        for (Thread t : workers) t.join();

        assertEquals(0, collisions.get(), "No two threads should receive the same ID");
        assertEquals(threads * idsPerThread, allIds.size(), "Total unique IDs must equal total requests");
    }

    // Helper import for test
    private final java.util.List<Thread> workers = new java.util.ArrayList<>();
}
