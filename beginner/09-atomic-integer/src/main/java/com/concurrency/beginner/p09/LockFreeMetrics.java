package com.concurrency.beginner.p09;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Problem 09 – AtomicInteger: Lock-Free Metrics Collector
 *
 * Thread-safe metrics using only atomic variables — NO synchronized keyword.
 */
public class LockFreeMetrics {

    // TODO: declare AtomicInteger/AtomicLong fields for count, totalLatency, min, max
    private final AtomicLong count        = new AtomicLong(0);
    private final AtomicLong totalLatency = new AtomicLong(0);
    private final AtomicInteger minLatency = new AtomicInteger(Integer.MAX_VALUE);
    private final AtomicInteger maxLatency = new AtomicInteger(Integer.MIN_VALUE);

    /**
     * Records a single latency measurement.
     * Must be thread-safe without using synchronized.
     *
     * @param ms latency in milliseconds
     */
    public void recordLatency(int ms) {
        // TODO:
        //  1. Increment count
        //  2. Add ms to totalLatency
        //  3. Update minLatency using CAS loop
        //  4. Update maxLatency using CAS loop
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the total number of recordings. */
    public long getTotalCount() {
        return count.get();
    }

    /** Returns the sum of all recorded latencies. */
    public long getTotalLatency() {
        return totalLatency.get();
    }

    /** Returns the minimum recorded latency, or Integer.MAX_VALUE if no recordings. */
    public int getMinLatency() {
        return minLatency.get();
    }

    /** Returns the maximum recorded latency, or Integer.MIN_VALUE if no recordings. */
    public int getMaxLatency() {
        return maxLatency.get();
    }

    /** Resets all metrics to initial state. */
    public void reset() {
        count.set(0);
        totalLatency.set(0);
        minLatency.set(Integer.MAX_VALUE);
        maxLatency.set(Integer.MIN_VALUE);
    }
}
