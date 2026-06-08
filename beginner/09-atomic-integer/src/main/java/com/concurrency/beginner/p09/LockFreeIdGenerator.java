package com.concurrency.beginner.p09;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 09 – AtomicInteger: Lock-Free ID Generator
 *
 * Issues unique, monotonically increasing IDs starting from 1.
 * Must be thread-safe using only AtomicInteger — no synchronized.
 */
public class LockFreeIdGenerator {

    // TODO: initialise an AtomicInteger counter starting at 0
    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Returns the next unique ID. IDs start at 1 and increase by 1 each call.
     * Thread-safe: concurrent calls must never return the same ID.
     */
    public int nextId() {
        // TODO: one-liner using incrementAndGet()
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the last issued ID, or 0 if nextId() has never been called. */
    public int currentId() {
        return counter.get();
    }
}
