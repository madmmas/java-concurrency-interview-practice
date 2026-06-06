package com.concurrency.beginner.p05;

/**
 * Problem 05 - Thread Join
 *
 * Use Thread.join() to collect results from parallel computations.
 */
public class ParallelMerge {

    /**
     * Computes the sum of all elements in `array` by:
     *  - Splitting the array in half
     *  - Summing each half on a separate thread
     *  - Joining both threads
     *  - Returning the combined total
     *
     * For arrays of length 0 or 1, handle the edge case directly.
     *
     * @param array the input array (non-null)
     * @return the total sum of all elements
     */
    public long sumArray(int[] array) throws InterruptedException {
        // TODO: implement parallel sum using two threads + join
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Finds the maximum element in `array` by:
     *  - Splitting the array in half
     *  - Finding the max of each half on a separate thread
     *  - Joining both threads
     *  - Returning the overall max
     *
     * @param array non-null, non-empty array
     * @return the maximum value in the array
     */
    public int findMax(int[] array) throws InterruptedException {
        // TODO: implement parallel max using two threads + join
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Runs `task` on a new thread and waits up to `timeoutMillis` for it to complete.
     *
     * @param task          the task to run
     * @param timeoutMillis maximum wait time in milliseconds
     * @return true if the task completed within the timeout; false if it timed out
     */
    public boolean runWithTimeout(Runnable task, long timeoutMillis) throws InterruptedException {
        // TODO: start thread, join with timeout, check isAlive()
        throw new UnsupportedOperationException("Implement this method");
    }
}
