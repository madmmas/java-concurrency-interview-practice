package com.concurrency.intermediate.p18;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Problem 18 – ExecutorService: Priority-Based Executor
 *
 * Executes tasks in priority order (higher int value = executes first).
 * Tasks with equal priority are executed in submission order (FIFO).
 *
 * Implementation guide:
 *  - Use PriorityBlockingQueue<PriorityTask> as the work queue
 *  - Wrap each (Runnable, priority) pair in a PriorityTask that implements
 *    Comparable so the queue orders them correctly
 *  - Use a sequence counter to break ties between equal priorities (lower seq = first)
 */
public class PrioritizedExecutor {

    private final ThreadPoolExecutor executor;
    private final AtomicLong sequencer = new AtomicLong(0);

    public PrioritizedExecutor(int threads) {
        // TODO: create ThreadPoolExecutor with PriorityBlockingQueue
        //   corePoolSize = threads, maximumPoolSize = threads
        //   keepAliveTime = 0, workQueue = new PriorityBlockingQueue<>()
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Submits a task with the given priority.
     * Higher priority values run before lower ones.
     * Equal priorities run in submission order.
     *
     * @param task     the work to execute
     * @param priority execution priority (higher = sooner)
     */
    public void submit(Runnable task, int priority) {
        // TODO: executor.execute(new PriorityTask(task, priority, sequencer.getAndIncrement()))
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Initiates graceful shutdown. */
    public void shutdown() {
        executor.shutdown();
    }

    /** Awaits termination. */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

    // ── Inner class ──────────────────────────────────────────────────────────

    /**
     * A Runnable wrapper that carries priority and sequence information.
     * Ordering: higher priority first; equal priority → lower sequence first.
     */
    static class PriorityTask implements Runnable, Comparable<PriorityTask> {

        private final Runnable task;
        private final int priority;
        private final long sequence;

        PriorityTask(Runnable task, int priority, long sequence) {
            this.task     = task;
            this.priority = priority;
            this.sequence = sequence;
        }

        @Override
        public void run() {
            task.run();
        }

        @Override
        public int compareTo(PriorityTask other) {
            // TODO: higher priority first; break ties by lower sequence first
            throw new UnsupportedOperationException("Implement compareTo");
        }
    }
}
