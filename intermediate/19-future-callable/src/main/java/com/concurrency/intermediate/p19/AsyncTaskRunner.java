package com.concurrency.intermediate.p19;

import java.util.List;
import java.util.concurrent.*;

/**
 * Problem 19 – Future & Callable: Async Task Runner
 *
 * Demonstrates submitting Callables, collecting Future results,
 * timeout handling, and first-completed selection.
 */
public class AsyncTaskRunner<T> {

    private final ExecutorService executor;

    public AsyncTaskRunner(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
    }

    /**
     * Submits a single task and returns its Future.
     */
    public Future<T> submitTask(Callable<T> task) {
        // TODO: return executor.submit(task)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Submits all tasks, waits for all to complete, and returns results in
     * the same order as the input list. Propagates the first exception encountered.
     *
     * @throws ExecutionException   if any task threw an exception
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public List<T> runAll(List<Callable<T>> tasks) throws InterruptedException, ExecutionException {
        // TODO:
        //  1. Submit each task → collect List<Future<T>>
        //  2. Call future.get() on each in order → collect results
        //  3. Return results list
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Runs the task and returns its result.
     *
     * @throws TimeoutException     if the task does not complete within timeoutMs
     * @throws ExecutionException   if the task threw an exception
     * @throws InterruptedException if interrupted while waiting
     */
    public T runWithTimeout(Callable<T> task, long timeoutMs)
            throws InterruptedException, ExecutionException, TimeoutException {
        // TODO: submit task, then future.get(timeoutMs, MILLISECONDS)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Submits all tasks and returns the result of whichever finishes first.
     * Cancels all remaining futures after the first result is obtained.
     *
     * Hint: Use ExecutorCompletionService to efficiently retrieve the first done future.
     *
     * @throws ExecutionException   if the winning task threw an exception
     * @throws InterruptedException if interrupted
     */
    public T runFirstCompleted(List<Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        // TODO:
        //  ExecutorCompletionService<T> cs = new ExecutorCompletionService<>(executor)
        //  submit all tasks → collect futures
        //  T result = cs.take().get()   ← first completed
        //  cancel all remaining futures
        //  return result
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Shuts down the underlying executor. */
    public void shutdown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}
