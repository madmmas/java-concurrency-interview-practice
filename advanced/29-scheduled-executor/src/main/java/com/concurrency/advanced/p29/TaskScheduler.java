package com.concurrency.advanced.p29;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Problem 29 – ScheduledExecutorService: Task Scheduler Façade
 *
 * Wraps a ScheduledExecutorService and tracks total completed task executions.
 */
public class TaskScheduler {

    private final ScheduledExecutorService scheduler;
    private final AtomicLong executionCount = new AtomicLong(0);

    public TaskScheduler(int threads) {
        this.scheduler = Executors.newScheduledThreadPool(threads);
    }

    /**
     * Schedules task to run once after delayMs milliseconds.
     * Increments executionCount when the task completes.
     */
    public ScheduledFuture<?> scheduleOnce(Runnable task, long delayMs) {
        // TODO: Runnable wrapped = () -> { task.run(); executionCount.incrementAndGet(); };
        //       return scheduler.schedule(wrapped, delayMs, TimeUnit.MILLISECONDS);
        throw new UnsupportedOperationException("Implement scheduleOnce()");
    }

    /**
     * Schedules task to run repeatedly at a fixed rate.
     * Each completion increments executionCount.
     *
     * @param initialDelayMs delay before first execution
     * @param periodMs       time between successive starts
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task,
                                                   long initialDelayMs,
                                                   long periodMs) {
        // TODO: scheduler.scheduleAtFixedRate(wrapped, initialDelayMs, periodMs, MILLISECONDS)
        throw new UnsupportedOperationException("Implement scheduleAtFixedRate()");
    }

    /**
     * Schedules task to run repeatedly with a fixed delay between completions.
     *
     * @param initialDelayMs delay before first execution
     * @param delayMs        delay between last completion and next start
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task,
                                                      long initialDelayMs,
                                                      long delayMs) {
        // TODO: scheduler.scheduleWithFixedDelay(wrapped, initialDelayMs, delayMs, MILLISECONDS)
        throw new UnsupportedOperationException("Implement scheduleWithFixedDelay()");
    }

    /**
     * Cancels a scheduled future without interrupting a running task.
     *
     * @return true if the task was successfully cancelled before execution
     */
    public boolean cancelTask(ScheduledFuture<?> future) {
        // TODO: return future.cancel(false);
        throw new UnsupportedOperationException("Implement cancelTask()");
    }

    /** Returns the total number of task executions that have completed. */
    public long getExecutionCount() {
        return executionCount.get();
    }

    /** Gracefully shuts down the scheduler, waiting up to 5 seconds. */
    public void shutdown() throws InterruptedException {
        // TODO: scheduler.shutdown(); scheduler.awaitTermination(5, TimeUnit.SECONDS);
        throw new UnsupportedOperationException("Implement shutdown()");
    }
}
