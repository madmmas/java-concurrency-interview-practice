package com.concurrency.intermediate.p20;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Problem 20 – ForkJoinPool: Parallel Array Sum
 *
 * Uses divide-and-conquer to sum a large int[] in parallel.
 * Extend RecursiveTask<Long> for the inner task.
 */
public class ParallelSum {

    private static final int THRESHOLD = 2000;

    /**
     * Entry point: creates a ForkJoinPool and invokes the root task.
     * @param array the array to sum
     * @return the sum of all elements
     */
    public long sum(int[] array) {
        // TODO: ForkJoinPool pool = new ForkJoinPool();
        //       return pool.invoke(new SumTask(array, 0, array.length))
        throw new UnsupportedOperationException("Implement this method");
    }

    // ── Inner RecursiveTask ──────────────────────────────────────────────────

    static class SumTask extends RecursiveTask<Long> {

        private final int[] array;
        private final int from; // inclusive
        private final int to;   // exclusive

        SumTask(int[] array, int from, int to) {
            this.array = array;
            this.from  = from;
            this.to    = to;
        }

        @Override
        protected Long compute() {
            // TODO:
            //  if (to - from <= THRESHOLD) → sequential sum from..to
            //  else:
            //    int mid = (from + to) / 2
            //    SumTask left  = new SumTask(array, from, mid)
            //    SumTask right = new SumTask(array, mid, to)
            //    left.fork()
            //    long rightResult = right.compute()
            //    long leftResult  = left.join()
            //    return leftResult + rightResult
            throw new UnsupportedOperationException("Implement this method");
        }
    }
}
