package com.concurrency.beginner.p08;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class InterruptibleWorkerTest {

    @Test
    void workerCountsUntilDurationExpires() throws InterruptedException {
        InterruptibleWorker w = new InterruptibleWorker();
        w.startCounting(100);
        Thread.sleep(200); // wait for natural completion
        assertTrue(w.getCount() > 0, "Worker should have counted at least once");
        assertFalse(w.wasInterrupted(), "Worker should NOT report interrupted for natural exit");
    }

    @Test
    void cancelStopsWorkerEarly() throws InterruptedException {
        InterruptibleWorker w = new InterruptibleWorker();
        w.startCounting(10_000); // 10 seconds — we'll cancel early
        Thread.sleep(50);
        long countBeforeCancel = w.getCount();
        w.cancel();

        assertTrue(w.wasInterrupted(), "Worker should report interrupted after cancel()");
        // Count should have stopped growing
        long countAfterCancel = w.getCount();
        assertEquals(countBeforeCancel, countAfterCancel,
                "No more counting should happen after cancel() joins the thread");
    }

    @Test
    void cancelReturnsQuickly() throws InterruptedException {
        InterruptibleWorker w = new InterruptibleWorker();
        w.startCounting(60_000);
        Thread.sleep(30);

        long start = System.currentTimeMillis();
        w.cancel();
        long elapsed = System.currentTimeMillis() - start;

        assertTrue(elapsed < 2000, "cancel() should return within 2 s (elapsed: " + elapsed + " ms)");
    }

    @Test
    void workerAccumulatesCountOverTime() throws InterruptedException {
        InterruptibleWorker w = new InterruptibleWorker();
        w.startCounting(500);
        Thread.sleep(200);
        long mid = w.getCount();
        Thread.sleep(250); // wait for natural finish
        long end = w.getCount();
        assertTrue(end >= mid, "Count should be non-decreasing");
        assertTrue(end > 0);
    }
}
