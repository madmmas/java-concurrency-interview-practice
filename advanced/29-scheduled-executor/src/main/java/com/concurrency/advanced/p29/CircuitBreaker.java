package com.concurrency.advanced.p29;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Problem 29 – ScheduledExecutorService: Scheduling-Based Circuit Breaker
 *
 * State machine:
 *   CLOSED    → requests pass through; consecutive failures increment a counter
 *   OPEN      → requests rejected immediately; after resetTimeoutMs a scheduled
 *               task transitions state to HALF_OPEN
 *   HALF_OPEN → one probe request is allowed; success → CLOSED, failure → OPEN
 *
 * State transitions use AtomicReference.compareAndSet() for thread safety.
 */
public class CircuitBreaker {

    public enum State { CLOSED, OPEN, HALF_OPEN }

    /** Thrown when a request arrives while the circuit is OPEN. */
    public static class CircuitBreakerOpenException extends RuntimeException {
        public CircuitBreakerOpenException() { super("Circuit breaker is OPEN — request rejected"); }
    }

    private final int  failureThreshold;  // consecutive failures before tripping OPEN
    private final long resetTimeoutMs;    // time in OPEN before transitioning to HALF_OPEN

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "circuit-breaker-scheduler");
                t.setDaemon(true);
                return t;
            });

    private final AtomicReference<State> state        = new AtomicReference<>(State.CLOSED);
    private final AtomicInteger          failureCount  = new AtomicInteger(0);

    public CircuitBreaker(int failureThreshold, long resetTimeoutMs) {
        this.failureThreshold = failureThreshold;
        this.resetTimeoutMs   = resetTimeoutMs;
    }

    /**
     * Executes task if the circuit is CLOSED or HALF_OPEN.
     * Throws CircuitBreakerOpenException if OPEN.
     *
     * On success: calls recordSuccess().
     * On exception: calls recordFailure(), then rethrows.
     */
    public String execute(Callable<String> task) throws Exception {
        // TODO:
        //   if (state.get() == State.OPEN) throw new CircuitBreakerOpenException();
        //   try {
        //       String result = task.call();
        //       recordSuccess();
        //       return result;
        //   } catch (Exception e) {
        //       recordFailure();
        //       throw e;
        //   }
        throw new UnsupportedOperationException("Implement execute()");
    }

    /**
     * Records a successful execution:
     *   HALF_OPEN → CLOSED  (reset failure count to 0)
     *   CLOSED    → reset failure count to 0
     *   OPEN      → no-op (shouldn't happen but safe to ignore)
     */
    public void recordSuccess() {
        // TODO:
        //   state.compareAndSet(State.HALF_OPEN, State.CLOSED);
        //   failureCount.set(0);
        throw new UnsupportedOperationException("Implement recordSuccess()");
    }

    /**
     * Records a failed execution:
     *   CLOSED    → increment failureCount; if >= threshold → trip to OPEN
     *   HALF_OPEN → trip back to OPEN
     *   OPEN      → no-op (already open)
     *
     * When tripping to OPEN, schedule a one-shot task to set state → HALF_OPEN
     * after resetTimeoutMs milliseconds.
     */
    public void recordFailure() {
        // TODO:
        //   State current = state.get();
        //   if (current == State.OPEN) return;
        //
        //   if (current == State.HALF_OPEN) {
        //       tripToOpen();
        //       return;
        //   }
        //
        //   // CLOSED: increment counter
        //   if (failureCount.incrementAndGet() >= failureThreshold) {
        //       tripToOpen();
        //   }
        throw new UnsupportedOperationException("Implement recordFailure()");
    }

    /** Returns the current state of the circuit breaker. */
    public State getState() {
        return state.get();
    }

    /** Shuts down the internal scheduler. */
    public void shutdown() {
        scheduler.shutdownNow();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Transitions to OPEN and schedules the HALF_OPEN reset.
     * Uses CAS so only one caller wins if multiple threads race.
     */
    private void tripToOpen() {
        // TODO:
        //   if (state.getAndSet(State.OPEN) != State.OPEN) {
        //       failureCount.set(0);
        //       scheduler.schedule(
        //           () -> state.compareAndSet(State.OPEN, State.HALF_OPEN),
        //           resetTimeoutMs,
        //           TimeUnit.MILLISECONDS
        //       );
        //   }
        throw new UnsupportedOperationException("Implement tripToOpen()");
    }
}
