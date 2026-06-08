package com.concurrency.beginner.p08;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Problem 08 – Thread Interruption: Interruptible Worker
 *
 * A counting worker that can be cancelled cooperatively via interruption.
 */
public class InterruptibleWorker {

    private final AtomicLong count = new AtomicLong(0);
    private volatile boolean interrupted = false;
    private Thread workerThread;

    /**
     * Starts a thread that counts in a tight loop for up to durationMs milliseconds.
     * The loop must check the interrupt flag on every iteration.
     *
     * @param durationMs natural deadline; the thread stops on its own after this time
     */
    public void startCounting(long durationMs) {
        // TODO: create and start a thread that:
        //  - records start time
        //  - loops while NOT interrupted AND time not exceeded
        //  - increments count each iteration
        //  - if InterruptedException is caught (from sleep or other blocking call),
        //    sets interrupted = true and returns
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Interrupts the worker thread immediately.
     * After calling this, the worker should exit its loop promptly.
     */
    public void cancel() throws InterruptedException {
        // TODO: interrupt the thread, then join it
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the number of loop iterations completed before stopping. */
    public long getCount() {
        return count.get();
    }

    /**
     * Returns true if the thread was stopped due to interruption
     * (either via isInterrupted() flag check or caught InterruptedException).
     */
    public boolean wasInterrupted() {
        return interrupted;
    }
}
