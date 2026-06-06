# Problem 03 — Synchronized Counter

## 🟢 Difficulty: Beginner

## 📖 Background

This problem demonstrates one of the most fundamental concurrency bugs: the **race condition**.

Consider `count++`. It looks like one operation but is actually three:
1. **Read** `count` from memory
2. **Increment** the value
3. **Write** the new value back

If two threads execute this simultaneously, both may read the same value, both increment it, and both write back the same result — effectively losing one increment. This is called a **lost update** or **race condition**.

The `synchronized` keyword in Java solves this by ensuring **mutual exclusion** — only one thread can execute a synchronized block/method at a time for a given object's monitor (lock).

```java
// NOT thread-safe
public void increment() { count++; }

// Thread-safe
public synchronized void increment() { count++; }
```

## 🎯 Task

Implement `ThreadSafeCounter` with:

- `increment()` — increments the count by 1 (thread-safe)
- `decrement()` — decrements the count by 1 (thread-safe)
- `getCount()` — returns the current count (thread-safe)
- `reset()` — resets count to 0 (thread-safe)
- `incrementBy(int delta)` — increments by the given amount atomically

Also implement `UnsafeCounter` with the **same interface but NO synchronization** — this demonstrates the race condition for comparison.

## 📋 Skeleton

See `src/main/java/com/concurrency/beginner/p03/`

## 💡 Hints

- Use the `synchronized` keyword on methods **or** `synchronized(this) { }` blocks
- Both approaches use the object's intrinsic lock — they are equivalent
- `getCount()` also needs synchronization to guarantee reading the latest value (memory visibility)

## 🧠 Interview Talking Points

- What is a race condition?
- What does `synchronized` guarantee? (mutual exclusion + memory visibility)
- What is the difference between a synchronized method and a synchronized block?
- What is an intrinsic lock / monitor?
- Why is `count++` not atomic in Java?
