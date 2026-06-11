package com.concurrency.advanced.p26;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class LockFreeDataStructuresTest {

    // ── TreiberStack ─────────────────────────────────────────────────────────

    @Test
    void stackPushAndPopLIFO() {
        TreiberStack<Integer> stack = new TreiberStack<>();
        stack.push(1); stack.push(2); stack.push(3);
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
        assertNull(stack.pop(), "pop() on empty stack must return null");
    }

    @Test
    void stackPeekDoesNotRemove() {
        TreiberStack<String> stack = new TreiberStack<>();
        stack.push("hello");
        assertEquals("hello", stack.peek());
        assertEquals("hello", stack.peek());  // still there
        assertFalse(stack.isEmpty());
    }

    @Test
    void stackIsEmptyAndSize() {
        TreiberStack<Integer> stack = new TreiberStack<>();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
        stack.push(1);
        assertFalse(stack.isEmpty());
        assertEquals(1, stack.size());
        stack.pop();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void stackConcurrentPushesAllPresent() throws InterruptedException {
        TreiberStack<Integer> stack = new TreiberStack<>();
        int threads = 10, pushesEach = 1_000;
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            final int base = i * pushesEach;
            workers.add(new Thread(() -> {
                for (int j = 0; j < pushesEach; j++) stack.push(base + j);
            }));
        }
        workers.forEach(Thread::start);
        for (Thread t : workers) t.join();

        Set<Integer> recovered = new HashSet<>();
        Integer v;
        while ((v = stack.pop()) != null) recovered.add(v);
        assertEquals(threads * pushesEach, recovered.size(),
                "Every pushed value must be recoverable — no lost updates");
    }

    @Test
    void stackConcurrentPushesAndPopsBalance() throws InterruptedException {
        TreiberStack<Integer> stack = new TreiberStack<>();
        int ops = 5_000;
        AtomicInteger pushed = new AtomicInteger(0);
        AtomicInteger popped = new AtomicInteger(0);
        CountDownLatch go   = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(4);

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                try { go.await(); } catch (InterruptedException ignored) {}
                for (int j = 0; j < ops; j++) { stack.push(j); pushed.incrementAndGet(); }
                done.countDown();
            }).start();
        }
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                try { go.await(); } catch (InterruptedException ignored) {}
                for (int j = 0; j < ops; j++) { if (stack.pop() != null) popped.incrementAndGet(); }
                done.countDown();
            }).start();
        }
        go.countDown();
        done.await();
        Integer v; while ((v = stack.pop()) != null) popped.incrementAndGet();
        assertEquals(pushed.get(), popped.get(),
                "Every push must be matched by exactly one pop");
    }

    // ── LockFreeQueue ────────────────────────────────────────────────────────

    @Test
    void queueEnqueueDequeueFIFO() {
        LockFreeQueue<Integer> q = new LockFreeQueue<>();
        q.enqueue(1); q.enqueue(2); q.enqueue(3);
        assertEquals(1, q.dequeue());
        assertEquals(2, q.dequeue());
        assertEquals(3, q.dequeue());
        assertNull(q.dequeue(), "dequeue() on empty queue must return null");
    }

    @Test
    void queueIsEmpty() {
        LockFreeQueue<String> q = new LockFreeQueue<>();
        assertTrue(q.isEmpty());
        q.enqueue("x");
        assertFalse(q.isEmpty());
        q.dequeue();
        assertTrue(q.isEmpty());
    }

    @Test
    void queueConcurrentEnqueuesAllPresent() throws InterruptedException {
        LockFreeQueue<Integer> q = new LockFreeQueue<>();
        int threads = 8, itemsEach = 500;
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            final int base = i * itemsEach;
            workers.add(new Thread(() -> {
                for (int j = 0; j < itemsEach; j++) q.enqueue(base + j);
            }));
        }
        workers.forEach(Thread::start);
        for (Thread t : workers) t.join();

        Set<Integer> collected = new HashSet<>();
        Integer v;
        while ((v = q.dequeue()) != null) collected.add(v);
        assertEquals(threads * itemsEach, collected.size(),
                "All enqueued items must be dequeued — no data loss");
    }

    @Test
    void queueConcurrentProducersAndConsumers() throws InterruptedException {
        LockFreeQueue<Integer> q = new LockFreeQueue<>();
        int ops = 2_000;
        AtomicInteger enqueued = new AtomicInteger(0);
        AtomicInteger dequeued = new AtomicInteger(0);
        CountDownLatch go   = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(4);

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                try { go.await(); } catch (InterruptedException ignored) {}
                for (int j = 0; j < ops; j++) { q.enqueue(j); enqueued.incrementAndGet(); }
                done.countDown();
            }).start();
        }
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                try { go.await(); } catch (InterruptedException ignored) {}
                for (int j = 0; j < ops; j++) { if (q.dequeue() != null) dequeued.incrementAndGet(); }
                done.countDown();
            }).start();
        }
        go.countDown();
        done.await();
        Integer v; while ((v = q.dequeue()) != null) dequeued.incrementAndGet();
        assertEquals(enqueued.get(), dequeued.get(),
                "Every enqueued item must be dequeued exactly once");
    }

    // ── ABA Demonstrator ─────────────────────────────────────────────────────

    @Test
    void naiveCASSucceedsDespiteABA() throws InterruptedException {
        ABADemonstrator demo = new ABADemonstrator();
        assertTrue(demo.demonstrateABA(),
                "Naive AtomicReference CAS MUST succeed (this is the ABA bug) — " +
                "ref looks unchanged even though it was modified and restored");
    }

    @Test
    void stampedReferenceCorrectlyRejectsABA() throws InterruptedException {
        ABADemonstrator demo = new ABADemonstrator();
        assertFalse(demo.demonstrateABAFix(),
                "AtomicStampedReference CAS MUST fail — the stamp changed even " +
                "though the value appears identical");
    }
}
