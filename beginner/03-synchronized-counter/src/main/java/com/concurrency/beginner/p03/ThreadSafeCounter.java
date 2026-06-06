package com.concurrency.beginner.p03;

/**
 * Problem 03 - Synchronized Counter (Thread-Safe version)
 *
 * Implement all methods so they are safe to call from multiple threads simultaneously.
 * Use the `synchronized` keyword (do NOT use AtomicInteger for this exercise).
 */
public class ThreadSafeCounter {

    private int count = 0;

    /** Increments the counter by 1. */
    public void increment() {
        // TODO: synchronize and increment
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Decrements the counter by 1. */
    public void decrement() {
        // TODO: synchronize and decrement
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the current count. Must reflect all completed increments/decrements. */
    public int getCount() {
        // TODO: synchronize and return count
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Resets the counter to 0. */
    public void reset() {
        // TODO: synchronize and reset
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Increments the counter by `delta` as a single atomic operation.
     * @param delta the amount to add (can be negative)
     */
    public void incrementBy(int delta) {
        // TODO: synchronize and add delta
        throw new UnsupportedOperationException("Implement this method");
    }
}
