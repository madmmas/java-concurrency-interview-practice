# Hints — Problem 20: ForkJoinPool

## Level 1 — Nudge

The fork-join pattern: split the problem in two, fork one half onto another thread, compute the other half on the current thread, then join the forked half and combine. The key is to fork *one* and compute *one* — forking both and then joining both means you'\''re waiting for both, gaining nothing on the current thread.

---

## Level 2 — Direction

**`ParallelSum`** pattern:
```java
if (to - from <= THRESHOLD) {
    // sequential sum
} else {
    int mid = (from + to) / 2;
    ParallelSum right = new ParallelSum(array, mid, to);
    right.fork();                          // right goes to another thread
    long leftResult = new ParallelSum(array, from, mid).compute();  // compute left here
    return leftResult + right.join();      // join right
}
```

**`ParallelMergeSort`**: use `RecursiveAction` (no return value), fork left half, compute right half (in-place sort of a copy), join left, then merge both halves into a temporary array and copy back.

**`ParallelSearch`**: fork both halves, join left first — if result ≥ 0, cancel right and return; otherwise return right'\''s result.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Performance not better than sequential | Threshold too low — too many tiny tasks; or forking both halves and joining both |
| Sort result wrong | Merging in-place into the original array while still reading from it |
| Search returns -1 for present element | Checking right result even when left already found it |
