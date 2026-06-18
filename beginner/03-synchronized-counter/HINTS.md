# Hints — Problem 03: Synchronized Counter

## Level 1 — Nudge

You need two classes with the same interface: one where every method is thread-safe, one where nothing is. The thread-safe version needs to prevent two threads from executing the same operation at the same time. Java has a built-in keyword for that.

---

## Level 2 — Direction

- Add the `synchronized` keyword to each method of `ThreadSafeCounter`. This acquires the object's intrinsic lock before the method body runs.
- `UnsafeCounter` is the same code *without* `synchronized` — its job is to demonstrate the race condition.
- `getCount()` also needs `synchronized`. Without it, a reader thread may see a stale cached value even though the field has been updated. Synchronization provides memory visibility, not just mutual exclusion.
- `incrementBy(int delta)` should add the full delta in one synchronized operation, not call `increment()` delta times (that would release and re-acquire the lock between each step).

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `ThreadSafeCounter` test fails intermittently | `getCount()` is not synchronized — stale read |
| `UnsafeCounter` test always passes (wrong) | You accidentally synchronized `UnsafeCounter` too |
| `incrementBy` test fails | Implemented as a loop of `increment()` calls rather than a single atomic add |

---

## Why `count++` is not atomic

`count++` compiles to three bytecode instructions: `getfield`, `iadd`, `putfield`. Two threads can interleave between any of them, both reading the same value, both adding 1, and both writing back the same result — effectively losing one increment.

