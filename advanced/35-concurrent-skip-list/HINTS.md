# Hints — Problem 35: Concurrent Skip List

## Level 1 — Nudge

A skip list is a multi-level linked list where higher levels skip over many nodes for fast search. The `add` and `remove` operations need to find predecessor nodes at each level and link/unlink nodes. The lock-based version uses per-node locks acquired in order (hand-over-hand). The lock-free version marks nodes logically deleted before physically removing them.

---

## Level 2 — Direction

**Lock-based `add`**:
1. `find(key)` — traverse from the highest level down, collecting `preds[]` and `succs[]` arrays
2. If key already present at level 0, return false
3. Create new node with random height (geometric distribution with p=0.5)
4. Lock all relevant predecessor nodes (in order)
5. Link the new node into each level

**Lock-free `remove`** (two phases):
1. *Logical delete*: mark the node'\''s `next` references using `AtomicMarkableReference.attemptMark(next, true)`
2. *Physical delete*: CAS the predecessor'\''s next from the marked node to its successor

**Random level**: `int level = 1; while (level < MAX_LEVEL && random.nextBoolean()) level++;`

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `contains` returns true for removed element | Physical deletion not happening — only logical mark set |
| `add` returns false for a new key | `find()` traverses the wrong direction or misidentifies predecessors |
| Lock-free version deadlocks | Trying to lock nodes in the lock-free variant — the lock-free version must never use `synchronized` |
| `toSortedList` misses elements | Only traversing level 0 but starting from a wrong sentinel node |
