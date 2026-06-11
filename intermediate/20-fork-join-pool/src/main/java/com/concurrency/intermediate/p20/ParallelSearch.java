package com.concurrency.intermediate.p20;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Problem 20 – ForkJoinPool: Parallel Linear Search
 *
 * Finds the index of a target value in an unsorted int[] using parallel search.
 * Returns the index if found, or -1 if absent.
 * If target appears multiple times, any valid index is acceptable.
 */
public class ParallelSearch {

    private static final int THRESHOLD = 1000;

    /**
     * Entry point: searches for target in array using a ForkJoinPool.
     * @return index of target, or -1 if not found
     */
    public int search(int[] array, int target) {
        // TODO: ForkJoinPool pool = new ForkJoinPool();
        //       return pool.invoke(new SearchTask(array, 0, array.length, target))
        throw new UnsupportedOperationException("Implement this method");
    }

    // ── Inner RecursiveTask ──────────────────────────────────────────────────

    static class SearchTask extends RecursiveTask<Integer> {

        private final int[] array;
        private final int from;   // inclusive
        private final int to;     // exclusive
        private final int target;

        SearchTask(int[] array, int from, int to, int target) {
            this.array  = array;
            this.from   = from;
            this.to     = to;
            this.target = target;
        }

        @Override
        protected Integer compute() {
            // TODO:
            //  if (to - from <= THRESHOLD):
            //    linear scan from..to → return index or -1
            //  else:
            //    int mid = (from + to) / 2
            //    SearchTask left  = new SearchTask(array, from, mid, target)
            //    SearchTask right = new SearchTask(array, mid,  to,  target)
            //    left.fork()
            //    int rightResult = right.compute()
            //    int leftResult  = left.join()
            //    return leftResult >= 0 ? leftResult : rightResult
            throw new UnsupportedOperationException("Implement this method");
        }
    }
}
