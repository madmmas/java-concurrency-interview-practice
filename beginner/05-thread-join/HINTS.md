# Hints — Problem 05: Thread Join

## Level 1 — Nudge

`join()` causes the calling thread to wait until the target thread has terminated. You need a way to get a result *out* of a thread. Standard `Thread` does not return values — you need a field on the thread (or a wrapper object) that the thread writes before it exits.

---

## Level 2 — Direction

**`sumArray`**: create two anonymous `Thread` subclasses (or two `Runnable`s with a shared result holder), each summing one half of the array. Store partial sums in `long[]` arrays or instance fields. Start both, join both, add the two partial sums.

**`findMax`**: same pattern as `sumArray`, but track the max of each half.

**`runWithTimeout`**:
```java
Thread t = new Thread(task);
t.start();
t.join(timeoutMillis);
return !t.isAlive();  // true = completed, false = still running
```
`join(millis)` returns after the timeout *or* when the thread finishes — whichever comes first. Check `isAlive()` to distinguish the two cases.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Sum/max is always 0 | You read the result field before joining — happens-before not established yet |
| `runWithTimeout` always returns `true` | You called `join()` without a timeout, so it waits forever |
| `runWithTimeout` always returns `false` | You never called `t.start()` before `t.join(timeout)` |

---

## The happens-before guarantee

After `thread.join()` returns, everything the joined thread wrote is guaranteed to be visible to the joining thread. This is why you can safely read the partial-sum field without `volatile` — as long as you read it after `join()`.

