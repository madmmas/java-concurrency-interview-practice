package com.concurrency.intermediate.p20;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Problem 20 – ForkJoinPool: Parallel Merge Sort
 *
 * Sorts an int[] in-place using parallel divide-and-conquer.
 * Extend RecursiveAction (no return value) for the inner task.
 */
public class ParallelMergeSort {

    private static final int THRESHOLD = 2000;

    /**
     * Entry point: sorts the array in-place using a ForkJoinPool.
     */
    public void sort(int[] array) {
        // TODO: ForkJoinPool pool = new ForkJoinPool();
        //       pool.invoke(new SortTask(array, 0, array.length))
        throw new UnsupportedOperationException("Implement this method");
    }

    // ── Inner RecursiveAction ────────────────────────────────────────────────

    static class SortTask extends RecursiveAction {

        private final int[] array;
        private final int from; // inclusive
        private final int to;   // exclusive

        SortTask(int[] array, int from, int to) {
            this.array = array;
            this.from  = from;
            this.to    = to;
        }

        @Override
        protected void compute() {
            // TODO:
            //  if (to - from <= THRESHOLD) → Arrays.sort(array, from, to)
            //  else:
            //    int mid = (from + to) / 2
            //    SortTask left  = new SortTask(array, from, mid)
            //    SortTask right = new SortTask(array, mid, to)
            //    left.fork()
            //    right.compute()
            //    left.join()
            //    merge(array, from, mid, to)
            throw new UnsupportedOperationException("Implement this method");
        }

        /**
         * Merges two sorted subarrays [from, mid) and [mid, to) in-place.
         * Uses a temporary array for the merge buffer.
         */
        static void merge(int[] array, int from, int mid, int to) {
            // TODO: standard merge algorithm using a temp[] buffer
            throw new UnsupportedOperationException("Implement merge");
        }
    }
}
