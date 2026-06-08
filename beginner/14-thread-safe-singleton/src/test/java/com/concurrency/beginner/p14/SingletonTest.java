package com.concurrency.beginner.p14;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class SingletonTest {

    @BeforeEach
    void reset() {
        // Reset static state between tests — only works for non-eager variants
        SynchronizedSingleton.resetForTesting();
        DoubleCheckedSingleton.resetForTesting();
        // Note: EagerSingleton and HolderSingleton cannot be reset
    }

    // ── Eager ──────────────────────────────────────────────────────────────

    @Test
    void eagerSingletonReturnsSameInstance() {
        assertSame(EagerSingleton.getInstance(), EagerSingleton.getInstance(),
                "EagerSingleton.getInstance() must always return the same object");
    }

    @Test
    void eagerSingletonCreatedExactlyOnce() {
        EagerSingleton.getInstance();
        EagerSingleton.getInstance();
        assertEquals(1, EagerSingleton.getCreationCount(),
                "EagerSingleton constructor must be called exactly once");
    }

    // ── Synchronized ───────────────────────────────────────────────────────

    @Test
    void synchronizedSingletonReturnsSameInstance() {
        assertSame(SynchronizedSingleton.getInstance(), SynchronizedSingleton.getInstance());
    }

    @Test
    void synchronizedSingletonCreatedExactlyOnceUnderConcurrency() throws InterruptedException {
        int threads = 50;
        Set<Integer> ids = ConcurrentHashMap.newKeySet();
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try { go.await(); ids.add(SynchronizedSingleton.getInstance().getId()); }
                catch (InterruptedException ignored) {}
                finally { done.countDown(); }
            }).start();
        }
        go.countDown();
        done.await();

        assertEquals(1, ids.size(), "All threads must see the same singleton id");
        assertEquals(1, SynchronizedSingleton.getCreationCount(),
                "Constructor must be called exactly once even under concurrent access");
    }

    // ── Double-Checked Locking ─────────────────────────────────────────────

    @Test
    void dclSingletonReturnsSameInstance() {
        assertSame(DoubleCheckedSingleton.getInstance(), DoubleCheckedSingleton.getInstance());
    }

    @Test
    void dclSingletonCreatedExactlyOnceUnderConcurrency() throws InterruptedException {
        int threads = 50;
        Set<Integer> ids = ConcurrentHashMap.newKeySet();
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try { go.await(); ids.add(DoubleCheckedSingleton.getInstance().getId()); }
                catch (InterruptedException ignored) {}
                finally { done.countDown(); }
            }).start();
        }
        go.countDown();
        done.await();

        assertEquals(1, ids.size(), "All threads must see the same DCL singleton id");
        assertEquals(1, DoubleCheckedSingleton.getCreationCount(),
                "DCL constructor must be called exactly once");
    }

    // ── Holder ─────────────────────────────────────────────────────────────

    @Test
    void holderSingletonReturnsSameInstance() {
        assertSame(HolderSingleton.getInstance(), HolderSingleton.getInstance());
    }

    @Test
    void holderSingletonCreatedExactlyOnceUnderConcurrency() throws InterruptedException {
        int threads = 50;
        Set<Integer> ids = ConcurrentHashMap.newKeySet();
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try { go.await(); ids.add(HolderSingleton.getInstance().getId()); }
                catch (InterruptedException ignored) {}
                finally { done.countDown(); }
            }).start();
        }
        go.countDown();
        done.await();

        assertEquals(1, ids.size(), "All threads must see the same Holder singleton id");
        assertEquals(1, HolderSingleton.getCreationCount(),
                "Holder constructor must be called exactly once");
    }

    // ── Cross-approach sanity ──────────────────────────────────────────────

    @Test
    void allApproachesReturnSameObjectAcrossMultipleCalls() {
        for (int i = 0; i < 100; i++) {
            assertSame(EagerSingleton.getInstance(), EagerSingleton.getInstance());
            assertSame(HolderSingleton.getInstance(), HolderSingleton.getInstance());
        }
    }
}
