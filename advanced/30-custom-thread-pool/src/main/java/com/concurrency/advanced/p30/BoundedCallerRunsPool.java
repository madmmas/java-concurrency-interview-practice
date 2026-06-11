package com.concurrency.advanced.p30;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Problem 30 – Custom ThreadPool: Bounded Pool with Caller-Runs Back-Pressure
 *
 * A ThreadPoolExecutor with an ArrayBlockingQueue and a custom rejection handler
 * that runs rejected tasks on the calling thread and tracks how many times this
 * happens. This provides natural back-pressure: when the system is saturated, the
 * producer (caller) is slowed down rather than tasks being dropped or an exception
 * being thrown.
 */
public class BoundedCallerRunsPool {

    private final ThreadPoolExecutor executor;

    /** How many tasks have been run on a caller thread due to queue saturation. */
    private final AtomicLong callerRunCount = new AtomicLong(0);

    /**
     * Creates the pool.
     *
     * @param coreThreads    number of threads always kept alive
     * @param maxThreads     maximum number of threads under load
     * @param queueCapacity  maximum tasks waiting in the queue before caller-runs kicks in
     */
    public BoundedCallerRunsPool(int coreThreads, int maxThreads, int queueCapacity) {
        // TODO: create a ThreadPoolExecutor with:
        //   - new ArrayBlockingQueue<>(queueCapacity) as the work queue
        //   - 60s keepAliveTime
        //   - A custom RejectedExecutionHandler:
        //       (r, exec) -> { callerRunCount.incrementAndGet(); r.run(); }
        //
        // Example:
        //   this.executor = new ThreadPoolExecutor(
        //       coreThreads, maxThreads,
        //       60L, TimeUnit.SECONDS,
        //       new ArrayBlockingQueue<>(queueCapacity),
        //       (r, exec) -> { callerRunCount.incrementAndGet(); r.run(); }
        //   );
        throw new UnsupportedOperationException("Implement constructor");
    }

    /**
     * Submits a task for execution on a pool thread.
     * If the pool and queue are both saturated, the custom rejection handler
     * runs the task on the calling thread and increments callerRunCount.
     */
    public void submit(Runnable task) {
        // TODO: executor.execute(task);
        throw new UnsupportedOperationException("Implement submit()");
    }

    /**
     * Returns how many tasks have been run on a caller (submitter) thread
     * because the pool and queue were both full.
     */
    public long getCallerRunCount() {
        return callerRunCount.get();
    }

    /**
     * Returns the total number of tasks that have completed execution on pool threads.
     * Does NOT include tasks that ran on caller threads via the rejection handler.
     */
    public long getCompletedTaskCount() {
        return executor.getCompletedTaskCount();
    }

    /** Gracefully shuts down the pool, waiting up to 5 seconds. */
    public void shutdown() throws InterruptedException {
        // TODO: executor.shutdown(); executor.awaitTermination(5, TimeUnit.SECONDS);
        throw new UnsupportedOperationException("Implement shutdown()");
    }
}
