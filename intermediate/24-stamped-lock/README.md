# Problem 24 — StampedLock

## 🟡 Difficulty: Intermediate

## 📖 Background

`StampedLock` (Java 8+) improves upon `ReadWriteLock` with a third locking mode:
**optimistic reads** — which require no lock acquisition at all.

### Three modes

```java
StampedLock lock = new StampedLock();

// 1. Write lock — exclusive, like writeLock()
long stamp = lock.writeLock();
try { modify(); }
finally { lock.unlockWrite(stamp); }

// 2. Read lock — shared, like readLock()
long stamp = lock.readLock();
try { read(); }
finally { lock.unlockRead(stamp); }

// 3. Optimistic read — NO lock acquired; validation needed
long stamp = lock.tryOptimisticRead();   // always succeeds, stamp may be 0
double x = this.x;                       // read WITHOUT holding a lock
double y = this.y;
if (!lock.validate(stamp)) {             // was there a write while we were reading?
    // fall back to real read lock
    stamp = lock.readLock();
    try { x = this.x; y = this.y; }
    finally { lock.unlockRead(stamp); }
}
```

### Key differences from `ReadWriteLock`
| Feature | ReadWriteLock | StampedLock |
|---------|--------------|-------------|
| Optimistic reads | ❌ | ✅ |
| Reentrant | ✅ | ❌ |
| Condition support | ✅ | ❌ |
| Lock upgrade (read→write) | ❌ | ✅ `tryConvertToWriteLock(stamp)` |
| Performance (read-heavy) | Good | Better |

**Important:** `StampedLock` is **not reentrant** — a thread that holds a stamp and
tries to acquire another lock of any mode will deadlock.

## 🎯 Task

### `OptimisticPoint`
A 2D point (`double x, y`) protected by `StampedLock` with optimistic reads:

- `move(double newX, double newY)` — write lock; updates both coordinates atomically
- `distanceFromOrigin()` — **optimistic read first**; falls back to read lock if
  a write occurred during the read; returns `Math.sqrt(x*x + y*y)`
- `getX()` / `getY()` — simple read lock

### `StampedCache<K, V>`
A cache that uses `StampedLock` to maximise read throughput:

- `get(K key)` — optimistic read first; if validation fails, fall back to read lock
- `put(K key, V value)` — write lock
- `computeIfAbsent(K key, java.util.function.Function<K,V> mappingFn)` — read first
  (optimistic), if absent try to convert stamp to write lock (`tryConvertToWriteLock`),
  compute and insert; if conversion fails, acquire write lock and retry
- `size()` — read lock
- `getOptimisticHits()` — number of times optimistic read succeeded (no fallback)
- `getReadLockFallbacks()` — number of times optimistic read failed and read lock was taken

## 💡 Hints
- `distanceFromOrigin()` pattern:
  ```java
  long stamp = lock.tryOptimisticRead();
  double x = this.x, y = this.y;
  if (!lock.validate(stamp)) {
      stamp = lock.readLock();
      try { x = this.x; y = this.y; }
      finally { lock.unlockRead(stamp); }
  }
  return Math.sqrt(x*x + y*y);
  ```
- `computeIfAbsent` with `tryConvertToWriteLock`:
  ```java
  long stamp = lock.readLock();
  try {
      V val = map.get(key);
      if (val != null) return val;
      long writeStamp = lock.tryConvertToWriteLock(stamp);
      if (writeStamp != 0) { stamp = writeStamp; /* compute & put */ return val; }
      // conversion failed: unlock read, acquire write
      lock.unlockRead(stamp);
      stamp = lock.writeLock();
      // compute & put
  } finally { lock.unlock(stamp); }
  ```

## 🧠 Interview Talking Points
- What is an optimistic read and when can it return stale data?
- Why is `StampedLock` not reentrant, and what problem does this cause?
- What is `tryConvertToWriteLock` and when does it fail?
- When would you choose `StampedLock` over `ReentrantReadWriteLock`?
- What happens if you forget to call `validate()` after an optimistic read?
