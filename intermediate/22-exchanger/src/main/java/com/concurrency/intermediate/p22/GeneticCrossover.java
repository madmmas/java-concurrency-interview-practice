package com.concurrency.intermediate.p22;

import java.util.Arrays;
import java.util.concurrent.Exchanger;

/**
 * Problem 22 – Exchanger: Genetic Crossover
 *
 * Two threads representing chromosomeA and chromosomeB each offer the second
 * half of their array to the Exchanger. After the swap both chromosomes contain
 * a hybrid: the first half stays, but the second half comes from the partner.
 *
 * Example (length 6, crossover at index 3):
 *   A before: [1,2,3 | 4,5,6]    B before: [7,8,9 | 10,11,12]
 *   A after:  [1,2,3 | 10,11,12] B after:  [7,8,9 | 4,5,6]
 */
public class GeneticCrossover {

    /**
     * Performs a crossover between chromosomeA and chromosomeB at the midpoint.
     * Modifies both arrays in-place and returns them as int[][]{resultA, resultB}.
     *
     * Both arrays must have the same even length.
     *
     * @return int[][] where [0] is the modified chromosomeA, [1] is modified chromosomeB
     */
    public int[][] crossover(int[] chromosomeA, int[] chromosomeB)
            throws InterruptedException {
        // TODO:
        //  1. Validate equal even lengths
        //  2. int mid = chromosomeA.length / 2
        //  3. Create Exchanger<int[]>
        //  4. Thread A: int[] received = exchanger.exchange(Arrays.copyOfRange(chromosomeA, mid, len))
        //               System.arraycopy(received, 0, chromosomeA, mid, received.length)
        //  5. Thread B: symmetric with chromosomeB
        //  6. Join both threads
        //  7. return new int[][] { chromosomeA, chromosomeB }
        throw new UnsupportedOperationException("Implement this method");
    }
}
