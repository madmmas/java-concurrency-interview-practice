# Problem 25 — ConcurrentHashMap Deep Dive

## 🟡 Difficulty: Intermediate

## 📖 Background

`ConcurrentHashMap` (CHM) is the gold standard thread-safe map in Java. Understanding
its atomic operations is essential for interviews.

### Why not `HashMap` or `Collections.synchronizedMap`?
- `HashMap` — not thread-safe at all
- `synchronizedMap` — wraps every method in a global lock; iterations must be
  manually locked; poor throughput
- `ConcurrentHashMap` — per-bucket locking (Java 8+: CAS + `synchronized` on the
  first bucket node); reads require **no lock at all**

### Key atomic operations

```java
// Atomic check-then-act
map.putIfAbsent(key, value)          // put only if key absent; returns existing or null
map.computeIfAbsent(key, fn)         // compute and insert only if absent; returns value
map.computeIfPresent(key, fn)        // update only if key present; returns new value or null
map.compute(key, fn)                 // always update; fn can return null to remove
map.merge(key, value, remappingFn)   // merge logic

// Atomic compare-and-replace
map.replace(key, oldVal, newVal)     // CAS-style; returns true only if old value matches
map.remove(key, value)               // removes only if current value == value

// Bulk operations (Java 8+)
map.forEach(parallelismThreshold, (k, v) -> ...)
map.reduce(parallelismThreshold, transformer, reducer)
map.search(parallelismThreshold, (k, v) -> ...)

// Aggregate
map.mappingCount()   // prefer over size() for large maps (avoids int overflow)
```

### `compute` vs `merge`
- `compute(key, (k, oldVal) -> newVal)` — oldVal is `null` if key absent
- `merge(key, value, (oldVal, v) -> combined)` — only called if key exists; use
  for accumulation patterns

## 🎯 Task

### `WordFrequencyCounter`
Counts word frequencies across multiple documents in parallel:

- `addDocument(String text)` — splits text by whitespace, increments count for
  each word using `map.merge(word, 1, Integer::sum)` — thread-safe, no synchronization needed
- `getCount(String word)` — returns frequency, or 0 if not seen
- `getTopN(int n)` — returns the N words with the highest frequency as `List<String>`
  (sorted descending by count, then alphabetically for ties)
- `processDocuments(List<String> documents)` — submits each document to an
  `ExecutorService` in parallel and waits for all to finish

### `ConcurrentInventory`
A thread-safe inventory system using CHM atomic operations:

- `addItem(String item, int quantity)` — adds/increases stock; use `merge`
- `removeItem(String item, int quantity)` — decreases stock; use `compute`;
  removes the key entirely when quantity reaches 0; throws `IllegalStateException`
  if stock would go negative
- `reserveItem(String item, int quantity)` — atomically checks and reserves
  (decreases stock) only if enough is available; returns `true` if successful,
  `false` if insufficient stock — use `compute` or `replace(key, oldVal, newVal)` in a CAS loop
- `getStock(String item)` — returns current stock, or 0 if not present
- `getSnapshot()` — returns an unmodifiable copy of the entire inventory map

## 💡 Hints
- `WordFrequencyCounter.addDocument`: split with `text.split("\\s+")`, filter blanks,
  then `map.merge(word, 1, Integer::sum)` — this is fully atomic
- `getTopN`: stream the entrySet, sort by value descending then key ascending, limit to n,
  map to keys, collect to list
- `ConcurrentInventory.reserveItem`: a simple `compute` that checks the current value
  and returns `currentQty - quantity` if sufficient (or returns `currentQty` unchanged
  with a side-effect boolean — use an `AtomicBoolean` to capture success)

## 🧠 Interview Talking Points
- What is the concurrency level of `ConcurrentHashMap` in Java 8+?
- Why is `size()` eventually-consistent in `ConcurrentHashMap`?
- What is the difference between `putIfAbsent` and `computeIfAbsent`?
- Why is the `compute` lambda called under a lock? What must it never do?
- How does `merge` differ from `compute`?
- Why is iterating a `ConcurrentHashMap` safe without external synchronization?
