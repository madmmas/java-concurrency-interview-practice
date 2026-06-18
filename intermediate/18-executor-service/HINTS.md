# Hints — Problem 18: ExecutorService

## Level 1 — Nudge

`ThreadPoolExecutor` is the constructor you need — `Executors.newFixedThreadPool()` is a factory shortcut that hides configuration options. For a bounded pool with overflow handling, build the `ThreadPoolExecutor` directly with an explicit queue and rejection handler.

---

## Level 2 — Direction

**`TaskDispatcher` constructor**:
```java
new ThreadPoolExecutor(
    coreThreads, maxThreads,
    60L, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(queueCapacity),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```
`CallerRunsPolicy` means if the queue is full, the submitting thread runs the task itself — no exception thrown.

**`submitAll`**: wrap each task so it counts down a `CountDownLatch`:
```java
CountDownLatch latch = new CountDownLatch(tasks.size());
tasks.forEach(t -> submit(() -> { try { t.run(); } finally { latch.countDown(); } }));
latch.await();
```

**`PrioritizedExecutor`**: create a `PriorityBlockingQueue` holding `PriorityTask` records that implement `Comparable` — higher priority = lower compareTo value. Include a sequence number field for FIFO tie-breaking.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Tasks submitted beyond queue capacity are rejected with exception | Using `AbortPolicy` (default) instead of `CallerRunsPolicy` |
| `submitAll` returns before all tasks finish | Missing `latch.await()` or countdown not called in finally |
| Priority ordering wrong | `compareTo` returns `this.priority - other.priority` (higher value = runs first needs reversed sign) |
