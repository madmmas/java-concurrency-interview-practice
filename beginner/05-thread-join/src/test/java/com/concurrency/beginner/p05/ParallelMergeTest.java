package com.concurrency.beginner.p05;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ParallelMergeTest {

    private final ParallelMerge pm = new ParallelMerge();

    // ---- sumArray ----

    @Test
    void sumEmptyArray() throws InterruptedException {
        assertEquals(0L, pm.sumArray(new int[]{}));
    }

    @Test
    void sumSingleElement() throws InterruptedException {
        assertEquals(7L, pm.sumArray(new int[]{7}));
    }

    @Test
    void sumSmallArray() throws InterruptedException {
        assertEquals(15L, pm.sumArray(new int[]{1, 2, 3, 4, 5}));
    }

    @Test
    void sumLargeArray() throws InterruptedException {
        int[] arr = new int[10_000];
        long expected = 0;
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i + 1;
            expected += (i + 1);
        }
        assertEquals(expected, pm.sumArray(arr));
    }

    @Test
    void sumNegativeNumbers() throws InterruptedException {
        assertEquals(-6L, pm.sumArray(new int[]{-1, -2, -3}));
    }

    // ---- findMax ----

    @Test
    void maxSingleElement() throws InterruptedException {
        assertEquals(42, pm.findMax(new int[]{42}));
    }

    @Test
    void maxSmallArray() throws InterruptedException {
        assertEquals(9, pm.findMax(new int[]{3, 9, 1, 7, 2}));
    }

    @Test
    void maxWithNegatives() throws InterruptedException {
        assertEquals(-1, pm.findMax(new int[]{-5, -1, -3, -9}));
    }

    @Test
    void maxAllSame() throws InterruptedException {
        assertEquals(4, pm.findMax(new int[]{4, 4, 4, 4}));
    }

    // ---- runWithTimeout ----

    @Test
    void taskCompletesWithinTimeout() throws InterruptedException {
        Runnable fastTask = () -> {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        };
        assertTrue(pm.runWithTimeout(fastTask, 2000),
                "Fast task should complete within the timeout");
    }

    @Test
    void taskExceedsTimeout() throws InterruptedException {
        Runnable slowTask = () -> {
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        };
        assertFalse(pm.runWithTimeout(slowTask, 200),
                "Slow task should not complete within the short timeout");
    }

    @Test
    void timeoutDoesNotBlockCallerForever() throws InterruptedException {
        long start = System.currentTimeMillis();
        pm.runWithTimeout(() -> {
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
        }, 300);
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed < 1500,
                "runWithTimeout should return quickly after timeout (elapsed: " + elapsed + "ms)");
    }

    @Test
    void joinGuaranteesMemoryVisibility() throws InterruptedException {
        // Verify happens-before: value written by child thread is visible after join
        int[] result = {0};
        AtomicBoolean workerRan = new AtomicBoolean(false);

        Runnable task = () -> {
            result[0] = 99;
            workerRan.set(true);
        };
        pm.runWithTimeout(task, 2000);

        // After join, result[0] MUST be 99 (join establishes happens-before)
        assertEquals(99, result[0], "Join must ensure memory visibility of child thread's writes");
        assertTrue(workerRan.get());
    }
}
