package com.concurrency.intermediate.p24;

import java.util.concurrent.locks.StampedLock;

/**
 * Problem 24 – StampedLock: 2D Point with Optimistic Reads
 *
 * Demonstrates the three StampedLock modes:
 *  - Write lock for move()
 *  - Optimistic read (with fallback) for distanceFromOrigin()
 *  - Read lock for getX() / getY()
 */
public class OptimisticPoint {

    private double x;
    private double y;
    private final StampedLock lock = new StampedLock();

    public OptimisticPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Moves the point to (newX, newY) under a write lock.
     */
    public void move(double newX, double newY) {
        // TODO: acquire write lock → update x, y → unlock in finally
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Returns the Euclidean distance from the origin.
     *
     * Algorithm:
     *  1. long stamp = lock.tryOptimisticRead()
     *  2. Read x and y (local copies)
     *  3. if (!lock.validate(stamp)) → fall back to read lock, re-read x and y
     *  4. return Math.sqrt(x*x + y*y)
     */
    public double distanceFromOrigin() {
        // TODO: implement with optimistic read + read-lock fallback
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns x under a read lock. */
    public double getX() {
        // TODO: read lock → return x → unlock in finally
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns y under a read lock. */
    public double getY() {
        // TODO: read lock → return y → unlock in finally
        throw new UnsupportedOperationException("Implement this method");
    }
}
