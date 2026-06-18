# Hints — Problem 11: ReentrantLock

## Level 1 — Nudge

`ReentrantLock` must always be unlocked in a `finally` block. If you unlock conditionally or forget the `finally`, an exception inside the critical section will leave the lock permanently held — deadlocking every subsequent caller.

---

## Level 2 — Direction

**`BankAccount.transfer(target, amount)`** deadlock-free pattern:
- Give every account a unique `long id` assigned at construction via `AtomicLong`
- Always lock the account with the *lower* id first:
  ```java
  ReentrantLock first  = this.id < target.id ? this.lock : target.lock;
  ReentrantLock second = this.id < target.id ? target.lock : this.lock;
  first.lock();
  try { second.lock(); try { /* transfer */ } finally { second.unlock(); } }
  finally { first.unlock(); }
  ```

**`BoundedStackWithCondition`**:
- Two `Condition` objects: `notFull = lock.newCondition()`, `notEmpty = lock.newCondition()`
- `push`: lock → while full, `notFull.await()` → push → `notEmpty.signal()` → unlock
- `pop`: lock → while empty, `notEmpty.await()` → pop → `notFull.signal()` → unlock

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Deadlock in `transfer` test | Both accounts lock themselves first — need consistent ordering |
| `await()` throws `IllegalMonitorStateException` | Called `condition.await()` without holding the lock |
| Stack test deadlocks | Using `notifyAll()` on the wrong object, or not calling `signal()` at all |

---

## Reentrancy reminder

If a thread calls `lock.lock()` twice without an unlock, the lock count becomes 2. The lock is not released until `unlock()` is called twice. Forgetting this causes subtle bugs when methods call each other while both holding the same lock.

