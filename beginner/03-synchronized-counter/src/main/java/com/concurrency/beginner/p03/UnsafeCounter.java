package com.concurrency.beginner.p03;

/**
 * Problem 03 - Unsafe Counter (intentionally NOT thread-safe)
 *
 * Implement this counter WITHOUT any synchronization.
 * It is used in tests to demonstrate that race conditions cause incorrect results.
 * This is an example of what NOT to do in production code.
 */
public class UnsafeCounter {

    private int count = 0;

    /** Increments without synchronization — intentionally unsafe. */
    public void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public void reset() {
        count = 0;
    }
}
