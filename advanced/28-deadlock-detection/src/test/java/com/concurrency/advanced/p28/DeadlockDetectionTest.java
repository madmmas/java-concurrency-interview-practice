package com.concurrency.advanced.p28;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class DeadlockDetectionTest {

    // ── ResourceAllocationGraph ───────────────────────────────────────────────

    private ResourceAllocationGraph rag;

    @BeforeEach
    void setUp() { rag = new ResourceAllocationGraph(); }

    @Test
    void noDeadlockInitially() {
        assertFalse(rag.hasDeadlock());
        assertTrue(rag.getDeadlockedThreads().isEmpty());
    }

    @Test
    void noDeadlockForLinearWait() {
        // T1 holds R1; T2 waits for R1 → no cycle
        rag.requestResource("T1", "R1"); rag.assignResource("T1", "R1");
        rag.requestResource("T2", "R1");
        assertFalse(rag.hasDeadlock(), "One-way wait is not a cycle");
    }

    @Test
    void detectsTwoThreadDeadlock() {
        // T1 holds R1, wants R2; T2 holds R2, wants R1 → cycle
        rag.requestResource("T1", "R1"); rag.assignResource("T1", "R1");
        rag.requestResource("T2", "R2"); rag.assignResource("T2", "R2");
        rag.requestResource("T1", "R2");
        rag.requestResource("T2", "R1");

        assertTrue(rag.hasDeadlock(), "Two-thread circular wait must be detected");
        assertTrue(rag.getDeadlockedThreads().containsAll(Set.of("T1", "T2")));
    }

    @Test
    void detectsThreeThreadCycle() {
        // T1→R1, T2→R2, T3→R3; T1 wants R2, T2 wants R3, T3 wants R1
        rag.requestResource("T1", "R1"); rag.assignResource("T1", "R1");
        rag.requestResource("T2", "R2"); rag.assignResource("T2", "R2");
        rag.requestResource("T3", "R3"); rag.assignResource("T3", "R3");
        rag.requestResource("T1", "R2");
        rag.requestResource("T2", "R3");
        rag.requestResource("T3", "R1");

        assertTrue(rag.hasDeadlock(), "Three-thread cycle must be detected");
        Set<String> deadlocked = rag.getDeadlockedThreads();
        assertTrue(deadlocked.containsAll(Set.of("T1", "T2", "T3")));
    }

    @Test
    void releasingResourceBreaksCycle() {
        rag.requestResource("T1", "R1"); rag.assignResource("T1", "R1");
        rag.requestResource("T2", "R2"); rag.assignResource("T2", "R2");
        rag.requestResource("T1", "R2");
        rag.requestResource("T2", "R1");
        assertTrue(rag.hasDeadlock());

        rag.releaseResource("T2", "R2");   // break the cycle
        assertFalse(rag.hasDeadlock(), "Releasing a resource must resolve the deadlock");
    }

    // ── DeadlockPreventer ─────────────────────────────────────────────────────

    @Test
    void acquireInOrderNeverDeadlocks() throws InterruptedException {
        ReentrantLock lockA = new ReentrantLock();
        ReentrantLock lockB = new ReentrantLock();
        DeadlockPreventer preventer = new DeadlockPreventer();
        int threads = 20;
        CountDownLatch done = new CountDownLatch(threads);
        java.util.concurrent.atomic.AtomicInteger completed = new java.util.concurrent.atomic.AtomicInteger(0);

        for (int i = 0; i < threads / 2; i++) {
            // Half: A then B (canonical order)
            new Thread(() -> {
                try {
                    Runnable release = preventer.acquireInOrder(lockA, lockB);
                    completed.incrementAndGet();
                    release.run();
                } finally { done.countDown(); }
            }).start();
            // Half: B then A — would deadlock without ordering
            new Thread(() -> {
                try {
                    Runnable release = preventer.acquireInOrder(lockB, lockA);
                    completed.incrementAndGet();
                    release.run();
                } finally { done.countDown(); }
            }).start();
        }
        assertTrue(done.await(8, TimeUnit.SECONDS),
                "All threads must complete without deadlock");
        assertEquals(threads, completed.get());
    }

    @Test
    void acquireInOrderReleaserUnlocksCorrectly() {
        ReentrantLock lockA = new ReentrantLock();
        ReentrantLock lockB = new ReentrantLock();
        DeadlockPreventer preventer = new DeadlockPreventer();
        Runnable release = preventer.acquireInOrder(lockA, lockB);
        assertTrue(lockA.isLocked(), "lockA must be held");
        assertTrue(lockB.isLocked(), "lockB must be held");
        release.run();
        assertFalse(lockA.isLocked(), "lockA must be released");
        assertFalse(lockB.isLocked(), "lockB must be released");
    }

    @Test
    void tryAcquireSucceedsWhenLocksAreFree() throws InterruptedException {
        ReentrantLock a = new ReentrantLock(), b = new ReentrantLock();
        DeadlockPreventer preventer = new DeadlockPreventer();
        assertTrue(preventer.tryAcquireBothWithTimeout(a, b, 1_000));
        assertTrue(a.isHeldByCurrentThread());
        assertTrue(b.isHeldByCurrentThread());
        a.unlock(); b.unlock();
    }

    @Test
    void tryAcquireFailsWhenSecondLockHeld() throws InterruptedException {
        ReentrantLock a = new ReentrantLock(), b = new ReentrantLock();
        DeadlockPreventer preventer = new DeadlockPreventer();
        b.lock();   // hold b on this thread
        try {
            assertFalse(preventer.tryAcquireBothWithTimeout(a, b, 200),
                    "Must return false when second lock is unavailable");
            assertFalse(a.isLocked(), "Lock A must be released when B acquisition fails");
        } finally {
            b.unlock();
        }
    }

    // ── DeadlockDemo ─────────────────────────────────────────────────────────

    @Test
    void jvmDetectsCreatedDeadlock() throws InterruptedException {
        DeadlockDemo demo = new DeadlockDemo();
        Thread[] threads = demo.createDeadlock();
        assertNotNull(threads);
        assertEquals(2, threads.length);

        Thread.sleep(300);   // give threads time to enter the deadlock state

        assertTrue(threads[0].isAlive(), "Thread 1 must be stuck (alive)");
        assertTrue(threads[1].isAlive(), "Thread 2 must be stuck (alive)");
        assertTrue(demo.detectDeadlock(threads[0], threads[1]),
                "ThreadMXBean must detect the deadlock");

        // Clean up: interrupt both (they are daemon-compatible via interrupt)
        threads[0].interrupt();
        threads[1].interrupt();
        threads[0].join(1_000);
        threads[1].join(1_000);
    }

    @Test
    void detectDeadlockReturnsFalseForHealthyThreads() throws InterruptedException {
        DeadlockDemo demo = new DeadlockDemo();
        Thread t1 = new Thread(() -> { try { Thread.sleep(500); } catch (InterruptedException ignored) {} });
        Thread t2 = new Thread(() -> { try { Thread.sleep(500); } catch (InterruptedException ignored) {} });
        t1.start(); t2.start();
        assertFalse(demo.detectDeadlock(t1, t2),
                "No deadlock must be reported for normally running threads");
        t1.join(); t2.join();
    }
}
