# Problem 04 — Producer-Consumer (Basic)

## 🟢 Difficulty: Beginner

## 📖 Background

Producer-Consumer is a classic concurrency pattern where:
- **Producers** generate data and place it in a shared buffer
- **Consumers** take data from the buffer and process it

The challenge: the buffer has a fixed capacity. Producers must **wait** when it's full, and consumers must **wait** when it's empty.

Java's `Object` class provides low-level coordination primitives:
- `wait()` — releases the lock and suspends the calling thread until notified
- `notify()` — wakes up one waiting thread
- `notifyAll()` — wakes up all waiting threads

**Critical rule:** `wait()` and `notify()` must ALWAYS be called from within a `synchronized` block on the same object.

```java
synchronized (lock) {
    while (condition) {   // ALWAYS use while, not if (spurious wakeups!)
        lock.wait();
    }
    // ... do work ...
    lock.notifyAll();
}
```

## 🎯 Task

Implement `BoundedBuffer<T>` — a thread-safe bounded queue:

- `put(T item)` — adds item to the buffer; **blocks** if buffer is full until space is available
- `take()` — removes and returns an item; **blocks** if buffer is empty until an item is available
- `size()` — returns the current number of items in the buffer
- `isEmpty()` — returns true if the buffer has no items
- `isFull()` — returns true if the buffer is at capacity

## 📋 Skeleton

See `src/main/java/com/concurrency/beginner/p04/BoundedBuffer.java`

## 💡 Hints

- Use a `LinkedList` (or `ArrayDeque`) internally to store items
- Use `wait()` in a `while` loop (not `if`) — this guards against **spurious wakeups**
- Call `notifyAll()` after each `put` or `take` to wake waiting producers/consumers
- The capacity is fixed at construction time

## 🧠 Interview Talking Points

- Why use `while` instead of `if` before `wait()`?
- What is a spurious wakeup?
- When would you use `notify()` vs `notifyAll()`?
- How does `BlockingQueue` in `java.util.concurrent` relate to this problem?
