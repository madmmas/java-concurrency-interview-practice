package com.concurrency.intermediate.p16;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Problem 16 – Semaphore: Connection Pool
 *
 * A bounded pool of (simulated) database connections.
 * At most maxConnections threads may hold a connection simultaneously.
 * Excess callers block in acquire() until a connection is released.
 *
 * Implementation guide:
 *  - Use a Semaphore(maxConnections) to gate access
 *  - Use an ArrayBlockingQueue pre-filled with connection IDs (1..maxConnections)
 *    to hand out and reclaim specific IDs
 */
public class ConnectionPool {

    private final int maxConnections;
    private final Semaphore semaphore;
    private final BlockingQueue<Integer> availableIds;

    public ConnectionPool(int maxConnections) {
        this.maxConnections = maxConnections;
        this.semaphore = new Semaphore(maxConnections, true);
        this.availableIds = new ArrayBlockingQueue<>(maxConnections);
        for (int i = 1; i <= maxConnections; i++) {
            availableIds.add(i);
        }
    }

    /**
     * Acquires a connection from the pool.
     * Blocks if all connections are currently in use.
     *
     * @return a unique connection ID (1..maxConnections)
     */
    public int acquire() throws InterruptedException {
        // TODO:
        //  1. semaphore.acquire()       — blocks until a permit is free
        //  2. return availableIds.poll() — take the next available ID
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Returns a connection back to the pool.
     * Must be called in a finally block to avoid leaking connections.
     *
     * @param connectionId the ID returned by acquire()
     */
    public void release(int connectionId) {
        // TODO:
        //  1. availableIds.offer(connectionId) — reclaim the ID
        //  2. semaphore.release()              — return the permit
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the number of currently available (free) connections. */
    public int availableConnections() {
        return semaphore.availablePermits();
    }

    /** Returns the maximum pool capacity. */
    public int getMaxConnections() {
        return maxConnections;
    }
}
