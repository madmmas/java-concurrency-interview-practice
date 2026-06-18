# Hints — Problem 09: AtomicInteger & CAS

## Level 1 — Nudge

You cannot use `synchronized` anywhere in this problem. Every counter field must be an atomic type from `java.util.concurrent.atomic`. For min and max, a simple `AtomicInteger` is not enough by itself — think about what operation lets you conditionally update a value only if it has not changed since you read it.

---

## Level 2 — Direction

**`LockFreeMetrics`**:
- `totalCount`: `AtomicLong.incrementAndGet()`
- `totalLatency`: `AtomicLong.addAndGet(ms)`
- `minLatency`: CAS loop —
  ```java
  int current, next;
  do {
      current = minLatency.get();
      next = Math.min(current, newValue);
  } while (!minLatency.compareAndSet(current, next));
  ```
- `maxLatency`: same pattern with `Math.max`
- Initialise `minLatency` to `Integer.MAX_VALUE`, `maxLatency` to `Integer.MIN_VALUE`

**`LockFreeIdGenerator`**:
- `nextId()` is one line: `return counter.incrementAndGet()`
- `currentId()`: `return counter.get()`

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `getMinLatency()` returns `Integer.MAX_VALUE` after recording | Initialised correctly but CAS loop has wrong condition — should loop while `compareAndSet` returns `false` |
| Concurrent min/max test is flaky | Using `set()` instead of a CAS loop — two threads can both read the same current min and both write, losing one update |
| `nextId()` returns 0 | Called `get()` instead of `incrementAndGet()` |

---

## Why a CAS loop?

The min-tracking problem: Thread A reads `current = 5`, Thread B reads `current = 5`, Thread A does `compareAndSet(5, 3)` — succeeds. Thread B now tries `compareAndSet(5, 4)` — fails (value is 3, not 5). Thread B retries: reads `current = 3`, computes `min(3, 4) = 3`, CAS succeeds. Correct result. Without CAS, Thread B's write (4) would overwrite Thread A's correct answer (3).

