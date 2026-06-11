# Problem 18 — ExecutorService & ThreadPoolExecutor

## 🟡 Difficulty: Intermediate

## 📖 Background

Managing raw threads is error-prone and wasteful. `ExecutorService` decouples
**task submission** from **thread management**, reusing a pool of threads across
many tasks.

### Factory methods (`Executors` class)
```java
Executors.newFixedThreadPool(n)       // n threads, unbounded queue
Executors.newCachedThreadPool()       // grows/shrinks on demand
Executors.newSingleThreadExecutor()   // 1 thread, ordered execution
Executors.newScheduledThreadPool(n)   // for periodic/delayed tasks
```

### `ThreadPoolExecutor` — full control
```java
new ThreadPoolExecutor(
    corePoolSize,    // min threads always kept alive
    maximumPoolSize, // max threads under load
    keepAliveTime,   // how long idle threads above core survive
    timeUnit,
    workQueue,       // LinkedBlockingQueue, SynchronousQueue, ArrayBlockingQueue
    threadFactory,   // optional: name threads, set daemon flag
    rejectedHandler  // AbortPolicy, CallerRunsPolicy, DiscardPolicy, etc.
)
```

### Graceful shutdown
```java
executor.shutdown();              // stop accepting new tasks
executor.awaitTermination(60, SECONDS); // wait for running tasks
executor.shutdownNow();           // interrupt running tasks (last resort)
```

### Rejection policies
| Policy | Behaviour |
|--------|-----------|
| `AbortPolicy` (default) | Throw `RejectedExecutionException` |
| `CallerRunsPolicy` | Caller thread runs the task (back-pressure) |
| `DiscardPolicy` | Silently discard the task |
| `DiscardOldestPolicy` | Discard oldest queued task, retry submit |

## 🎯 Task

### `TaskDispatcher`
A configurable dispatcher backed by `ThreadPoolExecutor`:
- `TaskDispatcher(int coreThreads, int maxThreads, int queueCapacity)` — creates
  a bounded pool; excess tasks use `CallerRunsPolicy`
- `submit(Runnable task)` — submits a task; never throws (uses CallerRunsPolicy)
- `submitAll(List<Runnable> tasks)` — submits all tasks and returns only after
  all have completed
- `shutdown()` — graceful shutdown with a 5-second wait
- `getCompletedTaskCount()` — delegates to `ThreadPoolExecutor.getCompletedTaskCount()`
- `getActiveThreadCount()` — delegates to `ThreadPoolExecutor.getActiveCount()`

### `PrioritizedExecutor`
An executor that runs higher-priority tasks first:
- `PrioritizedExecutor(int threads)` — uses a `PriorityBlockingQueue`
- `submit(Runnable task, int priority)` — higher priority = runs first
- `shutdown()` / `awaitTermination(long, TimeUnit)`
- Tasks with equal priority run in submission order (use a sequence number to break ties)

## 💡 Hints
- For `TaskDispatcher`, use `new ArrayBlockingQueue<>(queueCapacity)` as the work
  queue and `new ThreadPoolExecutor.CallerRunsPolicy()` as the handler
- `submitAll`: use a `CountDownLatch(tasks.size())` — wrap each task to call `latch.countDown()` after completion, then `latch.await()`
- For `PrioritizedExecutor`, wrap each `Runnable` in a `PriorityTask` record
  implementing `Comparable<PriorityTask>` (higher `priority` value = lower `compareTo`)

## 🧠 Interview Talking Points
- What is the difference between `corePoolSize` and `maximumPoolSize`?
- When does `ThreadPoolExecutor` create threads beyond `corePoolSize`?
- Why is `Executors.newFixedThreadPool()` potentially dangerous in production?
- What is `CallerRunsPolicy` and how does it provide back-pressure?
- What is the difference between `shutdown()` and `shutdownNow()`?
