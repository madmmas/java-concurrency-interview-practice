# Hints — Problem 10: ThreadLocal

## Level 1 — Nudge

`ThreadLocal` gives each thread its own independent copy of a variable. No synchronization needed for the value itself — threads literally access different objects. The critical question is: what happens to that object when a thread pool reuses the thread for a new task?

---

## Level 2 — Direction

**`RequestContext`**:
```java
private static final ThreadLocal<String> userId = new ThreadLocal<>();
private static final ThreadLocal<String> requestId = new ThreadLocal<>();
```
- `setUserId(s)` → `userId.set(s)`
- `getUserId()` → `userId.get()`
- `clear()` → `userId.remove(); requestId.remove()` — *always* remove both

**`PerThreadFormatter`**:
```java
private static final AtomicInteger instanceCount = new AtomicInteger(0);
private static final ThreadLocal<DecimalFormat> formatter =
    ThreadLocal.withInitial(() -> {
        instanceCount.incrementAndGet();
        return new DecimalFormat("0.00");
    });
```
- `format(value)` → `formatter.get().format(value)`
- `getFormatterInstanceCount()` → `instanceCount.get()`

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Different threads see each other's userId | Using a regular `static` field instead of `ThreadLocal` |
| Instance count never exceeds 1 | Sharing one instance instead of using `ThreadLocal` |
| Memory leak warning in pooled thread test | Missing `remove()` call in `clear()` |

---

## The memory leak

In a thread pool, threads live for the lifetime of the pool. A `ThreadLocal` value set in one task survives to the next task on the same thread. If you store a large object (or a reference to a `ClassLoader`) and never call `remove()`, that object is never garbage collected for the life of the pool. Always call `remove()` in a finally block.

