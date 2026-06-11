package com.concurrency.advanced.p29;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ScheduledExecutorTest {

    // ── TaskScheduler ─────────────────────────────────────────────────────────

    private TaskScheduler scheduler;

    @BeforeEach void setUp()                     { scheduler = new TaskScheduler(2); }
    @AfterEach  void tearDown() throws Exception { scheduler.shutdown(); }

    @Test
    void scheduleOnceRunsAfterDelay() throws InterruptedException {
        AtomicInteger ran = new AtomicInteger(0);
        scheduler.scheduleOnce(ran::incrementAndGet, 100);
        Thread.sleep(50);
        assertEquals(0, ran.get(), "Task must not run before the delay");
        Thread.sleep(200);
        assertEquals(1, ran.get(), "Task must run exactly once after the delay");
    }

    @Test
    void scheduleOnceIncrementsExecutionCount() throws InterruptedException {
        scheduler.scheduleOnce(() -> {}, 50);
        Thread.sleep(250);
        assertEquals(1, scheduler.getExecutionCount());
    }

    @Test
    void scheduleAtFixedRateRunsRepeatedly() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        ScheduledFuture<?> f = scheduler.scheduleAtFixedRate(count::incrementAndGet, 0, 80);
        Thread.sleep(450);
        f.cancel(false);
        assertTrue(count.get() >= 4,
                "Fixed-rate task must run ≥4 times in 450 ms at 80 ms period, got: " + count.get());
        assertTrue(scheduler.getExecutionCount() >= 4);
    }

    @Test
    void scheduleWithFixedDelayRunsRepeatedly() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        ScheduledFuture<?> f = scheduler.scheduleWithFixedDelay(count::incrementAndGet, 0, 60);
        Thread.sleep(400);
        f.cancel(false);
        assertTrue(count.get() >= 3,
                "Fixed-delay task must run multiple times, got: " + count.get());
    }

    @Test
    void cancelPreventsExecution() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        ScheduledFuture<?> f = scheduler.scheduleOnce(count::incrementAndGet, 500);
        boolean cancelled = scheduler.cancelTask(f);
        assertTrue(cancelled, "Task must be cancellable before its delay expires");
        Thread.sleep(700);
        assertEquals(0, count.get(), "Cancelled task must not execute");
    }

    @Test
    void multipleScheduledTasksCountIndependently() throws InterruptedException {
        scheduler.scheduleOnce(() -> {}, 30);
        scheduler.scheduleOnce(() -> {}, 30);
        scheduler.scheduleOnce(() -> {}, 30);
        Thread.sleep(300);
        assertEquals(3, scheduler.getExecutionCount(),
                "Execution count must reflect all completed one-shot tasks");
    }

    // ── CircuitBreaker ────────────────────────────────────────────────────────

    private CircuitBreaker cb;

    @BeforeEach void setUpCB()  { cb = new CircuitBreaker(3, 300); }
    @AfterEach  void tearDownCB() { cb.shutdown(); }

    @Test
    void initialStateIsClosed() {
        assertEquals(CircuitBreaker.State.CLOSED, cb.getState());
    }

    @Test
    void successfulCallsDoNotTripBreaker() throws Exception {
        cb.execute(() -> "ok");
        cb.execute(() -> "ok");
        assertEquals(CircuitBreaker.State.CLOSED, cb.getState());
    }

    @Test
    void threeFailuresTripsToOpen() {
        for (int i = 0; i < 3; i++) {
            try { cb.execute(() -> { throw new RuntimeException("fail"); }); }
            catch (Exception ignored) {}
        }
        assertEquals(CircuitBreaker.State.OPEN, cb.getState(),
                "3 consecutive failures must trip the circuit OPEN");
    }

    @Test
    void openCircuitRejectsRequests() {
        for (int i = 0; i < 3; i++) {
            try { cb.execute(() -> { throw new RuntimeException(); }); }
            catch (Exception ignored) {}
        }
        assertThrows(CircuitBreaker.CircuitBreakerOpenException.class,
                () -> cb.execute(() -> "should be rejected"),
                "OPEN circuit must throw CircuitBreakerOpenException");
    }

    @Test
    void openCircuitTransitionsToHalfOpenAfterTimeout() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            try { cb.execute(() -> { throw new RuntimeException(); }); }
            catch (Exception ignored) {}
        }
        assertEquals(CircuitBreaker.State.OPEN, cb.getState());
        Thread.sleep(450);   // wait past 300 ms reset timeout
        assertEquals(CircuitBreaker.State.HALF_OPEN, cb.getState(),
                "Circuit must transition to HALF_OPEN after reset timeout");
    }

    @Test
    void successfulProbeClosesCircuit() throws Exception {
        for (int i = 0; i < 3; i++) {
            try { cb.execute(() -> { throw new RuntimeException(); }); }
            catch (Exception ignored) {}
        }
        Thread.sleep(450);
        assertEquals(CircuitBreaker.State.HALF_OPEN, cb.getState());
        cb.execute(() -> "probe ok");
        assertEquals(CircuitBreaker.State.CLOSED, cb.getState(),
                "Successful probe in HALF_OPEN must close the circuit");
    }

    @Test
    void failedProbeReopensCircuit() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            try { cb.execute(() -> { throw new RuntimeException(); }); }
            catch (Exception ignored) {}
        }
        Thread.sleep(450);
        assertEquals(CircuitBreaker.State.HALF_OPEN, cb.getState());
        try { cb.execute(() -> { throw new RuntimeException("probe fail"); }); }
        catch (Exception ignored) {}
        assertEquals(CircuitBreaker.State.OPEN, cb.getState(),
                "Failed probe in HALF_OPEN must reopen the circuit");
    }
}
