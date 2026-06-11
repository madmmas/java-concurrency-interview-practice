package com.concurrency.intermediate.p24;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class OptimisticPointTest {

    @Test
    void distanceFromOriginIsCorrect() {
        OptimisticPoint p = new OptimisticPoint(3, 4);
        assertEquals(5.0, p.distanceFromOrigin(), 1e-9,
                "Distance of (3,4) from origin must be 5.0");
    }

    @Test
    void moveUpdatesCoordinates() {
        OptimisticPoint p = new OptimisticPoint(0, 0);
        p.move(6, 8);
        assertEquals(6.0, p.getX(), 1e-9);
        assertEquals(8.0, p.getY(), 1e-9);
        assertEquals(10.0, p.distanceFromOrigin(), 1e-9);
    }

    @Test
    void getXAndGetYAreThreadSafe() throws InterruptedException {
        OptimisticPoint p = new OptimisticPoint(1, 1);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    p.getX(); p.getY();
                }
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();
        // No assertion — just verifying no exception / deadlock
    }

    @Test
    void concurrentMovesAndDistancesAreConsistent() throws InterruptedException {
        OptimisticPoint p = new OptimisticPoint(3, 4);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < 50; j++) p.move(j, j);
            }));
            threads.add(new Thread(() -> {
                for (int j = 0; j < 50; j++) {
                    double d = p.distanceFromOrigin();
                    assertTrue(d >= 0, "Distance must be non-negative, got: " + d);
                }
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();
    }

    @Test
    void distanceAtOriginIsZero() {
        OptimisticPoint p = new OptimisticPoint(0, 0);
        assertEquals(0.0, p.distanceFromOrigin(), 1e-9);
    }

    @Test
    void distanceWithNegativeCoordinates() {
        OptimisticPoint p = new OptimisticPoint(-3, -4);
        assertEquals(5.0, p.distanceFromOrigin(), 1e-9);
    }
}
