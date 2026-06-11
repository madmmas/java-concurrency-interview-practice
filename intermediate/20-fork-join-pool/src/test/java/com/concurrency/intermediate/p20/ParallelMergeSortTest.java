package com.concurrency.intermediate.p20;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ParallelMergeSortTest {

    private final ParallelMergeSort sorter = new ParallelMergeSort();

    @Test
    void emptyArray() {
        int[] arr = {};
        sorter.sort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void singleElement() {
        int[] arr = {7};
        sorter.sort(arr);
        assertArrayEquals(new int[]{7}, arr);
    }

    @Test
    void alreadySorted() {
        int[] arr = {1, 2, 3, 4, 5};
        sorter.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void reverseOrder() {
        int[] arr = {5, 4, 3, 2, 1};
        sorter.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void duplicateElements() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5};
        int[] expected = arr.clone();
        Arrays.sort(expected);
        sorter.sort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void largeRandomArray() {
        Random rng = new Random(123);
        int n = 100_000;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = rng.nextInt(1_000_000);
        int[] expected = arr.clone();
        Arrays.sort(expected);
        sorter.sort(arr);
        assertArrayEquals(expected, arr, "Parallel merge sort must produce same result as Arrays.sort");
    }

    @Test
    void negativeNumbers() {
        int[] arr = {-3, 5, -1, 0, 2, -7};
        int[] expected = arr.clone();
        Arrays.sort(expected);
        sorter.sort(arr);
        assertArrayEquals(expected, arr);
    }
}
