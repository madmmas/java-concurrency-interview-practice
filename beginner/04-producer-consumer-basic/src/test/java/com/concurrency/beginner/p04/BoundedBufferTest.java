package com.concurrency.beginner.p04;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class BoundedBufferTest {

    @Test
    void basicPutAndTake() throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(3);
        buffer.put(1);
        buffer.put(2);
        assertEquals(2, buffer.size());
        assertEquals(1, buffer.take());
        assertEquals(2, buffer.take());
        assertTrue(buffer.isEmpty());
    }

    @Test
    void isFullWhenAtCapacity() throws InterruptedException {
        BoundedBuffer<String> buffer = new BoundedBuffer<>(2);
        assertFalse(buffer.isFull());
        buffer.put("a");
        assertFalse(buffer.isFull());
        buffer.put("b");
        assertTrue(buffer.isFull());
    }

    @Test
    void takeBlocksWhenEmpty() throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(5);
        AtomicBoolean tookItem = new AtomicBoolean(false);

        Thread consumer = new Thread(() -> {
            try {
                buffer.take();           // should block until item is available
                tookItem.set(true);
            } catch (InterruptedException ignored) {}
        });
        consumer.start();

        Thread.sleep(100);               // consumer should be blocked
        assertFalse(tookItem.get(), "take() should block when buffer is empty");

        buffer.put(42);                  // unblock the consumer
        consumer.join(2000);
        assertTrue(tookItem.get(), "Consumer should have taken the item");
    }

    @Test
    void putBlocksWhenFull() throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(1);
        buffer.put(1);                   // fill the buffer

        AtomicBoolean putSucceeded = new AtomicBoolean(false);
        Thread producer = new Thread(() -> {
            try {
                buffer.put(2);           // should block until space opens
                putSucceeded.set(true);
            } catch (InterruptedException ignored) {}
        });
        producer.start();

        Thread.sleep(100);               // producer should be blocked
        assertFalse(putSucceeded.get(), "put() should block when buffer is full");

        buffer.take();                   // free space
        producer.join(2000);
        assertTrue(putSucceeded.get(), "Producer should have put the item after space opened");
    }

    @Test
    void concurrentProducersAndConsumers() throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(5);
        List<Integer> produced = Collections.synchronizedList(new ArrayList<>());
        List<Integer> consumed = Collections.synchronizedList(new ArrayList<>());
        int itemsPerProducer = 50, numProducers = 4, numConsumers = 4;
        int totalItems = itemsPerProducer * numProducers;

        List<Thread> threads = new ArrayList<>();

        for (int p = 0; p < numProducers; p++) {
            final int producerId = p;
            threads.add(new Thread(() -> {
                for (int i = 0; i < itemsPerProducer; i++) {
                    int value = producerId * 1000 + i;
                    try {
                        buffer.put(value);
                        produced.add(value);
                    } catch (InterruptedException ignored) {}
                }
            }));
        }

        for (int c = 0; c < numConsumers; c++) {
            threads.add(new Thread(() -> {
                int myTotal = totalItems / numConsumers;
                for (int i = 0; i < myTotal; i++) {
                    try { consumed.add(buffer.take()); }
                    catch (InterruptedException ignored) {}
                }
            }));
        }

        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();

        assertEquals(totalItems, produced.size(), "All items should be produced");
        assertEquals(totalItems, consumed.size(), "All items should be consumed");
        assertTrue(buffer.isEmpty(), "Buffer should be empty after all items consumed");
    }
}
