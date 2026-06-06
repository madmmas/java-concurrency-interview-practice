package com.concurrency.beginner.p02;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Problem 02 - Runnable vs Thread
 *
 * Implement methods that use Runnable (not Thread subclasses) to execute tasks.
 */
public class TaskRunner {

    /**
     * Runs the given task on a new thread with the specified name.
     * Blocks until the thread completes before returning.
     *
     * @param task       the task to execute
     * @param threadName name for the new thread
     */
    public void runTask(Runnable task, String threadName) throws InterruptedException {
        Thread thread = new Thread(task, threadName);
        thread.start();
        thread.join();
    }

    /**
     * Runs all tasks in parallel, each on its own thread named "worker-0", "worker-1", etc.
     * Blocks until ALL tasks have completed before returning.
     *
     * @param tasks list of tasks to run concurrently
     */
    public void runTasksParallel(List<Runnable> tasks) throws InterruptedException {
        int i = 0;
        for(Runnable task: tasks) {
            Thread thread = new Thread(task, "worker-" + i);
            thread.start();
            thread.join();
            i++;
        }
    }

    /**
     * Returns a Runnable that, when run, adds `value` to `collector` exactly `times` times.
     *
     * @param collector a thread-safe list to add values into
     * @param value     the integer to add
     * @param times     how many times to add it
     * @return a Runnable implementing the described behavior
     */
    public Runnable buildCountingRunnable(List<Integer> collector, int value, int times) {
        return () -> {
            IntStream.range(0, times).forEach(i -> collector.add(value));
        };
    }
}
