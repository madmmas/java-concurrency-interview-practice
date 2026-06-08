package com.concurrency.beginner.p14;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 14 – Thread-Safe Singleton: HolderSingleton
 * Approach: initialisation-on-demand holder (Bill Pugh)
 *
 * TODO: Implement getInstance() using the initialisation-on-demand holder (Bill Pugh) approach.
 * The constructor must increment creationCount exactly once.
 */
public class HolderSingleton {

    private static final AtomicInteger creationCount = new AtomicInteger(0);
    private final int id;

    // TODO: add the instance field (and Holder class if needed)

    private HolderSingleton() {
        // TODO: increment creationCount and assign a unique id
        throw new UnsupportedOperationException("Implement constructor");
    }

    /**
     * Returns the single instance of HolderSingleton.
     * Must be thread-safe.
     */
    public static HolderSingleton getInstance() {
        // TODO: implement the initialisation-on-demand holder (Bill Pugh) pattern
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the unique ID assigned to this instance at construction. */
    public int getId() { return id; }

    /** Returns how many times the constructor has been called (must be 1). */
    public static int getCreationCount() { return creationCount.get(); }

    /** Resets state for testing purposes — DO NOT use in production code. */
    static void resetForTesting() {
        creationCount.set(0);
        // TODO: if you hold a static instance field, reset it to null here
    }
}
