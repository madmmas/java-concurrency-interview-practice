package com.concurrency.intermediate.p25;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ConcurrentInventoryTest {

    private ConcurrentInventory inventory;

    @BeforeEach void setUp() { inventory = new ConcurrentInventory(); }

    @Test
    void addAndGetStock() {
        inventory.addItem("apple", 10);
        assertEquals(10, inventory.getStock("apple"));
    }

    @Test
    void addAccumulatesQuantity() {
        inventory.addItem("apple", 5);
        inventory.addItem("apple", 3);
        assertEquals(8, inventory.getStock("apple"));
    }

    @Test
    void removeDecreasesStock() {
        inventory.addItem("apple", 10);
        inventory.removeItem("apple", 4);
        assertEquals(6, inventory.getStock("apple"));
    }

    @Test
    void removeToZeroDeletesKey() {
        inventory.addItem("apple", 5);
        inventory.removeItem("apple", 5);
        assertEquals(0, inventory.getStock("apple"));
        assertFalse(inventory.getSnapshot().containsKey("apple"),
                "Key must be removed when stock reaches 0");
    }

    @Test
    void removeThrowsWhenInsufficientStock() {
        inventory.addItem("apple", 3);
        assertThrows(IllegalStateException.class,
                () -> inventory.removeItem("apple", 10));
    }

    @Test
    void reserveSucceedsWhenStockSufficient() {
        inventory.addItem("apple", 10);
        assertTrue(inventory.reserveItem("apple", 3));
        assertEquals(7, inventory.getStock("apple"));
    }

    @Test
    void reserveFailsWhenStockInsufficient() {
        inventory.addItem("apple", 2);
        assertFalse(inventory.reserveItem("apple", 5));
        assertEquals(2, inventory.getStock("apple"), "Stock must be unchanged after failed reserve");
    }

    @Test
    void reserveFailsForMissingItem() {
        assertFalse(inventory.reserveItem("banana", 1));
    }

    @Test
    void concurrentAddItemIsAccurate() throws InterruptedException {
        int threads = 10, addEach = 100;
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            workers.add(new Thread(() -> {
                for (int j = 0; j < addEach; j++) inventory.addItem("item", 1);
            }));
        }
        workers.forEach(Thread::start);
        for (Thread t : workers) t.join();
        assertEquals(threads * addEach, inventory.getStock("item"),
                "Concurrent addItem must be fully atomic — no lost updates");
    }

    @Test
    void concurrentReservationsNeverOversell() throws InterruptedException {
        inventory.addItem("ticket", 100);
        int threads = 50, reserveEach = 5; // 250 attempts for 100 tickets
        AtomicInteger successes = new AtomicInteger(0);
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    go.await();
                    for (int j = 0; j < reserveEach; j++) {
                        if (inventory.reserveItem("ticket", 1)) successes.incrementAndGet();
                    }
                } catch (InterruptedException ignored) {}
                finally { done.countDown(); }
            }).start();
        }
        go.countDown();
        done.await();

        int remaining = inventory.getStock("ticket");
        assertEquals(100, successes.get() + remaining,
                "Successes + remaining stock must equal initial stock (no overselling)");
        assertTrue(successes.get() <= 100,
                "Cannot reserve more than 100 tickets; reserved: " + successes.get());
    }

    @Test
    void snapshotIsUnmodifiable() {
        inventory.addItem("x", 5);
        var snap = inventory.getSnapshot();
        assertThrows(UnsupportedOperationException.class, () -> snap.put("y", 1));
    }
}
