package com.concurrency.intermediate.p21;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 21 – BlockingQueue: Delayed Task Scheduler
 *
 * Uses a DelayQueue to execute tasks after a specified delay.
 * DelayQueue only makes an element available when its delay has expired.
 */
public class DelayedTaskScheduler {

    private final DelayQueue<DelayedTask> queue = new DelayQueue<>();
    private final AtomicInteger executedCount = new AtomicInteger(0);
    private volatile boolean running = false;
    private Thread dispatcher;

    /**
     * Schedules a task to execute after delayMs milliseconds.
     */
    public void schedule(Runnable task, long delayMs) {
        // TODO: queue.put(new DelayedTask(task, delayMs))
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Starts the background daemon dispatcher thread.
     * Loop: task = queue.take() → task.run() → executedCount++
     */
    public void start() {
        // TODO: create daemon thread that loops queue.take() until stopped
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Stops the dispatcher thread.
     */
    public void stop() throws InterruptedException {
        // TODO: set running = false, interrupt dispatcher, join it
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the number of tasks that have been executed. */
    public int getExecutedCount() {
        return executedCount.get();
    }

    // ── Inner class ──────────────────────────────────────────────────────────

    /**
     * A task that becomes available in the DelayQueue only after its deadline.
     *
     * Implement Delayed:
     *   getDelay(unit): returns remaining delay in the requested unit
     *   compareTo(other): tasks with shorter remaining delay come first
     */
    static class DelayedTask implements Delayed, Runnable {

        private final Runnable task;
        private final long deadlineNanos; // System.nanoTime() + delayMs * 1_000_000

        DelayedTask(Runnable task, long delayMs) {
            this.task = task;
            this.deadlineNanos = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(delayMs);
        }

        @Override
        public void run() {
            task.run();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            // TODO: return unit.convert(deadlineNanos - System.nanoTime(), TimeUnit.NANOSECONDS)
            throw new UnsupportedOperationException("Implement getDelay");
        }

        @Override
        public int compareTo(Delayed other) {
            // TODO: compare remaining delays; shorter delay = higher priority (comes first)
            throw new UnsupportedOperationException("Implement compareTo");
        }
    }
}
