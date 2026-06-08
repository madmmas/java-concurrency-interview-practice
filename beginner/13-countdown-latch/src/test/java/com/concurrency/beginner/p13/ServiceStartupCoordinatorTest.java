package com.concurrency.beginner.p13;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ServiceStartupCoordinatorTest {

    @Test
    void waitForAllBlocksUntilAllReady() throws InterruptedException {
        ServiceStartupCoordinator coord = new ServiceStartupCoordinator();
        coord.registerService("db");
        coord.registerService("cache");
        coord.registerService("http");

        AtomicBoolean coordinatorPassed = new AtomicBoolean(false);
        Thread coordinator = new Thread(() -> {
            try {
                coord.waitForAll();
                coordinatorPassed.set(true);
            } catch (InterruptedException ignored) {}
        });
        coordinator.start();

        Thread.sleep(50);
        assertFalse(coordinatorPassed.get(), "waitForAll() should still be blocking");

        coord.serviceReady("db");
        coord.serviceReady("cache");
        Thread.sleep(50);
        assertFalse(coordinatorPassed.get(), "Still waiting for 'http'");

        coord.serviceReady("http");
        coordinator.join(2000);
        assertTrue(coordinatorPassed.get(), "waitForAll() should return after all services are ready");
    }

    @Test
    void readyCountTracksProgress() throws InterruptedException {
        ServiceStartupCoordinator coord = new ServiceStartupCoordinator();
        coord.registerService("svc1");
        coord.registerService("svc2");
        // initialise latch via waitForAll on background thread
        new Thread(() -> {
            try { coord.waitForAll(); } catch (InterruptedException ignored) {}
        }).start();
        Thread.sleep(30);

        assertEquals(0, coord.getReadyCount());
        coord.serviceReady("svc1");
        Thread.sleep(10);
        assertEquals(1, coord.getReadyCount());
        coord.serviceReady("svc2");
        Thread.sleep(10);
        assertEquals(2, coord.getReadyCount());
    }

    @Test
    void waitForAllWithTimeoutReturnsFalseOnTimeout() throws InterruptedException {
        ServiceStartupCoordinator coord = new ServiceStartupCoordinator();
        coord.registerService("slow-service");
        // Start latch, but never signal readiness
        boolean completed = coord.waitForAll(150);
        assertFalse(completed, "Should return false when timeout expires before all services ready");
    }

    @Test
    void waitForAllWithTimeoutReturnsTrueWhenAllReady() throws InterruptedException {
        ServiceStartupCoordinator coord = new ServiceStartupCoordinator();
        coord.registerService("fast-service");
        new Thread(() -> {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            coord.serviceReady("fast-service");
        }).start();
        boolean completed = coord.waitForAll(2000);
        assertTrue(completed, "Should return true when service becomes ready within timeout");
    }

    @Test
    void concurrentServicesSignalCorrectly() throws InterruptedException {
        int n = 20;
        ServiceStartupCoordinator coord = new ServiceStartupCoordinator();
        for (int i = 0; i < n; i++) coord.registerService("svc-" + i);

        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            final String name = "svc-" + i;
            workers.add(new Thread(() -> {
                try { Thread.sleep((long)(Math.random() * 100)); } catch (InterruptedException ignored) {}
                coord.serviceReady(name);
            }));
        }
        workers.forEach(Thread::start);

        boolean allReady = coord.waitForAll(5000);
        assertTrue(allReady, "All " + n + " services should become ready");
        assertEquals(n, coord.getReadyCount());
    }
}
