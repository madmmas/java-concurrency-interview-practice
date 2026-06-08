# Problem 09 — AtomicInteger & Compare-And-Swap (CAS)

## 🟢 Difficulty: Beginner

## 📖 Background

`AtomicInteger` (and the broader `java.util.concurrent.atomic` package) provides
**lock-free** thread safety using hardware-level **Compare-And-Swap (CAS)** instructions.

CAS works like this:
```
compareAndSet(expected, update):
    if current value == expected:
        current value = update
        return true
    else:
        return false   // someone else changed it; retry
```

This avoids the overhead of acquiring a lock while still being thread-safe.

Key `AtomicInteger` methods:
| Method | Description |
|---|---|
| `get()` | Read current value |
| `set(int)` | Write value |
| `getAndIncrement()` | Return old value, then increment |
| `incrementAndGet()` | Increment, then return new value |
| `compareAndSet(expect, update)` | CAS — atomic conditional update |
| `getAndAdd(int)` | Return old value, then add delta |
| `updateAndGet(IntUnaryOperator)` | Apply function atomically |

## 🎯 Task

Implement `LockFreeMetrics`:
- `recordLatency(int ms)` — records a latency measurement (thread-safe, no locks)
- `getTotalCount()` — total number of recordings
- `getTotalLatency()` — sum of all recorded latencies (as a `long`)
- `getMinLatency()` — minimum recorded latency (or `Integer.MAX_VALUE` if none)
- `getMaxLatency()` — maximum recorded latency (or `Integer.MIN_VALUE` if none)
- `reset()` — resets all metrics

**Constraint:** Use only `AtomicInteger`, `AtomicLong`, and `AtomicReference` — **no `synchronized` keyword allowed**.

Implement `LockFreeIdGenerator`:
- `nextId()` — returns monotonically increasing IDs starting from 1, thread-safe, no gaps
- `currentId()` — the last issued ID (0 if none issued yet)

## 💡 Hints
- Use `AtomicLong` for `totalLatency` (it can overflow an `int`)
- For `min`/`max`, use a CAS loop with `AtomicInteger.compareAndSet()`:
  ```java
  int current, next;
  do {
      current = atomicMin.get();
      next = Math.min(current, newValue);
  } while (!atomicMin.compareAndSet(current, next));
  ```
- `nextId()` is a one-liner with `AtomicInteger.incrementAndGet()`

## 🧠 Interview Talking Points
- What is Compare-And-Swap (CAS)?
- What is the ABA problem in CAS? How does `AtomicStampedReference` solve it?
- How does `AtomicInteger` differ from `volatile int` and `synchronized int`?
- When would you still prefer `synchronized` over atomics?
