# Problem 12 — ReadWriteLock

## 🟢 Difficulty: Beginner

## 📖 Background

`ReentrantReadWriteLock` separates read and write access with two locks:

```
rwLock.readLock().lock()   / rwLock.readLock().unlock()
rwLock.writeLock().lock()  / rwLock.writeLock().unlock()
```

**The rules:**
| Situation | Allowed? |
|-----------|----------|
| Multiple threads reading simultaneously | ✅ Yes |
| One thread writing, no readers | ✅ Yes |
| Thread writing while others read | ❌ No — writer waits |
| Thread reading while another writes | ❌ No — reader waits |

**When to use it:** Data structures that are read far more often than written —
caches, configuration stores, in-memory lookup tables. If reads and writes are
roughly equal, the overhead of two locks may not be worth it.

**Lock downgrading** (advanced): a thread holding a write lock can acquire the
read lock, release the write lock, and continue reading — atomically demoting
itself to a reader. The reverse (upgrading from read → write) is NOT supported.

## 🎯 Task

Implement `ThreadSafeCache<K, V>` backed by `ReentrantReadWriteLock`:
- `put(K key, V value)` — stores a mapping (write operation)
- `get(K key)` — retrieves a value by key, or `null` if absent (read operation)
- `remove(K key)` — removes a key (write operation)
- `containsKey(K key)` — returns whether key exists (read operation)
- `size()` — returns number of entries (read operation)
- `getReadCount()` — total number of `get()` / `containsKey()` / `size()` calls (tracked with `AtomicLong`)
- `getWriteCount()` — total number of `put()` / `remove()` calls

Implement `VersionedConfig` — a configuration store that supports **lock downgrading**:
- `updateConfig(String key, String value)` — acquires write lock, updates value,
  then downgrades to read lock and returns the value just written
- `getConfig(String key)` — reads under read lock
- `getAllKeys()` — returns a snapshot of all keys under read lock

## 💡 Hints
- Use `HashMap` as the backing store — `ReadWriteLock` provides the thread safety
- Read operations: acquire `rwLock.readLock()`, release in `finally`
- Write operations: acquire `rwLock.writeLock()`, release in `finally`
- For lock downgrading in `VersionedConfig.updateConfig()`:
  1. Acquire write lock
  2. Perform update
  3. Acquire read lock (while still holding write lock)
  4. Release write lock
  5. Do your read work
  6. Release read lock in `finally`

## 🧠 Interview Talking Points
- When does `ReadWriteLock` outperform a plain `ReentrantLock`?
- What is lock downgrading? Why is upgrading (read → write) not supported?
- Can readers starve writers (or vice versa)? How does fair mode help?
- What is `StampedLock` and how does it improve upon `ReadWriteLock`?
