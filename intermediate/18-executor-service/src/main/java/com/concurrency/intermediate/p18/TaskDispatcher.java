package com.concurrency.intermediate.p18;

import java.util.List;
import java.util.concurrent.*;

/**
 * Problem 18 – ExecutorService: Configurable Task Dispatcher
 *
 * Backed by a ThreadPoolExecutor with a bounded queue and CallerRunsPolicy.
 * CallerRunsPolicy provides back-pressure: if the queue is full and all
 * threads are busy, the submitting thread runs the task itself.
 */
public class TaskDispatcher {

    private final ThreadPoolExecutor executor;

    /**
     * @param coreThreads   minimum number of threads kept alive
     * @param maxThreads    maximum number of threads under load
     * @param queueCapacity maximum number of tasks waiting in the queue
     */
    public TaskDispatcher(int coreThreads, int maxThreads, int queueCapacity) {
        // TODO: create a ThreadPoolExecutor with:
        //   - ArrayBlockingQueue(queueCapacity) as work queue
        //   - CallerRunsPolicy as the rejection handler
        //   - 60-second keepAliveTime for idle threads
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Submits a task for execution.
     * If the queue is full and the pool is saturated, CallerRunsPolicy
     * causes the calling thread to run the task synchronously.
     */
    public void submit(Runnable task) {
        // TODO: executor.execute(task)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Submits all tasks and blocks until every one has completed.
     * Uses a CountDownLatch internally — does not use invokeAll().
     */
    public void submitAll(List<Runnable> tasks) throws InterruptedException {
        // TODO:
        //  CountDownLatch latch = new CountDownLatch(tasks.size())
        //  for each task: submit(() -> { task.run(); latch.countDown(); })
        //  latch.await()
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Initiates graceful shutdown and waits up to 5 seconds for tasks to finish.
     */
    public void shutdown() throws InterruptedException {
        // TODO: executor.shutdown(); executor.awaitTermination(5, TimeUnit.SECONDS)
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the number of tasks that have completed execution. */
    public long getCompletedTaskCount() {
        return executor.getCompletedTaskCount();
    }

    /** Returns the approximate number of threads actively executing tasks. */
    public int getActiveThreadCount() {
        return executor.getActiveCount();
    }
}
