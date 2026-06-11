# Problem 21 — BlockingQueue Deep Dive

## 🟡 Difficulty: Intermediate

## 📖 Background

`BlockingQueue<E>` is the workhorse of the `java.util.concurrent` package.
It is the standard building block for producer-consumer pipelines.

### The four operation families

|            | Throws exception | Returns value   | Blocks    | Times out                |
|------------|-----------------|-----------------|-----------|--------------------------|
| **Insert** | `add(e)`        | `offer(e)`      | `put(e)`  | `offer(e, time, unit)`   |
| **Remove** | `remove()`      | `poll()`        | `take()`  | `poll(time, unit)`       |
| **Examine**| `element()`     | `peek()`        | —         | —                        |

### Concrete implementations

| Class | Capacity | Notes |
|-------|---------|-------|
| `LinkedBlockingQueue` | Optionally bounded | High-throughput; separate head/tail locks |
| `ArrayBlockingQueue`  | Fixed bounded | Single lock; fair or non-fair |
| `PriorityBlockingQueue` | Unbounded | Elements ordered by natural order or `Comparator` |
| `SynchronousQueue`    | Zero capacity | Each `put` must be matched by a `take` (handoff) |
| `DelayQueue`          | Unbounded | Elements only become available after a delay |
| `LinkedTransferQueue` | Unbounded | Combines SynchronousQueue + LinkedBlockingQueue |

## 🎯 Task

### `WorkStealingPipeline`
A multi-stage processing pipeline where each stage is backed by its own
`LinkedBlockingQueue`. Data flows: source → stage1 → stage2 → sink.

- `WorkStealingPipeline(int stageWorkers)` — creates a 2-stage pipeline with
  `stageWorkers` threads per stage
- `start()` — launches all worker threads
- `submit(String item)` — enqueues item for stage-1 processing
- `shutdown()` — sends poison-pill signals to drain and stop all workers; blocks
  until all workers terminate
- `getProcessedCount()` — number of items that reached the sink
- Stage 1 transforms: `item.toUpperCase()`
- Stage 2 transforms: `item + "!"`  (appends exclamation mark)

### `DelayedTaskScheduler`
Schedules tasks to run after a specified delay using `DelayQueue<DelayedTask>`:

- `schedule(Runnable task, long delayMs)` — enqueues a task to run after `delayMs`
- `start()` — starts the background dispatcher thread (daemon)
- `stop()` — stops the dispatcher
- `getExecutedCount()` — number of tasks executed so far

`DelayedTask` must implement `Delayed` so that the `DelayQueue` releases it only
after the delay has elapsed.

## 💡 Hints
- Poison-pill: use a sentinel string (e.g., `null` or a special `POISON` constant)
  that each worker recognises as "stop"; re-enqueue the pill after consuming it so
  the next worker also sees it
- `DelayedTask.getDelay(TimeUnit)`: return `deadline - System.nanoTime()` converted
  to the requested unit
- `DelayedTask.compareTo`: tasks with shorter remaining delay come first

## 🧠 Interview Talking Points
- What is the difference between `LinkedBlockingQueue` and `ArrayBlockingQueue`?
- Why does `SynchronousQueue` have zero capacity?
- How do you implement a poison-pill shutdown for multiple consumers?
- What is `LinkedTransferQueue.transfer()` and how does it differ from `put()`?
- When would you use `DelayQueue` vs `ScheduledExecutorService`?
