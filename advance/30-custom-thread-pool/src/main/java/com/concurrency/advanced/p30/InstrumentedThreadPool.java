package com.concurrency.advanced.p30;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Problem 30 – Custom ThreadPool: Instrumented Thread Pool
 *
 * Extends ThreadPoolExecutor and overrides the three hook methods to add:
 *  - Per-task latency tracking (min / max / average)
 *  - Total tasks run counter (includes tasks that threw exceptions)
 *  - Termination timestamp
 *
 * The ThreadLocal<Long> stores each worker thread's task start time so that
 * afterExecute() can compute elapsed time without shared-state contention.
 */
public class InstrumentedThreadPool extends ThreadPoolExecutor {

    /** Stores System.nanoTime() at the moment a task starts on a worker thread. */
    private final ThreadLocal<Long> taskStartTime = new ThreadLocal<>();

    // Latency accumulators (nanoseconds internally; exposed as milliseconds)
    private final AtomicLong totalLatencyNanos = new AtomicLong(0);
    private final AtomicLong minLatencyNanos   = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxLatencyNanos   = new AtomicLong(0);
    private final AtomicLong totalTasksRun     = new AtomicLong(0);

    /** Set by terminated(); -1 until the pool fully terminates. */
    private volatile long terminatedAt = -1;

    public InstrumentedThreadPool(int corePoolSize, int maximumPoolSize,
                                   long keepAliveTime, TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * Called by the worker thread immediately BEFORE executing task r.
     * Record the start time so afterExecute() can compute elapsed duration.
     *
     * Always call super.beforeExecute(t, r) first.
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        // TODO: taskStartTime.set(System.nanoTime());
        throw new UnsupportedOperationException("Implement beforeExecute()");
    }

    /**
     * Called by the worker thread immediately AFTER executing task r.
     *
     * Steps:
     *  1. Compute elapsed = System.nanoTime() - taskStartTime.get()
     *  2. taskStartTime.remove()  ← prevents memory leak in thread pools
     *  3. totalLatencyNanos.addAndGet(elapsed)
     *  4. totalTasksRun.incrementAndGet()
     *  5. Update minLatencyNanos: updateAndGet(cur -> Math.min(cur, elapsed))
     *  6. Update maxLatencyNanos: updateAndGet(cur -> Math.max(cur, elapsed))
     *
     * Always call super.afterExecute(r, t) last.
     * Note: parameter t is the Throwable thrown by the task (null if no exception).
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        // TODO: implement steps 1–6 above
        super.afterExecute(r, t);
        throw new UnsupportedOperationException("Implement afterExecute()");
    }

    /**
     * Called once when the pool has fully terminated (all workers have stopped).
     * Record the wall-clock termination timestamp.
     *
     * Always call super.terminated().
     */
    @Override
    protected void terminated() {
        // TODO: terminatedAt = System.currentTimeMillis();
        super.terminated();
        throw new UnsupportedOperationException("Implement terminated()");
    }

    // ── Metrics accessors ─────────────────────────────────────────────────────

    /**
     * Average task execution time in milliseconds.
     * Returns 0.0 if no tasks have run yet.
     */
    public double getAverageLatencyMs() {
        long tasks = totalTasksRun.get();
        if (tasks == 0) return 0.0;
        return totalLatencyNanos.get() / (double) tasks / 1_000_000.0;
    }

    /**
     * Minimum task execution time in milliseconds.
     * Returns 0.0 if no tasks have run yet.
     */
    public double getMinLatencyMs() {
        long min = minLatencyNanos.get();
        return min == Long.MAX_VALUE ? 0.0 : min / 1_000_000.0;
    }

    /**
     * Maximum task execution time in milliseconds.
     * Returns 0.0 if no tasks have run yet.
     */
    public double getMaxLatencyMs() {
        return maxLatencyNanos.get() / 1_000_000.0;
    }

    /**
     * Total number of tasks that have run, including those that threw exceptions.
     */
    public long getTotalTasksRun() {
        return totalTasksRun.get();
    }

    /**
     * System.currentTimeMillis() at the moment the pool fully terminated.
     * Returns -1 if the pool has not yet terminated.
     */
    public long getTerminatedAt() {
        return terminatedAt;
    }
}
