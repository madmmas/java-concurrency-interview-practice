# Problem 11 — ReentrantLock

## 🟢 Difficulty: Beginner

## 📖 Background

`ReentrantLock` is an explicit lock that offers everything `synchronized` does,
plus powerful extras:

| Feature | `synchronized` | `ReentrantLock` |
|---------|---------------|----------------|
| Automatic release | ✅ | ❌ (must call `unlock()` in `finally`) |
| Interruptible lock wait | ❌ | ✅ `lockInterruptibly()` |
| Try-lock with timeout | ❌ | ✅ `tryLock(long, TimeUnit)` |
| Fairness option | ❌ | ✅ `new ReentrantLock(true)` |
| Multiple conditions | ❌ | ✅ `lock.newCondition()` |
| Lock held query | ❌ | ✅ `isHeldByCurrentThread()` |

**The golden rule:** Always unlock in a `finally` block:
```java
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();
}
```

**Reentrancy:** A thread that already holds the lock can acquire it again without
deadlocking. The lock tracks a hold count; it is fully released only when
`unlock()` is called the same number of times as `lock()`.

**`Condition`** replaces `wait()`/`notify()` and works with `ReentrantLock`:
```java
Condition notFull  = lock.newCondition();
Condition notEmpty = lock.newCondition();
// inside lock: notFull.await() / notEmpty.signal()
```
Using two separate `Condition` objects is more efficient than `notifyAll()` because
you can signal only producers or only consumers — not everyone at once.

## 🎯 Task

Implement `BankAccount` using `ReentrantLock`:
- `deposit(double amount)` — adds amount; must be positive
- `withdraw(double amount)` — subtracts amount; throws `IllegalStateException` if
  insufficient funds; must be positive
- `getBalance()` — returns current balance
- `transfer(BankAccount target, double amount)` — atomically transfers `amount`
  from this account to `target` without deadlock. Use a consistent lock-ordering
  strategy (lock the account with the lower `id` first).
- Each account gets a unique `long id` assigned at construction.

Implement `BoundedStackWithCondition<T>` using `ReentrantLock` + two `Condition`s:
- `push(T item)` — pushes to the stack; blocks if full
- `pop()` — pops from the stack; blocks if empty
- `peek()` — returns top element without removing; blocks if empty
- `size()` — current number of elements

## 💡 Hints
- For `transfer`, always acquire `Math.min(this.id, target.id)` first to prevent
  circular waits (the classic deadlock-avoidance trick)
- Use `Deque<T>` (e.g., `ArrayDeque`) as the internal stack storage
- Signal only `notFull` after a pop, and only `notEmpty` after a push

## 🧠 Interview Talking Points
- Why must `unlock()` always go in a `finally` block?
- What does "reentrant" mean? Give a use case where reentrancy matters.
- How does `tryLock()` help avoid deadlock?
- What is the difference between `Condition.await()` and `Object.wait()`?
- What is lock fairness and what is its performance cost?
