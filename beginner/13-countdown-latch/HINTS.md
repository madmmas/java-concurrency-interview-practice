# Hints — Problem 13: CountDownLatch

## Level 1 — Nudge

`CountDownLatch` is a one-shot counter. Once it reaches zero it stays there — it cannot be reset. The two classic patterns are: (1) one thread awaits while N others count down, and (2) N threads await while one fires the starting gun. Figure out which pattern fits each class.

---

## Level 2 — Direction

**`ServiceStartupCoordinator`**:
- Create the latch sized to the number of registered services
- `serviceReady(name)` calls `latch.countDown()` and increments an `AtomicInteger` for `getReadyCount()`
- `waitForAll()` calls `latch.await()`
- `waitForAll(timeoutMs)` calls `latch.await(timeoutMs, TimeUnit.MILLISECONDS)` and returns the boolean result

**`RaceStartGun`**:
- Internal latch initialised to `1`
- `register()` calls `latch.await()` — the calling thread blocks here
- `fire()` calls `latch.countDown()` — unblocks all waiting threads at once
- `getWaitingCount()`: track registrations with an `AtomicInteger`, decrement when `await()` unblocks (use try/finally around the await)

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `waitForAll` returns before all services are ready | Latch sized incorrectly — must equal the number of registered services |
| `waitForAll(timeout)` always returns `false` | Not checking the boolean return value of `await(timeout, unit)` |
| `getWaitingCount()` is always 0 | Counter incremented after await returns instead of before |

---

## One-shot limitation

Once a `CountDownLatch` reaches zero it cannot be reused. If you need a reusable barrier (same threads synchronize at the same point multiple times), use `CyclicBarrier` instead.

