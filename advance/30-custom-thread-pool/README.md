# Problem 30 — Custom ThreadPoolExecutor

## 🔴 Difficulty: Advanced

## 📖 Background

`ThreadPoolExecutor` is the engine behind every `Executors.*` factory method.
Understanding how to extend it — and how to build a custom `ThreadFactory` and
`RejectedExecutionHandler` — is essential for production-grade work.

### Thread lifecycle in ThreadPoolExecutor

```
RUNNING → SHUTDOWN (shutdown()) → STOP (shutdownNow()) → TIDYING → TERMINATED
```

When a task is submitted:
1. If threads < corePoolSize → create a new thread (even if idle threads exist)
2. If threads >= corePoolSize → enqueue in work queue
3. If queue is full and threads < maximumPoolSize → create a new thread
4. If queue is full and threads == maximumPoolSize → invoke `RejectedExecutionHandler`

### Hook methods (override these to instrument the pool)

```java
protected void beforeExecute(Thread t, Runnable r)   // called before each task
protected void afterExecute(Runnable r, Throwable t) // called after each task
protected void terminated()                          // called when pool fully terminates
```

### Idiomatic graceful shutdown pattern

```java
executor.shutdown();                          // stop accepting new tasks
if (!executor.awaitTermination(60, SECONDS)) {
    executor.shutdownNow();                   // cancel running tasks
    executor.awaitTermination(60, SECONDS);   // wait for cancellation
}
```

## 🎯 Task

### `InstrumentedThreadPool` (extends `ThreadPoolExecutor`)
Adds latency tracking and lifecycle hooks:
- Override `beforeExecute` → store task start time in a `ThreadLocal<Long>`
- Override `afterExecute` → compute elapsed nanos, update min/max/total latency atomics, increment `totalTasksRun`; always call `taskStartTime.remove()`
- Override `terminated` → record `terminatedAt = System.currentTimeMillis()`
- `getAverageLatencyMs()`, `getMinLatencyMs()`, `getMaxLatencyMs()` — latency stats in ms
- `getTotalTasksRun()` — includes tasks that threw exceptions
- `getTerminatedAt()` — wall-clock ms of termination, or -1 if not yet terminated

### `WorkerThreadFactory` (implements `ThreadFactory`)
- Names threads `"pool-{poolName}-worker-{N}"` (N monotonically increasing from 1)
- Configurable daemon flag
- `getCreatedCount()` — total threads created
- Installs an `UncaughtExceptionHandler` that appends the `Throwable` to an internal list
- `getUncaughtExceptions()` — unmodifiable view of that list

### `BoundedCallerRunsPool`
A `ThreadPoolExecutor` with bounded queue + custom `CallerRunsPolicy` that also tracks how many tasks ran on the caller thread:
- `BoundedCallerRunsPool(int coreThreads, int maxThreads, int queueCapacity)`
- `submit(Runnable task)` — delegates to `executor.execute(task)`; if rejected, runs on caller thread and increments `callerRunCount`
- `getCallerRunCount()` — tasks that ran on the calling thread
- `getCompletedTaskCount()` — delegates to `ThreadPoolExecutor`
- `shutdown()`

## 💡 Hints
- `beforeExecute`: `taskStartTime.set(System.nanoTime())`
- `afterExecute`: `long elapsed = System.nanoTime() - taskStartTime.get(); taskStartTime.remove();` then update atomics
- `minLatencyNanos` starts at `Long.MAX_VALUE`; use `updateAndGet(cur -> Math.min(cur, elapsed))`
- `WorkerThreadFactory`: thread number is `AtomicInteger`, increment in `newThread`
- `BoundedCallerRunsPool`: pass `(r, executor) -> { callerRunCount.incrementAndGet(); r.run(); }` as the rejection handler

## 🧠 Interview Talking Points
- Walk through `ThreadPoolExecutor`'s thread-creation decision tree.
- What are the three hook methods? What are common uses for each?
- What is the idiomatic two-phase shutdown pattern?
- How does `CallerRunsPolicy` provide back-pressure? What are its trade-offs?
- Why must `afterExecute` always call `taskStartTime.remove()`?
- What is the difference between `getTaskCount()` and `getCompletedTaskCount()`?
