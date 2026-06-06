# Problem 05 — Thread Join

## 🟢 Difficulty: Beginner

## 📖 Background

`thread.join()` causes the **calling thread** to wait until the specified thread terminates. It's essential for:

- Collecting results from worker threads
- Ensuring tasks complete before moving on
- Establishing **happens-before** guarantees (anything written before a thread terminates is visible to any thread that joins it)

Variants:
- `join()` — waits indefinitely
- `join(long millis)` — waits at most `millis` milliseconds; returns even if thread is still running
- `join(long millis, int nanos)` — fine-grained timeout

**Key insight:** After `join()` returns, all data written by the joined thread is guaranteed to be visible to the joining thread — no `volatile` or `synchronized` needed for that data.

## 🎯 Task

Implement `ParallelMerge` which:

1. `sumArray(int[] array)` — splits the array into two halves, computes the sum of each half on a **separate thread**, joins both threads, and returns the total sum. Use `join()` to wait for results.

2. `findMax(int[] array)` — same idea: split in half, find the max of each half in parallel, join, return the overall max.

3. `runWithTimeout(Runnable task, long timeoutMillis)` — runs the task on a new thread, waits at most `timeoutMillis` for it to finish, and returns `true` if the task completed within the timeout or `false` if it timed out. Must use `join(long millis)`.

## 📋 Skeleton

See `src/main/java/com/concurrency/beginner/p05/ParallelMerge.java`

## 💡 Hints

- Use instance variables on inner `Thread` subclasses or `int[]` wrappers to pass results back
- For `sumArray`, you need two threads each accumulating a partial sum
- `join(millis)` returns after the timeout — check `thread.isAlive()` to determine if it timed out

## 🧠 Interview Talking Points

- What does `join()` guarantee in terms of memory visibility (happens-before)?
- What is the difference between `join()` and `join(long millis)`?
- Why is `join()` not suitable for large-scale parallel workloads? (hint: `ExecutorService`)
- What happens if you call `join()` on a thread that hasn't been started yet?
