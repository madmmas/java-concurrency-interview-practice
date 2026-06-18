# Hints — Problem 40: Thread-Safe Object Pool

## Level 1 — Nudge

`LinkedBlockingQueue` is the right backing structure — `take()` blocks when empty, `offer()` is non-blocking. Pre-populate it with `maxSize` factory-created objects at construction. For the timed borrow, use `poll(timeout, unit)`.

---

## Level 2 — Direction

**`ObjectPool<T>`**:
```java
private final LinkedBlockingQueue<T> pool;
private volatile boolean shutdown = false;

public ObjectPool(int size, Supplier<T> factory) {
    pool = new LinkedBlockingQueue<>(size);
    for (int i = 0; i < size; i++) pool.offer(factory.get());
}

public T borrow() throws InterruptedException {
    if (shutdown) throw new IllegalStateException("Pool is shut down");
    return pool.take();
}

public T borrow(long timeoutMs) throws InterruptedException {
    return pool.poll(timeoutMs, TimeUnit.MILLISECONDS);
}

public void returnObject(T obj) { pool.offer(obj); }
```

**`borrowResource()`** returns an `AutoCloseable` wrapper:
```java
public PooledResource<T> borrowResource() throws InterruptedException {
    T obj = borrow();
    return new PooledResource<>(obj, this::returnObject);
}
```
`PooledResource.close()` calls `returnObject`.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `availableCount()` wrong | Using `pool.size()` but not accounting for items in transit |
| `borrowBlocksWhenEmpty` test is flaky | Checking `shutdown` after `take()` — check before to get accurate error |
| Auto-return test: pool count does not restore | `close()` not calling `returnObject`, or calling it twice |
