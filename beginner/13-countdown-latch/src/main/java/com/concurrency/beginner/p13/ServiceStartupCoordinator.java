package com.concurrency.beginner.p13;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 13 – CountDownLatch: Service Startup Coordinator
 *
 * Coordinates a set of services that must all start before the system is "ready".
 * After calling registerService() for each service, call waitForAll() to block
 * until every service has signalled readiness via serviceReady().
 */
public class ServiceStartupCoordinator {

    private final List<String> services = new ArrayList<>();
    private final AtomicInteger readyCount = new AtomicInteger(0);
    private CountDownLatch latch;

    /**
     * Registers a service that must be ready before the system starts.
     * Must be called before waitForAll().
     */
    public synchronized void registerService(String name) {
        // TODO: add to services list
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Initialises the latch and blocks until all registered services have called
     * serviceReady(). Must be called after all registerService() calls.
     */
    public void waitForAll() throws InterruptedException {
        // TODO: create latch sized to services.size(), then latch.await()
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Same as waitForAll() but with a timeout.
     * @return true if all services became ready before the timeout; false otherwise
     */
    public boolean waitForAll(long timeoutMs) throws InterruptedException {
        // TODO: latch.await(timeoutMs, TimeUnit.MILLISECONDS)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Signals that the named service has finished starting.
     * Decrements the latch count by 1.
     */
    public void serviceReady(String name) {
        // TODO: increment readyCount, then latch.countDown()
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns how many services have called serviceReady() so far. */
    public int getReadyCount() {
        return readyCount.get();
    }

    /** Returns the names of all registered services. */
    public List<String> getRegisteredServices() {
        return List.copyOf(services);
    }
}
