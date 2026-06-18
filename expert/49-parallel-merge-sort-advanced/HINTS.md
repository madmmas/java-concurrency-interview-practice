# Hints — Problem 49: Parallel Merge Sort (Advanced)

## Level 1 — Nudge

This is a production-quality parallel merge sort, not the basic version from problem 20. Key differences: the sort must not mutate the input array, it uses a generic `Comparator`, it has an adaptive threshold based on available processors, and the merge uses binary search to find split points.

---

## Level 2 — Direction

**`adaptiveThreshold(arrayLength)`**: return `Math.max(1024, arrayLength / (4 * Runtime.getRuntime().availableProcessors()))` — at least 1024 elements, but larger for bigger arrays to avoid excessive task overhead.

**`sort(array, comparator, pool)`**:
1. Copy input to a working array — do not mutate the original
2. Create a `RecursiveAction` that sorts a range of the working array
3. Submit to the provided `ForkJoinPool`
4. Return the working array (not the original)

**`sequentialMerge(src, lo, mid, hi, dest, comparator)`**:
- Standard merge of `src[lo..mid)` and `src[mid..hi)` into `dest[lo..hi)`
- Binary search (`binarySearch`) can optimise by finding where the first element of the right half belongs in the left half — merge the prefix sequentially, then recurse on the remainder

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `doesNotMutateInput` fails | Writing sorted results back to the input array |
| `sortDescending` fails | Ignoring the `comparator` parameter — using `Integer.compare` hardcoded |
| `binarySearchFindsFirstGe` wrong | `binarySearch` finds last less-than instead of first greater-or-equal — check boundary condition |
| Sort wrong for large arrays | `mid` computed as `(lo + hi) / 2` — for very large arrays this overflows; use `lo + (hi - lo) / 2` |
