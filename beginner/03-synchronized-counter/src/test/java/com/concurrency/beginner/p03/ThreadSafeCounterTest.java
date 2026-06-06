package com.concurrency.beginner.p03;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ThreadSafeCounterTest {

    private ThreadSafeCounter counter;

    @BeforeEach
    void setUp() {
        counter = new ThreadSafeCounter();
    }

    @Test
    void singleThreadIncrement() {
        counter.increment();
        counter.increment();
        counter.increment();
        assertEquals(3, counter.getCount());
    }

    @Test
    void singleThreadDecrement() {
        counter.increment();
        counter.increment();
        counter.decrement();
        assertEquals(1, counter.getCount());
    }

    @Test
    void resetSetsToZero() {
        counter.increment();
        counter.increment();
        counter.reset();
        assertEquals(0, counter.getCount());
    }

    @Test
    void incrementByDelta() {
        counter.incrementBy(10);
        counter.incrementBy(5);
        assertEquals(15, counter.getCount());
    }

    @Test
    void concurrentIncrementsAreAccurate() throws InterruptedException {
        int threads = 10;
        int incrementsPerThread = 1000;

        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            workers.add(new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            }));
        }

        workers.forEach(Thread::start);
        for (Thread t : workers) t.join();

        assertEquals(threads * incrementsPerThread, counter.getCount(),
                "All increments must be reflected — no lost updates");
    }

    @Test
    void concurrentMixedOperations() throws InterruptedException {
        // 5 threads increment 1000x, 5 threads decrement 1000x → net 0
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            workers.add(new Thread(() -> { for (int j = 0; j < 1000; j++) counter.increment(); }));
            workers.add(new Thread(() -> { for (int j = 0; j < 1000; j++) counter.decrement(); }));
        }
        workers.forEach(Thread::start);
        for (Thread t : workers) t.join();
        assertEquals(0, counter.getCount(), "Net result of equal increments and decrements should be 0");
    }

    @Test
    void unsafeCounterDemonstratesRaceCondition() throws InterruptedException {
        // Run many times to expose the race — at least one run should show data loss
        boolean raceDetected = false;
        for (int attempt = 0; attempt < 5 && !raceDetected; attempt++) {
            UnsafeCounter unsafe = new UnsafeCounter();
            int threads = 10, ops = 1000;
            List<Thread> workers = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                workers.add(new Thread(() -> { for (int j = 0; j < ops; j++) unsafe.increment(); }));
            }
            workers.forEach(Thread::start);
            for (Thread t : workers) t.join();
            if (unsafe.getCount() != threads * ops) raceDetected = true;
        }
        assertTrue(raceDetected,
                "UnsafeCounter should demonstrate data loss under concurrent access");
    }
}
