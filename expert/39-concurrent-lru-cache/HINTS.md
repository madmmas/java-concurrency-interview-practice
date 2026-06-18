# Hints — Problem 39: Concurrent LRU Cache

## Level 1 — Nudge

`LinkedHashMap(capacity, loadFactor, true)` — the third argument enables access order. When `accessOrder = true`, getting or putting a key moves it to the end of the iteration order. Overriding `removeEldestEntry` automates eviction. Wrap it with `ReadWriteLock` for thread safety.

---

## Level 2 — Direction

**`ConcurrentLRUCache`** core:
```java
private final Map<K, V> cache = new LinkedHashMap<>(capacity, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
};
private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
```

- `get(key)`: write lock (because accessing a key mutates the access order)
- `put(key, value)`: write lock
- `computeIfAbsent(key, fn)`: write lock — read then potentially write, and we need ordering to be consistent
- `size()`, `containsKey()`: read lock
- `getKeysLRUOrder()`: read lock, return `new ArrayList<>(cache.keySet())`

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| LRU order test fails | Using read lock for `get()` — `LinkedHashMap` with `accessOrder=true` mutates on `get()`, requires write lock |
| `computeIfAbsent` loads twice | Using read lock first then upgrading — `ReadWriteLock` cannot upgrade; use write lock directly |
| `evictsLRUWhenFull` fails | `removeEldestEntry` returns `size() >= capacity` instead of `size() > capacity` (off-by-one) |
