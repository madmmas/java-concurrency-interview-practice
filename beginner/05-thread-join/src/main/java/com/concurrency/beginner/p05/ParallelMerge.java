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
        if(array == null || array.length == 0) return 0;

        final long[] partialSums = new long[2];

        int mid = array.length / 2;

        Thread t1 = new Thread(() -> {
            for(int i = 0; i < mid; i++) {
                partialSums[0] += array[i];
            }
        });

        Thread t2 = new Thread(() -> {
            for(int i = mid; i < array.length; i++) {
                partialSums[1] += array[i];
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        return partialSums[0] + partialSums[1];
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
        if(array == null || array.length == 0) {
            throw new IllegalArgumentException("Array must not be null or empty");
        }

        final int[] partialMax = new int[2];

        int mid = array.length / 2;

        Thread t1 = new Thread(() -> {
            int max = array[0];
            for(int i = 1; i < mid; i++) {
                max = Math.max(max, array[i]);
            }
            partialMax[0] = max;
        });

        Thread t2 = new Thread(() -> {
            int max = array[mid];
            for(int i = mid + 1; i < array.length; i++) {
                max = Math.max(max, array[i]);
            }
            partialMax[1] = max;
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        return Math.max(partialMax[0], partialMax[1]);
    }

    /**
     * Runs `task` on a new thread and waits up to `timeoutMillis` for it to complete.
     *
     * @param task          the task to run
     * @param timeoutMillis maximum wait time in milliseconds
     * @return true if the task completed within the timeout; false if it timed out
     */
    public boolean runWithTimeout(Runnable task, long timeoutMillis) throws InterruptedException {

        Thread thread = new Thread(task);
        thread.start();
        thread.join(timeoutMillis);

        return !thread.isAlive();
    }
}
