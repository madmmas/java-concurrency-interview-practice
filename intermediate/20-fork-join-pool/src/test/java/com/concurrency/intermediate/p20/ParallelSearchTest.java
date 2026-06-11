package com.concurrency.intermediate.p20;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ParallelSearchTest {

    private final ParallelSearch ps = new ParallelSearch();

    @Test
    void foundInSingleElement() {
        assertEquals(0, ps.search(new int[]{42}, 42));
    }

    @Test
    void notFoundInSingleElement() {
        assertEquals(-1, ps.search(new int[]{1}, 99));
    }

    @Test
    void foundAtStart() {
        assertEquals(0, ps.search(new int[]{7, 1, 2, 3}, 7));
    }

    @Test
    void foundAtEnd() {
        int[] arr = {1, 2, 3, 4, 5};
        assertEquals(4, ps.search(arr, 5));
    }

    @Test
    void notFoundInSmallArray() {
        assertEquals(-1, ps.search(new int[]{1, 2, 3}, 99));
    }

    @Test
    void foundInLargeArray() {
        int n = 100_000;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i * 2; // even numbers
        int targetIdx = 70_000;
        int target = arr[targetIdx];
        int result = ps.search(arr, target);
        assertTrue(result >= 0 && arr[result] == target,
                "Must find target " + target + " at a valid index, got index: " + result);
    }

    @Test
    void notFoundInLargeArray() {
        int n = 50_000;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i;
        assertEquals(-1, ps.search(arr, n + 1), "Target outside range must return -1");
    }

    @Test
    void emptyArrayReturnsMinusOne() {
        assertEquals(-1, ps.search(new int[]{}, 5));
    }
}
