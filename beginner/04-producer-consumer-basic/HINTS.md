# Hints — Problem 04: Producer-Consumer (Basic)

## Level 1 — Nudge

You need a buffer that blocks callers: `put` blocks when full, `take` blocks when empty. Java's `Object` class has primitives for blocking and signalling: `wait()`, `notify()`, and `notifyAll()`. These must be called inside a `synchronized` block.

---

## Level 2 — Direction

**Structure**: use a `LinkedList` or `ArrayDeque` internally. The capacity is set in the constructor.

**`put(item)`**:
```
synchronized(this) {
    while (buffer.size() == capacity) wait();
    buffer.add(item);
    notifyAll();
}
```

**`take()`**:
```
synchronized(this) {
    while (buffer.isEmpty()) wait();
    T item = buffer.remove();
    notifyAll();
    return item;
}
```

Use `while` — not `if` — before every `wait()`. This is critical.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `wait()` throws `IllegalMonitorStateException` | You called `wait()` outside a `synchronized` block |
| Thread wakes up but buffer is still full/empty | Using `if` instead of `while` before `wait()` — spurious wakeup check missing |
| Test deadlocks | Using `notify()` instead of `notifyAll()` — wrong thread woken, everyone goes back to sleep |

---

## Why `while` and not `if`?

Two reasons: (1) spurious wakeups — the JVM is permitted to wake a thread without anyone calling `notify()`; (2) another thread may consume the item between `notifyAll()` and when your thread reacquires the lock. The `while` re-checks the condition after every wakeup.

