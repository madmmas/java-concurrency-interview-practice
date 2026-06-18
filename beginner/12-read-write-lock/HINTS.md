# Hints — Problem 12: ReadWriteLock

## Level 1 — Nudge

`ReentrantReadWriteLock` has two locks accessible via `rwLock.readLock()` and `rwLock.writeLock()`. Use the read lock for operations that only observe state; the write lock for operations that modify it. Multiple threads can hold the read lock simultaneously — that is the whole point.

---

## Level 2 — Direction

**`ThreadSafeCache`** pattern for each method type:
```java
// Read operation
rwLock.readLock().lock();
try { return map.get(key); }
finally { rwLock.readLock().unlock(); }

// Write operation
rwLock.writeLock().lock();
try { map.put(key, value); }
finally { rwLock.writeLock().unlock(); }
```
Track read/write counts with `AtomicLong` — these counters are updated under the respective locks, so they could also use plain fields, but `AtomicLong` is simpler.

**`VersionedConfig.updateConfig` — lock downgrade**:
1. Acquire write lock
2. Update the map
3. Acquire the read lock (while still holding write lock — this is allowed)
4. Release the write lock
5. Read the value you just wrote
6. Release the read lock in `finally`

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Concurrent read test is slower than expected | You used the write lock for read operations |
| `updateConfig` returns wrong value | You released the write lock before acquiring the read lock — another thread may have changed it |
| Write count wrong | Forgot to increment counter in `remove()` |

---

## When not to use ReadWriteLock

If write operations are as frequent as reads, the overhead of two locks adds latency without benefit. `ReadWriteLock` shines in read-heavy workloads (e.g., a config store updated once every few minutes but read thousands of times per second).

