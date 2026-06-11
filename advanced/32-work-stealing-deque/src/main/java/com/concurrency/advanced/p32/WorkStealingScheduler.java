package com.concurrency.advanced.p32;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Problem 32 – Work-Stealing Deque: Scheduler
 *
 * Each worker thread owns one WorkStealingDeque.
 * Workers first pop from their own deque (locality); if empty they steal
 * from a random other worker's deque.
 *
 * submit() distributes tasks to the least-loaded worker's deque.
 */
public class WorkStealingScheduler {

    private final int numWorkers;
    private final WorkStealingDeque<Runnable>[] deques;
    private final Thread[] workers;

    private volatile boolean running = false;

    private final AtomicLong completedCount = new AtomicLong(0);
    private final AtomicLong stolenCount    = new AtomicLong(0);

    @SuppressWarnings("unchecked")
    public WorkStealingScheduler(int numWorkers) {
        this.numWorkers = numWorkers;
        this.deques  = new WorkStealingDeque[numWorkers];
        this.workers = new Thread[numWorkers];
        for (int i = 0; i < numWorkers; i++) {
            deques[i] = new WorkStealingDeque<>();
        }
    }

    /**
     * Starts all worker threads.
     *
     * Each worker thread (index i) loops:
     *   1. task = deques[i].pop()        → own deque (LIFO)
     *   2. if task == null:
     *        pick a random victim j ≠ i
     *        task = deques[j].steal()    → steal from victim (FIFO)
     *        if task != null: stolenCount++
     *   3. if task != null: task.run(); completedCount++
     *   4. else: Thread.yield()
     *   5. loop while running || any deque is non-empty
     */
    public void start() {
        // TODO: set running = true, create and start numWorkers threads
        throw new UnsupportedOperationException("Implement start()");
    }

    /**
     * Submits a task to the deque of the worker with the fewest tasks.
     * Thread-safe: may be called by any thread (including non-worker threads).
     */
    public void submit(Runnable task) {
        // TODO: find the deque with the smallest size() and push the task
        throw new UnsupportedOperationException("Implement submit()");
    }

    /**
     * Signals workers to finish, waits until all deques are drained and all
     * worker threads terminate.
     */
    public void shutdown() throws InterruptedException {
        // TODO: running = false; join all worker threads
        throw new UnsupportedOperationException("Implement shutdown()");
    }

    /** Total tasks completed across all worker threads (own + stolen). */
    public long getCompletedCount() {
        return completedCount.get();
    }

    /**
     * Tasks that were completed via stealing (not by the deque's own worker).
     */
    public long getStolenCount() {
        return stolenCount.get();
    }
}
