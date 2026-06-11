package com.concurrency.intermediate.p20;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ParallelSumTest {

    private final ParallelSum ps = new ParallelSum();

    @Test
    void emptyArray() {
        assertEquals(0L, ps.sum(new int[]{}));
    }

    @Test
    void singleElement() {
        assertEquals(42L, ps.sum(new int[]{42}));
    }

    @Test
    void smallArray() {
        assertEquals(15L, ps.sum(new int[]{1, 2, 3, 4, 5}));
    }

    @Test
    void arrayBelowThreshold() {
        int[] arr = new int[1000];
        long expected = 0;
        for (int i = 0; i < arr.length; i++) { arr[i] = i + 1; expected += (i + 1); }
        assertEquals(expected, ps.sum(arr));
    }

    @Test
    void largeArrayUsesParallelism() {
        int n = 100_000;
        int[] arr = new int[n];
        long expected = 0;
        for (int i = 0; i < n; i++) { arr[i] = i + 1; expected += (i + 1); }
        assertEquals(expected, ps.sum(arr),
                "Parallel sum must be exact for large arrays");
    }

    @Test
    void arrayWithNegatives() {
        assertEquals(0L, ps.sum(new int[]{-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5}));
    }

    @Test
    void randomLargeArray() {
        Random rng = new Random(42);
        int n = 50_000;
        int[] arr = new int[n];
        long expected = 0;
        for (int i = 0; i < n; i++) { arr[i] = rng.nextInt(1000); expected += arr[i]; }
        assertEquals(expected, ps.sum(arr));
    }
}
