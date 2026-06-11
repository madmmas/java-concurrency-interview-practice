package com.concurrency.intermediate.p21;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class DelayedTaskSchedulerTest {

    private DelayedTaskScheduler scheduler;

    @BeforeEach void setUp()                       { scheduler = new DelayedTaskScheduler(); scheduler.start(); }
    @AfterEach  void tearDown() throws Exception   { scheduler.stop(); }

    @Test
    void taskRunsAfterDelay() throws InterruptedException {
        AtomicBoolean ran = new AtomicBoolean(false);
        scheduler.schedule(() -> ran.set(true), 100);
        Thread.sleep(50);
        assertFalse(ran.get(), "Task must not run before delay expires");
        Thread.sleep(150);
        assertTrue(ran.get(), "Task must run after delay expires");
    }

    @Test
    void executedCountTracksCompletions() throws InterruptedException {
        scheduler.schedule(() -> {}, 50);
        scheduler.schedule(() -> {}, 50);
        scheduler.schedule(() -> {}, 50);
        Thread.sleep(300);
        assertEquals(3, scheduler.getExecutedCount());
    }

    @Test
    void shorterDelayRunsFirst() throws InterruptedException {
        AtomicInteger order = new AtomicInteger(0);
        int[] firstRan  = {0};
        int[] secondRan = {0};

        scheduler.schedule(() -> secondRan[0] = order.incrementAndGet(), 200);
        scheduler.schedule(() -> firstRan[0]  = order.incrementAndGet(), 50);

        Thread.sleep(400);
        assertEquals(1, firstRan[0],  "Shorter delay task must run first");
        assertEquals(2, secondRan[0], "Longer delay task must run second");
    }

    @Test
    void zeroDelayRunsImmediately() throws InterruptedException {
        AtomicBoolean ran = new AtomicBoolean(false);
        scheduler.schedule(() -> ran.set(true), 0);
        Thread.sleep(100);
        assertTrue(ran.get(), "Zero-delay task should run essentially immediately");
    }

    @Test
    void delayedTaskGetDelayReturnsPositiveBeforeDeadline() {
        DelayedTaskScheduler.DelayedTask task =
                new DelayedTaskScheduler.DelayedTask(() -> {}, 10_000);
        assertTrue(task.getDelay(TimeUnit.MILLISECONDS) > 0,
                "getDelay() must be positive before deadline");
    }

    @Test
    void delayedTaskCompareToOrdersByRemainingDelay() {
        DelayedTaskScheduler.DelayedTask sooner = new DelayedTaskScheduler.DelayedTask(() -> {}, 100);
        DelayedTaskScheduler.DelayedTask later  = new DelayedTaskScheduler.DelayedTask(() -> {}, 5000);
        assertTrue(sooner.compareTo(later) < 0,
                "Task with shorter delay must sort before task with longer delay");
        assertTrue(later.compareTo(sooner) > 0);
    }
}
