package com.concurrency.beginner.p08;

/**
 * Problem 08 – Thread Interruption: Graceful Sleeper
 *
 * Demonstrates proper InterruptedException handling.
 * Tracks whether sleep completed normally or was interrupted.
 */
public class GracefulSleeper {

    private volatile boolean completedNormally = false;
    private volatile boolean gotInterrupted = false;
    private Thread sleeperThread;

    /**
     * Starts a thread that sleeps for the given duration.
     * If interrupted mid-sleep, it must:
     *  1. Set gotInterrupted = true
     *  2. Restore the interrupt flag via Thread.currentThread().interrupt()
     *  3. Return cleanly (do not rethrow in this exercise)
     * If sleep completes normally, set completedNormally = true.
     *
     * @param millis duration to sleep
     */
    public void sleepFor(long millis) {
        // TODO: create and start a thread that sleeps for millis ms
        //       and correctly handles InterruptedException
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Interrupts the sleeping thread.
     * Blocks until the thread terminates.
     */
    public void interrupt() throws InterruptedException {
        // TODO: call interrupt() on sleeperThread, then join it
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Waits for the sleeper to finish naturally (used in tests without interruption). */
    public void join() throws InterruptedException {
        if (sleeperThread != null) sleeperThread.join();
    }

    /** True if sleep ran to completion without being interrupted. */
    public boolean didCompleteNormally() {
        return completedNormally;
    }

    /** True if an InterruptedException was caught during sleep. */
    public boolean didGetInterrupted() {
        return gotInterrupted;
    }
}
