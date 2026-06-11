package com.concurrency.intermediate.p19;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class AsyncTaskRunnerTest {

    private AsyncTaskRunner<Integer> runner;

    @BeforeEach void setUp()               { runner = new AsyncTaskRunner<>(4); }
    @AfterEach  void tearDown() throws Exception { runner.shutdown(); }

    @Test
    void submitTaskReturnsCorrectResult() throws Exception {
        Future<Integer> f = runner.submitTask(() -> 21 + 21);
        assertEquals(42, f.get());
    }

    @Test
    void runAllPreservesOrder() throws Exception {
        List<Callable<Integer>> tasks = List.of(
                () -> 1, () -> 2, () -> 3, () -> 4, () -> 5
        );
        List<Integer> results = runner.runAll(tasks);
        assertEquals(List.of(1, 2, 3, 4, 5), results,
                "runAll() must return results in submission order");
    }

    @Test
    void runAllPropagatesException() {
        List<Callable<Integer>> tasks = List.of(
                () -> 1,
                () -> { throw new RuntimeException("task failed"); },
                () -> 3
        );
        assertThrows(ExecutionException.class, () -> runner.runAll(tasks),
                "runAll() must propagate exceptions from failed tasks");
    }

    @Test
    void runWithTimeoutReturnsResultWhenFast() throws Exception {
        Integer result = runner.runWithTimeout(() -> {
            Thread.sleep(50);
            return 99;
        }, 2000);
        assertEquals(99, result);
    }

    @Test
    void runWithTimeoutThrowsWhenSlow() {
        assertThrows(TimeoutException.class, () ->
                runner.runWithTimeout(() -> { Thread.sleep(5000); return 0; }, 100),
                "Must throw TimeoutException when task exceeds timeout");
    }

    @Test
    void runFirstCompletedReturnsQuickestTask() throws Exception {
        List<Callable<Integer>> tasks = List.of(
                () -> { Thread.sleep(2000); return 1; }, // slow
                () -> { Thread.sleep(50);   return 2; }, // medium
                () -> { Thread.sleep(10);   return 3; }  // fastest
        );
        Integer result = runner.runFirstCompleted(tasks);
        // The fastest task returns 3 but race conditions mean we accept 2 or 3
        assertTrue(result == 2 || result == 3,
                "runFirstCompleted() should return one of the fast tasks' results, got: " + result);
    }

    @Test
    void runAllWithTenTasksIsCorrect() throws Exception {
        List<Callable<Integer>> tasks = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final int val = i;
            tasks.add(() -> val * val);
        }
        List<Integer> results = runner.runAll(tasks);
        for (int i = 0; i < 10; i++) {
            assertEquals(i * i, results.get(i), "Result at index " + i + " is wrong");
        }
    }
}
