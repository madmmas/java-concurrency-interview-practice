# Problem 10 — ThreadLocal

## 🟢 Difficulty: Beginner

## 📖 Background

`ThreadLocal<T>` gives each thread its own private copy of a variable. No sharing,
no synchronization required — each thread sees only its own value.

```java
ThreadLocal<Integer> local = ThreadLocal.withInitial(() -> 0);

// Thread A sets 1  → Thread A sees 1, Thread B still sees 0
// Thread B sets 99 → Thread B sees 99, Thread A still sees 1
```

Key API:
| Method | Description |
|---|---|
| `get()` | Returns this thread's value (initializes on first call) |
| `set(T value)` | Sets this thread's value |
| `remove()` | Removes this thread's value (crucial for thread pools!) |
| `withInitial(Supplier)` | Factory with a default value per thread |

**Critical pitfall — thread pool memory leak:**
In a thread pool, threads are reused. If you `set()` a `ThreadLocal` and never call
`remove()`, the next task on the same thread sees stale data. Always call `remove()`
in a `finally` block when using `ThreadLocal` in pooled threads.

**Real-world uses:**
- `SimpleDateFormat` (not thread-safe, expensive to create — store one per thread)
- Per-request context in web frameworks (user ID, transaction ID, locale)
- Database connection / transaction management

## 🎯 Task

Implement `RequestContext`:
- `setUserId(String userId)` — stores a user ID in the current thread's context
- `getUserId()` — returns the current thread's user ID (or `null` if not set)
- `setRequestId(String requestId)` — stores a request ID
- `getRequestId()` — returns the current thread's request ID
- `clear()` — removes both values from the current thread (call this in finally blocks!)

Implement `PerThreadFormatter`:
- `format(double value)` — formats a `double` to 2 decimal places using a
  **per-thread** `DecimalFormat` instance. Must be thread-safe without synchronization.
- `getFormatterInstanceCount()` — returns how many distinct `DecimalFormat` instances
  have been created across all threads (to verify one-per-thread behaviour)

## 💡 Hints
- Use `ThreadLocal.withInitial(() -> new DecimalFormat("0.00"))` for the formatter
- Track instance count with `AtomicInteger` incremented inside the initializer lambda
- `clear()` must call `remove()` on each `ThreadLocal` separately

## 🧠 Interview Talking Points
- Why is `ThreadLocal` important in thread-pool environments?
- What memory leak can `ThreadLocal` cause and how do you prevent it?
- How does `InheritableThreadLocal` differ from `ThreadLocal`?
- Name two real-world frameworks that use `ThreadLocal` internally.
