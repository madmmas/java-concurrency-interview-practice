# Problem 35 — Concurrent Skip List

## 🔴 Difficulty: Advanced

## 📖 Background

A **skip list** is a probabilistic data structure invented by William Pugh (1990)
that achieves O(log n) average-case search, insert, and delete — without the
complexity of balanced trees — by maintaining multiple layers of linked lists:

```
Level 3:  head ──────────────────────────── 50 ────────────── tail
Level 2:  head ───────── 20 ─────────────── 50 ──── 70 ────── tail
Level 1:  head ── 10 ─── 20 ─── 30 ─── 40 ─ 50 ─── 70 ─ 80 ─ tail
```

Each element appears at level 1 always. With probability p (typically 0.5),
it also appears at level 2; with probability p² at level 3, and so on.

### Search algorithm
Start at the top level. At each node, look at the next node on the same level:
- If `next.key < target` → advance forward on the same level
- If `next.key >= target` → drop down one level
- Repeat until level 0 is reached

### Lock-free concurrent skip list
Java's `ConcurrentSkipListMap` uses a lock-free implementation. The key insight:

- **Insertion** is a two-phase commit: first link the node at level 0, then
  add it to higher levels. Each link operation uses CAS.
- **Deletion** uses *marking*: before physically unlinking a node, set a
  `deleted` flag (via CAS on the next pointer). Marked nodes are skipped during
  traversal and cleaned up lazily.
- **Linearisation point**: insertion's linearisation point is the CAS that links
  the node at level 0.

For this problem we build a **lock-based** concurrent skip list (simpler but
still instructive) and then a **lock-free** variant for the advanced section.

## 🎯 Task

### `ConcurrentSkipListSet<T extends Comparable<T>>`

A thread-safe sorted set backed by a skip list:

- `add(T key)` — inserts key; returns `true` if newly inserted, `false` if already present
- `remove(T key)` — removes key; returns `true` if removed, `false` if not found
- `contains(T key)` — returns `true` if key is in the set
- `size()` — returns the current number of elements
- `toSortedList()` — returns all elements in ascending order (snapshot)
- `first()` / `last()` — min and max elements, or throws `NoSuchElementException`

**Implementation approach — lock-based with per-node `ReentrantLock`s:**
- Each `Node` holds a `ReentrantLock`
- `add` and `remove` acquire locks on predecessor nodes (hand-over-hand locking)
- `contains` is optimistic (no lock)
- Maximum height `MAX_LEVEL = 16`; promote with probability `p = 0.5`

### `LockFreeSkipListSet<T extends Comparable<T>>`

A lock-free variant using `AtomicMarkableReference<Node<T>>`:

- Same API as above
- Each `next` pointer is an `AtomicMarkableReference<Node<T>>`
- `remove` uses a two-phase approach:
  1. Logical delete: CAS the `next[0]` reference to mark it deleted
  2. Physical delete: help unlink the node during subsequent traversals
- `add` uses CAS to link the new node
- `contains` reads without any CAS (wait-free read path)

### `SkipListBenchmark`

Compares the two implementations under concurrent load:

- `benchmarkAdd(SkipListSet set, int threads, int keysPerThread)` — all threads
  concurrently add keys; returns time in ms
- `benchmarkMixed(SkipListSet set, int threads, int ops, int readPct)` — mixed
  read/write workload; `readPct`% of ops are `contains()`, rest are `add`/`remove`

## 💡 Hints

### Node structure (lock-based)
```java
class Node<T> {
    final T key;
    final Node<T>[] next;   // next[i] = successor at level i
    final ReentrantLock lock = new ReentrantLock();
    volatile boolean deleted = false;

    Node(T key, int height) {
        this.key  = key;
        this.next = new Node[height];
    }
}
```

### `add` — hand-over-hand locking
```
1. Find predecessor nodes at each level (update[])
2. Lock update[0] and update[0].next
3. Check update[0].next.key == key → already present, unlock and return false
4. Create newNode with random height
5. Link newNode at level 0 (CAS or simple assignment under lock)
6. Unlock level 0
7. For levels 1..newNode.height-1: lock update[i], link, unlock
```

### Random level generation
```java
private int randomLevel() {
    int level = 1;
    while (level < MAX_LEVEL && random.nextDouble() < 0.5) level++;
    return level;
}
```

### `LockFreeSkipListSet` — `AtomicMarkableReference`
```java
// Check if logically deleted
boolean[] marked = {false};
Node<T> succ = curr.next[i].get(marked);
if (marked[0]) { /* node is deleted — help remove it */ }
```

### `find` helper (lock-free)
Returns `{pred, curr}` at level 0 after cleaning up deleted nodes along the way.
This is the core helper used by `add`, `remove`, and `contains`.

## 🧠 Interview Talking Points

- What is the expected time complexity of skip list operations and why?
- How does logical deletion (marking) help avoid the ABA problem in lock-free skip lists?
- What is linearisability and what is the linearisation point of `add` in this implementation?
- How does `ConcurrentSkipListMap` in Java achieve wait-free reads?
- Compare skip lists to balanced BSTs (e.g., red-black trees) for concurrent workloads.
- What is hand-over-hand (lock coupling) locking and when is it used?
