package com.concurrency.intermediate.p16;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ConnectionPoolTest {

    @Test
    void acquireReturnsValidConnectionId() throws InterruptedException {
        ConnectionPool pool = new ConnectionPool(5);
        int id = pool.acquire();
        assertTrue(id >= 1 && id <= 5, "Connection ID must be in range 1..5, got: " + id);
        pool.release(id);
    }

    @Test
    void allConnectionsCanBeAcquired() throws InterruptedException {
        ConnectionPool pool = new ConnectionPool(3);
        int id1 = pool.acquire();
        int id2 = pool.acquire();
        int id3 = pool.acquire();
        assertEquals(0, pool.availableConnections(), "Pool should be exhausted");
        pool.release(id1);
        pool.release(id2);
        pool.release(id3);
    }

    @Test
    void acquireBlocksWhenPoolExhausted() throws InterruptedException {
        ConnectionPool pool = new ConnectionPool(1);
        int id = pool.acquire();
        AtomicBoolean acquired = new AtomicBoolean(false);

        Thread waiter = new Thread(() -> {
            try { pool.acquire(); acquired.set(true); }
            catch (InterruptedException ignored) {}
        });
        waiter.start();
        Thread.sleep(100);
        assertFalse(acquired.get(), "acquire() must block when pool is exhausted");

        pool.release(id);
        waiter.join(2000);
        assertTrue(acquired.get(), "acquire() must unblock after a connection is released");
    }

    @Test
    void releaseRestoresAvailability() throws InterruptedException {
        ConnectionPool pool = new ConnectionPool(2);
        assertEquals(2, pool.availableConnections());
        int id = pool.acquire();
        assertEquals(1, pool.availableConnections());
        pool.release(id);
        assertEquals(2, pool.availableConnections());
    }

    @Test
    void connectionIdsAreUniqueAcrossConcurrentAcquires() throws InterruptedException {
        int capacity = 5, threads = 5;
        ConnectionPool pool = new ConnectionPool(capacity);
        Set<Integer> ids = ConcurrentHashMap.newKeySet();
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    int id = pool.acquire();
                    ids.add(id);
                    Thread.sleep(50);
                    pool.release(id);
                } catch (InterruptedException ignored) {}
                finally { done.countDown(); }
            }).start();
        }
        done.await();
        assertEquals(threads, ids.size(), "All concurrent acquires must receive unique connection IDs");
    }

    @Test
    void maxConcurrentUsersNeverExceedsCapacity() throws InterruptedException {
        int capacity = 3, threads = 20;
        ConnectionPool pool = new ConnectionPool(capacity);
        AtomicInteger concurrentUsers = new AtomicInteger(0);
        AtomicInteger maxObserved = new AtomicInteger(0);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    int id = pool.acquire();
                    int current = concurrentUsers.incrementAndGet();
                    maxObserved.updateAndGet(m -> Math.max(m, current));
                    Thread.sleep(20);
                    concurrentUsers.decrementAndGet();
                    pool.release(id);
                } catch (InterruptedException ignored) {}
                finally { done.countDown(); }
            }).start();
        }
        done.await();
        assertTrue(maxObserved.get() <= capacity,
                "Concurrent users (" + maxObserved.get() + ") must never exceed pool capacity (" + capacity + ")");
    }
}
