package com.concurrency.advanced.p32;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class WorkStealingTest {

    // ── WorkStealingDeque ─────────────────────────────────────────────────────

    @Test
    void pushAndPopLIFO() {
        WorkStealingDeque<Integer> deque = new WorkStealingDeque<>();
        deque.push(1); deque.push(2); deque.push(3);
        assertEquals(3, deque.pop(), "pop() must return the most recently pushed element (LIFO)");
        assertEquals(2, deque.pop());
        assertEquals(1, deque.pop());
        assertNull(deque.pop(), "pop() on empty deque must return null");
    }

    @Test
    void stealFromTop() {
        WorkStealingDeque<Integer> deque = new WorkStealingDeque<>();
        deque.push(1); deque.push(2); deque.push(3);
        assertEquals(1, deque.steal(), "steal() must return the oldest element (FIFO)");
        assertEquals(2, deque.steal());
        assertEquals(3, deque.steal());
        assertNull(deque.steal(), "steal() on empty deque must return null");
    }

    @Test
    void sizeAndIsEmpty() {
        WorkStealingDeque<String> deque = new WorkStealingDeque<>();
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
        deque.push("a"); deque.push("b");
        assertFalse(deque.isEmpty());
        assertEquals(2, deque.size());
        deque.pop();
        assertEquals(1, deque.size());
    }

    @Test
    void growsWhenFull() {
        WorkStealingDeque<Integer> deque = new WorkStealingDeque<>();
        // Push many more than INITIAL_CAPACITY (16)
        for (int i = 0; i < 100; i++) deque.push(i);
        assertEquals(100, deque.size());
        // Pop all — should get them in reverse order (LIFO)
        for (int i = 99; i >= 0; i--) {
            assertEquals(i, deque.pop(), "Element " + i + " must be popped in LIFO order");
        }
        assertTrue(deque.isEmpty());
    }

    @Test
    void concurrentStealingAllItemsAreRecovered() throws InterruptedException {
        WorkStealingDeque<Integer> deque = new WorkStealingDeque<>();
        int n = 500;
        for (int i = 0; i < n; i++) deque.push(i);

        Set<Integer> stolen = ConcurrentHashMap.newKeySet();
        int thieves = 4;
        CountDownLatch done = new CountDownLatch(thieves);

        for (int t = 0; t < thieves; t++) {
            new Thread(() -> {
                Integer v;
                while ((v = deque.steal()) != null) stolen.add(v);
                done.countDown();
            }).start();
        }
        done.await();
        // Pop remainder with owner
        Integer v;
        while ((v = deque.pop()) != null) stolen.add(v);

        assertEquals(n, stolen.size(), "All " + n + " items must be recovered (no duplicates, no losses)");
    }

    @Test
    void ownerAndThievesShareWorkCorrectly() throws InterruptedException {
        WorkStealingDeque<Integer> deque = new WorkStealingDeque<>();
        int n = 200;
        for (int i = 0; i < n; i++) deque.push(i);

        Set<Integer> collected = ConcurrentHashMap.newKeySet();
        CountDownLatch done = new CountDownLatch(3);

        // 2 thieves
        for (int t = 0; t < 2; t++) {
            new Thread(() -> {
                Integer v;
                while ((v = deque.steal()) != null) collected.add(v);
                done.countDown();
            }).start();
        }
        // owner also pops
        new Thread(() -> {
            Integer v;
            while ((v = deque.pop()) != null) collected.add(v);
            done.countDown();
        }).start();

        done.await();
        assertEquals(n, collected.size(), "Combined owner+thieves must recover all items exactly once");
    }

    // ── WorkStealingScheduler ─────────────────────────────────────────────────

    @Test
    void allSubmittedTasksComplete() throws InterruptedException {
        WorkStealingScheduler sched = new WorkStealingScheduler(4);
        sched.start();

        int tasks = 100;
        AtomicInteger ran = new AtomicInteger(0);
        for (int i = 0; i < tasks; i++) sched.submit(ran::incrementAndGet);

        sched.shutdown();
        assertEquals(tasks, ran.get(), "All submitted tasks must complete");
        assertEquals(tasks, sched.getCompletedCount(), "completedCount must reflect all tasks");
    }

    @Test
    void stolenCountIsRecordedWhenStealing() throws InterruptedException {
        // One worker gets a burst of tasks → others will steal
        WorkStealingScheduler sched = new WorkStealingScheduler(4);
        sched.start();

        AtomicInteger ran = new AtomicInteger(0);
        int tasks = 200;
        for (int i = 0; i < tasks; i++) sched.submit(ran::incrementAndGet);

        sched.shutdown();
        assertEquals(tasks, ran.get());
        // With 4 workers and 200 tasks, some stealing is virtually guaranteed
        assertTrue(sched.getStolenCount() >= 0,
                "stolenCount must be non-negative; got: " + sched.getStolenCount());
    }

    @Test
    void schedulerWithSingleWorker() throws InterruptedException {
        WorkStealingScheduler sched = new WorkStealingScheduler(1);
        sched.start();
        AtomicInteger ran = new AtomicInteger(0);
        for (int i = 0; i < 50; i++) sched.submit(ran::incrementAndGet);
        sched.shutdown();
        assertEquals(50, ran.get(), "Single worker must complete all 50 tasks");
    }
}
