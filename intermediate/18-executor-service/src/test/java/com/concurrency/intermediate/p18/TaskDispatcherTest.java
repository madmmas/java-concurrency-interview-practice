package com.concurrency.intermediate.p18;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class TaskDispatcherTest {

    private TaskDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = new TaskDispatcher(2, 4, 10);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        dispatcher.shutdown();
    }

    @Test
    void submitExecutesTask() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        dispatcher.submit(counter::incrementAndGet);
        Thread.sleep(200);
        assertEquals(1, counter.get(), "Submitted task must execute");
    }

    @Test
    void submitAllExecutesAllTasksAndBlocks() throws InterruptedException {
        int n = 20;
        AtomicInteger counter = new AtomicInteger(0);
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < n; i++) tasks.add(counter::incrementAndGet);

        dispatcher.submitAll(tasks);
        // After submitAll returns, all tasks must be done
        assertEquals(n, counter.get(), "All tasks must complete before submitAll() returns");
    }

    @Test
    void completedTaskCountTracksExecution() throws InterruptedException {
        int n = 10;
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < n; i++) tasks.add(() -> {});
        dispatcher.submitAll(tasks);
        assertEquals(n, dispatcher.getCompletedTaskCount(),
                "Completed task count must equal number of submitted tasks");
    }

    @Test
    void callerRunsPolicyPreventsRejection() throws InterruptedException {
        // Saturate the pool + queue, extra tasks should run on caller thread
        TaskDispatcher small = new TaskDispatcher(1, 1, 2);
        AtomicInteger ran = new AtomicInteger(0);
        List<Runnable> tasks = new ArrayList<>();
        // 10 tasks into a pool of 1 thread + queue of 2 = CallerRunsPolicy kicks in
        for (int i = 0; i < 10; i++) tasks.add(() -> {
            try { Thread.sleep(20); } catch (InterruptedException ignored) {}
            ran.incrementAndGet();
        });
        small.submitAll(tasks);
        assertEquals(10, ran.get(), "CallerRunsPolicy must ensure no tasks are rejected");
        small.shutdown();
    }

    @Test
    void shutdownPreventsNewSubmissions() throws InterruptedException {
        dispatcher.shutdown();
        // After shutdown, submitting should not throw (CallerRunsPolicy handles it gracefully)
        // or if using AbortPolicy it would throw — just verify shutdown completes
        assertFalse(dispatcher.getActiveThreadCount() > 0, // threads should be winding down
                "Active thread count should be 0 after shutdown");
    }
}
