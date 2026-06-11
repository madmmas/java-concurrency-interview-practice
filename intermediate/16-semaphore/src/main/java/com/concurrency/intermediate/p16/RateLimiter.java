package com.concurrency.intermediate.p16;

import java.util.concurrent.Semaphore;

/**
 * Problem 16 – Semaphore: Token-Bucket Rate Limiter
 *
 * Limits callers to maxRequestsPerWindow requests per windowMs milliseconds.
 * A background daemon thread refills the semaphore at the end of each window.
 *
 * Implementation guide:
 *  - Semaphore starts at maxRequestsPerWindow (all permits available)
 *  - acquire() / tryAcquire() consume permits
 *  - Refill thread: every windowMs ms, release enough permits to restore to max
 *    i.e. release(maxRequestsPerWindow - availablePermits())
 */
public class RateLimiter {

    private final int maxRequestsPerWindow;
    private final long windowMs;
    private final Semaphore semaphore;
    private volatile boolean running = false;
    private Thread refillThread;

    public RateLimiter(int maxRequestsPerWindow, long windowMs) {
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowMs = windowMs;
        this.semaphore = new Semaphore(maxRequestsPerWindow, true);
    }

    /**
     * Starts the background daemon refill thread.
     * The thread wakes every windowMs ms and restores permits to maxRequestsPerWindow.
     */
    public void start() {
        // TODO: create a daemon thread that loops:
        //   sleep(windowMs)
        //   int used = maxRequestsPerWindow - semaphore.availablePermits()
        //   if (used > 0) semaphore.release(used)
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Stops the background refill thread. */
    public void stop() throws InterruptedException {
        // TODO: set running = false, interrupt and join the refill thread
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Attempts to acquire a permit without blocking.
     * @return true if a request is permitted right now; false if rate limit exceeded
     */
    public boolean tryAcquire() {
        // TODO: return semaphore.tryAcquire()
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Acquires a permit, blocking until one is available.
     */
    public void acquire() throws InterruptedException {
        // TODO: semaphore.acquire()
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns how many permits are currently available. */
    public int availablePermits() {
        return semaphore.availablePermits();
    }
}
