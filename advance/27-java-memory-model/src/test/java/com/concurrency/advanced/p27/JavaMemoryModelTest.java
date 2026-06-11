package com.concurrency.advanced.p27;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class JavaMemoryModelTest {

    // ── SafePublicationShowcase ───────────────────────────────────────────────

    @Test
    void unsafePublishWorksInSingleThread() {
        SafePublicationShowcase s = new SafePublicationShowcase();
        SafePublicationShowcase.MutableHolder h = s.publishUnsafe(42);
        // In single-threaded context the value is always correct
        assertNotNull(h);
        assertEquals(42, h.value);
    }

    @Test
    void synchronizedPublicationVisibleAfterJoin() throws InterruptedException {
        SafePublicationShowcase s = new SafePublicationShowcase();
        Thread writer = new Thread(() -> s.publishViaSynchronized(99));
        writer.start();
        writer.join();   // join HB → writer's synchronized-unlock is visible

        SafePublicationShowcase.MutableHolder h = s.getSynchronized();
        assertNotNull(h);
        assertEquals(99, h.value,
                "synchronized publication must make value visible after join()");
    }

    @Test
    void volatilePublicationVisibleAfterJoin() throws InterruptedException {
        SafePublicationShowcase s = new SafePublicationShowcase();
        Thread writer = new Thread(() -> s.publishViaVolatile(77));
        writer.start();
        writer.join();   // join HB → volatile write is visible

        SafePublicationShowcase.MutableHolder h = s.getVolatile();
        assertNotNull(h);
        assertEquals(77, h.value,
                "volatile publication must make value visible after join()");
    }

    @Test
    void finalFieldPublicationVisibleAfterJoin() throws InterruptedException {
        SafePublicationShowcase s = new SafePublicationShowcase();
        Thread writer = new Thread(() -> s.publishViaFinalField(55));
        writer.start();
        writer.join();   // join HB → construction + final-freeze guarantee

        SafePublicationShowcase.ImmutableHolder h = s.getFinalFieldHolder();
        assertNotNull(h);
        assertEquals(55, h.value,
                "final field must be visible after thread join");
    }

    // ── HappensBeforeChain ────────────────────────────────────────────────────

    @Test
    void twoThreadChainReturnsSum() throws InterruptedException {
        HappensBeforeChain chain = new HappensBeforeChain();
        long result = chain.runChain(new int[]{3, 7});
        assertEquals(10L, result, "2-thread chain must return 3+7=10");
    }

    @Test
    void fiveThreadChainReturnsSum() throws InterruptedException {
        HappensBeforeChain chain = new HappensBeforeChain();
        long result = chain.runChain(new int[]{1, 2, 3, 4, 5});
        assertEquals(15L, result, "5-thread chain must return 1+2+3+4+5=15");
    }

    @Test
    void tenThreadChainCompletesWithoutDeadlock() throws InterruptedException {
        HappensBeforeChain chain = new HappensBeforeChain();
        long result = chain.runChain(new int[]{1,2,3,4,5,6,7,8,9,10});
        assertEquals(55L, result, "10-thread chain must return sum=55 and complete without deadlock");
    }

    @Test
    void chainIsRepeatableOnNewInstances() throws InterruptedException {
        for (int run = 0; run < 5; run++) {
            HappensBeforeChain chain = new HappensBeforeChain();
            long result = chain.runChain(new int[]{run + 1, run + 2, run + 3});
            long expected = (run + 1) + (run + 2) + (run + 3);
            assertEquals(expected, result, "Run " + run + " must return " + expected);
        }
    }

    // ── MemoryVisibilityProbe ─────────────────────────────────────────────────

    @Test
    void buggyVisibilityFlagIsSetByStop() {
        MemoryVisibilityProbe.BuggyVisibility buggy = new MemoryVisibilityProbe.BuggyVisibility();
        buggy.start();
        assertTrue(buggy.isRunning());
        buggy.stop();
        assertFalse(buggy.isRunning(),
                "Flag must be false on the writing thread — visibility to worker is not guaranteed");
        // Clean up: the worker may spin forever if running was cached; interrupt it via daemon status
    }

    @Test
    void fixedVisibilityTerminatesPromptly() throws InterruptedException {
        MemoryVisibilityProbe.FixedVisibility fixed = new MemoryVisibilityProbe.FixedVisibility();
        fixed.start();
        assertTrue(fixed.isThreadAlive(), "Worker must be alive after start()");
        Thread.sleep(50);
        fixed.stop();   // volatile write + join
        assertFalse(fixed.isRunning(), "volatile flag must be false after stop()");
        assertFalse(fixed.isThreadAlive(),
                "Worker must have terminated — volatile ensures it sees the stop signal");
    }

    @Test
    void fixedVisibilityStopTimeIsReasonable() throws InterruptedException {
        MemoryVisibilityProbe.FixedVisibility fixed = new MemoryVisibilityProbe.FixedVisibility();
        fixed.start();
        Thread.sleep(30);
        long t0 = System.currentTimeMillis();
        fixed.stop();
        long elapsed = System.currentTimeMillis() - t0;
        assertTrue(elapsed < 2_000,
                "stop() must return within 2 s (worker sees volatile flag); elapsed: " + elapsed + " ms");
    }
}
