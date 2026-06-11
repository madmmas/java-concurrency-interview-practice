# Problem 29 — ScheduledExecutorService

## 🔴 Difficulty: Advanced

## 📖 Background

`ScheduledExecutorService` extends `ExecutorService` with time-based scheduling:

```java
ScheduledExecutorService s = Executors.newScheduledThreadPool(2);

// One-shot after delay
ScheduledFuture<?> f = s.schedule(task, 500, MILLISECONDS);

// Fixed-rate: next fire = start + n*period (may overlap if task is slow)
ScheduledFuture<?> fr = s.scheduleAtFixedRate(task, 0, 1, SECONDS);

// Fixed-delay: next fire = last-completion + delay (no overlap)
ScheduledFuture<?> fd = s.scheduleWithFixedDelay(task, 0, 1, SECONDS);

f.cancel(false);          // stop future executions; false = don't interrupt
f.getDelay(MILLISECONDS); // time until next execution
```

### `scheduleAtFixedRate` vs `scheduleWithFixedDelay`

| | `scheduleAtFixedRate` | `scheduleWithFixedDelay` |
|---|---|---|
| Next fire time | `startTime + n × period` | `lastCompletionTime + delay` |
| Can tasks overlap? | Yes (if task > period) | No |
| Drift under load | No drift | Drifts if task is slow |
| Use case | Heartbeat, metrics, polling | Retry-after-completion |

## 🎯 Task

### `TaskScheduler`
A façade over `ScheduledExecutorService` with execution tracking:
- `scheduleOnce(Runnable, long delayMs)` → `ScheduledFuture<?>`
- `scheduleAtFixedRate(Runnable, long initialDelayMs, long periodMs)` → `ScheduledFuture<?>`
- `scheduleWithFixedDelay(Runnable, long initialDelayMs, long delayMs)` → `ScheduledFuture<?>`
- `cancelTask(ScheduledFuture<?>)` → `boolean`
- `getExecutionCount()` — total completed executions
- `shutdown()`

### `CircuitBreaker`
Scheduling-based circuit breaker with CLOSED → OPEN → HALF_OPEN state machine:
- `execute(Callable<String>)` — runs if CLOSED/HALF_OPEN; throws `CircuitBreakerOpenException` if OPEN
- `recordSuccess()` / `recordFailure()` — drive state transitions
- Tripping to OPEN schedules a one-shot task to set state → HALF_OPEN after `resetTimeoutMs`
- `getState()` — current `State` enum value
- `shutdown()` — stop the internal scheduler

## 💡 Hints
- `TaskScheduler`: wrap each task with `() -> { task.run(); executionCount.incrementAndGet(); }`
  before submitting so counter reflects **completions**, not submissions
- `CircuitBreaker`: keep state in `AtomicReference<State>`; use `compareAndSet` for transitions;
  when tripping OPEN, call `scheduler.schedule(() -> state.set(HALF_OPEN), resetTimeoutMs, MILLISECONDS)`
- Half-open probe: only the very first call through changes state from HALF_OPEN → CLOSED or
  back to OPEN; subsequent callers in HALF_OPEN should be treated as CLOSED until state flips

## 🧠 Interview Talking Points
- What is the difference between `scheduleAtFixedRate` and `scheduleWithFixedDelay`?
- If a fixed-rate task takes longer than its period, what happens to subsequent fires?
- How do you cancel a recurring task without shutting down the executor?
- What exception does `ScheduledFuture.get()` throw if cancelled?
- How would you implement a circuit breaker without `ScheduledExecutorService`?
