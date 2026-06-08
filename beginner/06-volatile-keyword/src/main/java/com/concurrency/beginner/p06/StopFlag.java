package com.concurrency.beginner.p06;

/**
 * Problem 06 – Volatile Keyword: Stop Flag
 *
 * A cancellable background worker that uses a volatile flag for safe stopping.
 *
 * Requirements:
 *  - The worker thread must stop promptly when stop() is called.
 *  - Without `volatile`, the JVM may cache `running` and loop forever.
 */
public class StopFlag {

    // TODO: declare `running` as volatile
    private boolean running = false;

    private volatile long count = 0;
    private Thread worker;

    /**
     * Starts a background thread that increments count in a tight loop until stopped.
     */
    public void start() {
        // TODO: set running = true, then start a thread that loops while running is true
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Signals the background thread to stop and waits for it to terminate.
     */
    public void stop() throws InterruptedException {
        // TODO: set running = false, then join the worker thread
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns approximate number of loop iterations completed. */
    public long getCount() {
        return count;
    }

    /** Returns true if the worker thread is still alive. */
    public boolean isRunning() {
        return worker != null && worker.isAlive();
    }
}
