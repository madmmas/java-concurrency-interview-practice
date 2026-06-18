# Hints — Problem 50: Mini Job Scheduler (Capstone)

## Level 1 — Nudge

This problem combines several components you'\''ve built across the expert tier: a priority queue for job ordering, a token bucket for rate limiting, metrics collection, and graceful shutdown. Build each component separately, then wire them together. Resist the urge to do everything in one class.

---

## Level 2 — Direction

**Component breakdown**:
- **Job queue**: `PriorityBlockingQueue<Job>` ordered by priority then submission time (same comparator pattern as problem 38)
- **Delay**: jobs with `delayMs > 0` should not be dequeued before their scheduled time — use a `ScheduledExecutorService` to re-enqueue delayed jobs after the delay elapses
- **Rate limiter**: `TokenBucketRateLimiter` from problem 44 — wrap task submission through it
- **Metrics**: per-`jobType` `AtomicLong` counters for success and failure, same pattern as problem 37
- **Recurring jobs**: after a recurring job completes, re-submit it with the same delay

**`JobHandle`**:
```java
public boolean cancel() {
    if (status.compareAndSet(PENDING, CANCELLED)) { future.cancel(false); return true; }
    return false;
}
```

**Graceful shutdown**: `shutdown()` → set status to `SHUTTING_DOWN` → stop accepting new jobs → `executor.shutdown()` → `executor.awaitTermination(shutdownTimeoutMs, MILLISECONDS)` → set status to `TERMINATED`.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `delayedJobNotImmediate` fails | Job submitted to thread pool immediately instead of after delay |
| `recurringJobRunsMultipleTimes` hangs | Recurring re-submission not happening — check that completed recurring jobs re-enqueue themselves |
| `cancelDoneReturnsFalse` fails | `cancel()` using `set(CANCELLED)` unconditionally instead of CAS from `PENDING` |
| Metrics not counting failures | Exception in job not caught — wrap job body in try/catch and record failure in the catch block |
| `shutdownRejectsNew` throws wrong exception | Not checking scheduler status at the top of `submit()` |
