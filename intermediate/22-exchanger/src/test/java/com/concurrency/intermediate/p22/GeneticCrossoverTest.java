package com.concurrency.intermediate.p22;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class GeneticCrossoverTest {

    private final GeneticCrossover crossover = new GeneticCrossover();

    @Test
    void basicCrossoverSwapsSecondHalves() throws InterruptedException {
        int[] a = {1, 2, 3, 4, 5, 6};
        int[] b = {7, 8, 9, 10, 11, 12};
        int[][] result = crossover.crossover(a, b);

        assertArrayEquals(new int[]{1, 2, 3, 10, 11, 12}, result[0],
                "ChromosomeA second half should come from chromosomeB");
        assertArrayEquals(new int[]{7, 8, 9, 4, 5, 6}, result[1],
                "ChromosomeB second half should come from chromosomeA");
    }

    @Test
    void crossoverWithLengthTwo() throws InterruptedException {
        int[] a = {1, 2};
        int[] b = {3, 4};
        int[][] result = crossover.crossover(a, b);
        assertArrayEquals(new int[]{1, 4}, result[0]);
        assertArrayEquals(new int[]{3, 2}, result[1]);
    }

    @Test
    void firstHalfIsUnchanged() throws InterruptedException {
        int[] a = {10, 20, 30, 40};
        int[] b = {50, 60, 70, 80};
        int[][] result = crossover.crossover(a, b);
        // First halves must be preserved
        assertEquals(10, result[0][0]);
        assertEquals(20, result[0][1]);
        assertEquals(50, result[1][0]);
        assertEquals(60, result[1][1]);
    }

    @Test
    void crossoverWithLargeArrays() throws InterruptedException {
        int n = 1000;
        int[] a = new int[n], b = new int[n];
        for (int i = 0; i < n; i++) { a[i] = i; b[i] = i + n; }
        int[][] result = crossover.crossover(a, b);
        // Check midpoint boundaries
        assertEquals(n / 2 - 1, result[0][n / 2 - 1], "Last element of first half in A must be unchanged");
        assertEquals(n + n / 2,  result[0][n / 2],     "First element of second half in A must come from B");
        assertEquals(n / 2,      result[1][n / 2],     "First element of second half in B must come from A");
    }

    @Test
    void crossoverModifiesOriginalArrays() throws InterruptedException {
        int[] a = {1, 2, 3, 4};
        int[] b = {5, 6, 7, 8};
        crossover.crossover(a, b);
        // The returned arrays are the same references as the inputs (modified in-place)
        assertArrayEquals(new int[]{1, 2, 7, 8}, a);
        assertArrayEquals(new int[]{5, 6, 3, 4}, b);
    }
}
