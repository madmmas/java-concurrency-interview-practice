package com.concurrency.beginner.p02;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class TaskRunnerTest {

    private final TaskRunner runner = new TaskRunner();

    @Test
    void runTaskExecutesOnNamedThread() throws InterruptedException {
        AtomicReference<String> threadName = new AtomicReference<>();
        runner.runTask(() -> threadName.set(Thread.currentThread().getName()), "special-thread");
        assertEquals("special-thread", threadName.get(),
                "Task should run on a thread named 'special-thread'");
    }

    @Test
    void runTaskBlocksUntilComplete() throws InterruptedException {
        List<String> log = new ArrayList<>();
        runner.runTask(() -> {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            log.add("done");
        }, "t");
        // After runTask returns, "done" must already be in the log
        assertEquals(List.of("done"), log, "runTask should block until the task finishes");
    }

    @Test
    void runTasksParallelRunsAllTasks() throws InterruptedException {
        CopyOnWriteArrayList<String> log = new CopyOnWriteArrayList<>();
        List<Runnable> tasks = List.of(
                () -> log.add("task-0"),
                () -> log.add("task-1"),
                () -> log.add("task-2")
        );
        runner.runTasksParallel(tasks);
        assertEquals(3, log.size(), "All 3 tasks should have run");
        assertTrue(log.containsAll(List.of("task-0", "task-1", "task-2")));
    }

    @Test
    void runTasksParallelNamesThreadsCorrectly() throws InterruptedException {
        CopyOnWriteArrayList<String> names = new CopyOnWriteArrayList<>();
        List<Runnable> tasks = List.of(
                () -> names.add(Thread.currentThread().getName()),
                () -> names.add(Thread.currentThread().getName()),
                () -> names.add(Thread.currentThread().getName())
        );
        runner.runTasksParallel(tasks);
        List<String> sorted = new ArrayList<>(names);
        Collections.sort(sorted);
        assertEquals(List.of("worker-0", "worker-1", "worker-2"), sorted,
                "Threads should be named worker-0, worker-1, worker-2");
    }

    @Test
    void buildCountingRunnableAddsCorrectly() throws InterruptedException {
        CopyOnWriteArrayList<Integer> collector = new CopyOnWriteArrayList<>();
        Runnable r = runner.buildCountingRunnable(collector, 42, 5);
        Thread t = new Thread(r);
        t.start();
        t.join();
        assertEquals(5, collector.size(), "Should add exactly 5 elements");
        assertTrue(collector.stream().allMatch(v -> v == 42), "All elements should be 42");
    }

    @Test
    void runTasksParallelBlocksUntilAllComplete() throws InterruptedException {
        CopyOnWriteArrayList<String> log = new CopyOnWriteArrayList<>();
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final int idx = i;
            tasks.add(() -> {
                try { Thread.sleep(30); } catch (InterruptedException ignored) {}
                log.add("done-" + idx);
            });
        }
        runner.runTasksParallel(tasks);
        assertEquals(5, log.size(), "All 5 tasks must be done before runTasksParallel returns");
    }
}
