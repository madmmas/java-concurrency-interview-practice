package com.concurrency.beginner.p11;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class BoundedStackWithConditionTest {

    @Test
    void basicPushAndPop() throws InterruptedException {
        BoundedStackWithCondition<Integer> stack = new BoundedStackWithCondition<>(5);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.size());
        assertEquals(3, stack.pop()); // LIFO
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void peekDoesNotRemove() throws InterruptedException {
        BoundedStackWithCondition<String> stack = new BoundedStackWithCondition<>(3);
        stack.push("hello");
        assertEquals("hello", stack.peek());
        assertEquals("hello", stack.peek());
        assertEquals(1, stack.size(), "peek() must not remove the element");
    }

    @Test
    void pushBlocksWhenFull() throws InterruptedException {
        BoundedStackWithCondition<Integer> stack = new BoundedStackWithCondition<>(1);
        stack.push(99);
        AtomicBoolean pushed = new AtomicBoolean(false);
        Thread producer = new Thread(() -> {
            try { stack.push(100); pushed.set(true); }
            catch (InterruptedException ignored) {}
        });
        producer.start();
        Thread.sleep(100);
        assertFalse(pushed.get(), "push() should block when stack is full");
        stack.pop(); // make room
        producer.join(2000);
        assertTrue(pushed.get(), "push() should complete after space is freed");
    }

    @Test
    void popBlocksWhenEmpty() throws InterruptedException {
        BoundedStackWithCondition<Integer> stack = new BoundedStackWithCondition<>(5);
        AtomicReference<Integer> result = new AtomicReference<>();
        Thread consumer = new Thread(() -> {
            try { result.set(stack.pop()); }
            catch (InterruptedException ignored) {}
        });
        consumer.start();
        Thread.sleep(100);
        assertNull(result.get(), "pop() should block when stack is empty");
        stack.push(42);
        consumer.join(2000);
        assertEquals(42, result.get(), "pop() should return the pushed value");
    }

    @Test
    void conditionSignalsCorrectWaiters() throws InterruptedException {
        // Multiple producers and consumers — verify no deadlock and correct count
        BoundedStackWithCondition<Integer> stack = new BoundedStackWithCondition<>(5);
        int ops = 50;
        CountDownLatch done = new CountDownLatch(2);
        Thread producer = new Thread(() -> {
            try { for (int i = 0; i < ops; i++) stack.push(i); }
            catch (InterruptedException ignored) {}
            finally { done.countDown(); }
        });
        Thread consumer = new Thread(() -> {
            try { for (int i = 0; i < ops; i++) stack.pop(); }
            catch (InterruptedException ignored) {}
            finally { done.countDown(); }
        });
        producer.start(); consumer.start();
        assertTrue(done.await(8, TimeUnit.SECONDS), "Producer and consumer should complete without deadlock");
        assertEquals(0, stack.size());
    }
}
