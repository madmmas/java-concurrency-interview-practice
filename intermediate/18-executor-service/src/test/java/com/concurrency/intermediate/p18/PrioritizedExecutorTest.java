package com.concurrency.intermediate.p18;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class PrioritizedExecutorTest {

    @Test
    void highPriorityTaskRunsFirst() throws InterruptedException {
        // Single worker thread ensures tasks run one at a time in priority order
        PrioritizedExecutor exec = new PrioritizedExecutor(1);
        CopyOnWriteArrayList<String> order = new CopyOnWriteArrayList<>();

        // Block the single thread so tasks queue up
        CountDownLatch blockLatch = new CountDownLatch(1);
        exec.submit(() -> {
            try { blockLatch.await(); } catch (InterruptedException ignored) {}
        }, 0);
        Thread.sleep(50); // ensure the blocker is running

        exec.submit(() -> order.add("LOW"),    1);
        exec.submit(() -> order.add("HIGH"),  10);
        exec.submit(() -> order.add("MEDIUM"), 5);

        blockLatch.countDown(); // release the blocker
        exec.shutdown();
        exec.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(List.of("HIGH", "MEDIUM", "LOW"), order,
                "Tasks must execute in priority order: HIGH > MEDIUM > LOW");
    }

    @Test
    void equalPriorityRunsInSubmissionOrder() throws InterruptedException {
        PrioritizedExecutor exec = new PrioritizedExecutor(1);
        CopyOnWriteArrayList<Integer> order = new CopyOnWriteArrayList<>();

        CountDownLatch blockLatch = new CountDownLatch(1);
        exec.submit(() -> {
            try { blockLatch.await(); } catch (InterruptedException ignored) {}
        }, 0);
        Thread.sleep(50);

        for (int i = 0; i < 5; i++) {
            final int seq = i;
            exec.submit(() -> order.add(seq), 5); // all same priority
        }
        blockLatch.countDown();
        exec.shutdown();
        exec.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(List.of(0, 1, 2, 3, 4), order,
                "Equal-priority tasks must execute in submission order (FIFO)");
    }

    @Test
    void priorityTaskComparatorIsCorrect() {
        PrioritizedExecutor.PriorityTask high   = new PrioritizedExecutor.PriorityTask(() -> {}, 10, 0);
        PrioritizedExecutor.PriorityTask medium = new PrioritizedExecutor.PriorityTask(() -> {}, 5,  1);
        PrioritizedExecutor.PriorityTask low    = new PrioritizedExecutor.PriorityTask(() -> {}, 1,  2);

        assertTrue(high.compareTo(medium) < 0, "HIGH priority should sort before MEDIUM");
        assertTrue(medium.compareTo(low)  < 0, "MEDIUM priority should sort before LOW");
        assertTrue(low.compareTo(high)    > 0, "LOW priority should sort after HIGH");
    }

    @Test
    void allTasksEventuallyComplete() throws InterruptedException {
        PrioritizedExecutor exec = new PrioritizedExecutor(3);
        AtomicInteger counter = new AtomicInteger(0);
        int n = 50;
        for (int i = 0; i < n; i++) {
            final int p = i % 10;
            exec.submit(counter::incrementAndGet, p);
        }
        exec.shutdown();
        exec.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(n, counter.get(), "All " + n + " tasks must complete");
    }
}
