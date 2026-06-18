# Hints — Problem 38: Priority Task Executor

## Level 1 — Nudge

A `PriorityBlockingQueue` orders tasks by their `compareTo`. Higher priority must mean *smaller* compareTo result (queue is a min-heap by default). For FIFO tie-breaking among equal priorities, add a monotonically increasing sequence number to each task and compare it when priorities are equal.

---

## Level 2 — Direction

**`PriorityTask` comparable**:
```java
record PriorityTask(Runnable task, int priority, long seq) implements Comparable<PriorityTask> {
    public int compareTo(PriorityTask other) {
        int cmp = Integer.compare(other.priority, this.priority); // higher priority first
        if (cmp != 0) return cmp;
        return Long.compare(this.seq, other.seq);  // earlier submission first
    }
}
```

**`PriorityTaskExecutor`** backed by `ThreadPoolExecutor`:
- Use `new PriorityBlockingQueue<Runnable>()` as the work queue
- Wrap the submitted `Runnable` in a `PriorityTask` before calling `executor.execute()`
- For `submit(Callable, priority)`: wrap in a `FutureTask`, then wrap that in a `PriorityTask`
- `getCompletedCount()`: `AtomicLong` incremented in a wrapper around each task

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `highPriorityRunsFirst` test fails | `compareTo` returns positive when higher priority — reverse the comparison |
| `equalPriorityFIFO` fails | Missing sequence number — tasks with equal priority ordered randomly |
| `submit` returns wrong result | Wrapping `Callable` in `PriorityTask` but not in a `FutureTask` — priority queue can'\''t hold `Callable` directly |
