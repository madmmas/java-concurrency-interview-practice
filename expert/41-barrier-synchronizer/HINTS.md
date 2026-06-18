# Hints — Problem 41: Custom Barrier

## Level 1 — Nudge

`CyclicBarrier` reimplemented from scratch. You need: a count of how many parties have arrived, a generation object to detect when the barrier has been broken or reset, a `Condition` to wait on, and logic to trip the barrier when all parties arrive.

---

## Level 2 — Direction

**Core fields**:
```java
private final ReentrantLock lock = new ReentrantLock();
private final Condition trip = lock.newCondition();
private final int parties;
private final Runnable barrierAction;
private int count;         // parties still needed
private Generation generation = new Generation();

private static class Generation { boolean broken = false; }
```

**`await()`**:
```java
lock.lock();
try {
    Generation g = generation;
    if (--count == 0) {           // last to arrive
        if (barrierAction != null) barrierAction.run();
        nextGeneration();         // reset count, new generation, signalAll
        return 0;
    }
    while (count > 0 && !g.broken) trip.await();
    if (g.broken) throw new BrokenBarrierException();
    return count;
} finally { lock.unlock(); }
```

**`reset()`**: mark current generation broken, `trip.signalAll()`, then start a fresh generation.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `resetBreaksWaiters` — exception not thrown | Generation'\''s `broken` flag not checked after `await()` returns |
| `reusableAcrossGenerations` fails | `nextGeneration()` not resetting `count` back to `parties` |
| `timedAwaitThrows` does not throw `TimeoutException` | Using `trip.await()` instead of `trip.await(timeout, unit)` |
