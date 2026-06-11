package com.concurrency.intermediate.p17;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Problem 17 – CyclicBarrier: Parallel Matrix Multiplier
 *
 * Multiplies two n×n integer matrices using n*n worker threads.
 * Each thread computes exactly one cell of the result matrix.
 * A CyclicBarrier synchronises all workers: the caller blocks until
 * every cell has been computed.
 */
public class ParallelMatrixMultiplier {

    /**
     * Multiplies matrix a by matrix b and returns the result.
     * Both matrices must be square and the same size.
     *
     * Uses one thread per result cell. A CyclicBarrier ensures all
     * cells are computed before the result is returned.
     *
     * @param a n×n matrix
     * @param b n×n matrix
     * @return  n×n result matrix (a × b)
     */
    public int[][] multiply(int[][] a, int[][] b) throws InterruptedException, BrokenBarrierException {
        // TODO:
        //  1. Validate that a and b are square and same size
        //  2. Create result[n][n]
        //  3. Create a CyclicBarrier(n*n) — barrier action can be a no-op
        //  4. Launch n*n threads; thread (i,j) computes result[i][j]
        //  5. await() the barrier (or join all threads) and return result
        throw new UnsupportedOperationException("Implement this method");
    }
}
