# Hints — Problem 29: ScheduledExecutorService

## Level 1 — Nudge

`scheduleAtFixedRate` measures intervals from the *start* of each execution; `scheduleWithFixedDelay` measures from the *end*. For a circuit breaker, the state must be an `AtomicReference<State>` — direct field assignment is not atomic under concurrency.

---

## Level 2 — Direction

**`TaskScheduler`** — wrap each submitted task:
```java
Runnable wrapped = () -> { task.run(); executionCount.incrementAndGet(); };
return scheduler.scheduleAtFixedRate(wrapped, initialDelay, period, MILLISECONDS);
```
The counter increments only on *completion* (not submission).

**`CircuitBreaker` state machine**:
- `CLOSED` → record failures; if failures ≥ threshold → `OPEN` + schedule reset
- `OPEN` → reject all calls immediately
- `HALF_OPEN` → let one call through; success → `CLOSED`; failure → `OPEN` again

```java
// Tripping to OPEN:
if (state.compareAndSet(CLOSED, OPEN)) {
    scheduler.schedule(() -> state.compareAndSet(OPEN, HALF_OPEN),
                       resetTimeoutMs, MILLISECONDS);
}
```

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Execution count less than expected | Wrapped task not counting exceptions — use try/finally in wrapper |
| Circuit breaker stays OPEN forever | Reset task not scheduled, or `compareAndSet(OPEN, HALF_OPEN)` fails because state was already changed |
| HALF_OPEN lets multiple probes through | Not using CAS for the HALF_OPEN → CLOSED transition — multiple threads all see HALF_OPEN and all execute |
