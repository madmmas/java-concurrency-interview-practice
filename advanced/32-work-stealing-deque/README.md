# Problem 32 — Work-Stealing Deque (ArrayDeque with Concurrent Access)

## 🔴 Difficulty: Advanced

## 📖 Background

A **work-stealing deque** (double-ended queue / deque) is the core data structure
behind Java's `ForkJoinPool`. Each worker thread has its own deque:

- The **owner** thread pushes/pops from the **bottom** (LIFO for locality)
- **Thief** threads steal from the **top** (FIFO to grab older, larger tasks)

This design gives excellent cache locality for the owner while allowing idle
threads to steal work without contending with the owner most of the time.

```
         top (thieves steal from here)
         ┌──────────────────────────┐
         │  task0  task1  task2     │  task3  ← owner pushes/pops here
         └──────────────────────────┘
         bottom (owner works here)
```

### Chase-Lev algorithm (simplified)
The classic lock-free work-stealing deque by Chase & Lev (2005):
- `push(task)` — owner: write to `bottom`, then increment `bottom` (single writer)
- `pop()` — owner: decrement `bottom`, then CAS on `top` (contends only when nearly empty)
- `steal()` — thief: CAS `top` forward (multiple thieves contend here)

### Why it matters
Understanding this deque explains:
- Why `ForkJoinPool` achieves near-linear speedup on parallel workloads
- Why work-stealing has lower contention than a shared queue
- How `RecursiveTask.fork()` / `join()` map to push/pop internally

## 🎯 Task

### `WorkStealingDeque<T>`

A simplified (but correct) work-stealing deque using `AtomicInteger` for the
`top` pointer and a plain `int` for the `bottom` pointer (owner access only):

- `push(T task)` — owner pushes onto the bottom
- `pop()` — owner pops from the bottom (returns `null` if empty)
- `steal()` — any thread steals from the top (returns `null` if empty or contested)
- `size()` — approximate number of elements
- `isEmpty()` — true if deque appears empty

**Simplified design** (not lock-free but correct):
- Use a `ReentrantLock` for `steal()` only (thieves contend with each other)
- The owner uses `push()` and `pop()` without the lock (single-thread owner model)
- Internal storage is a circular array that doubles when full

### `WorkStealingScheduler`

A scheduler that uses one `WorkStealingDeque` per worker thread:

- `WorkStealingScheduler(int workers)` — creates `workers` threads each with its own deque
- `submit(Runnable task)` — adds a task to the least-loaded worker's deque
- `start()` — starts all worker threads; each loops: `pop()` own deque,
  else `steal()` from a random other deque, else `Thread.yield()`
- `shutdown()` — signals all workers to drain and stop; blocks until done
- `getCompletedCount()` — total tasks completed across all workers
- `getStolenCount()` — total tasks completed via stealing (not own deque)

## 💡 Hints

- `WorkStealingDeque`: use a `volatile T[]` array; `bottom` is only written by
  the owner so it can be a plain `int` field on the deque; `top` needs to be
  `AtomicInteger` because thieves CAS it
- `steal()`: read `top`, read `bottom`, if `top >= bottom` return null (empty);
  CAS `top` from old value to `old+1`; if CAS fails, return null (another thief won)
- `pop()`: if `bottom - top.get() <= 0` return null; otherwise read element at
  `--bottom` and check if a thief also got it (if `bottom < top.get()` restore)
- For `WorkStealingScheduler`, use a `volatile boolean running` flag

## 🧠 Interview Talking Points

- How does the owner's LIFO order differ from thieves' FIFO order, and why is
  this beneficial for cache locality?
- Why does the owner need to handle the "nearly empty" race in `pop()`?
- How does `ForkJoinPool`'s work-stealing compare to a shared `BlockingQueue`
  in terms of contention under high parallelism?
- What is the difference between a work-sharing and a work-stealing scheduler?
- Why does `ForkJoinPool` use `computeIfAbsent` rather than `fork()` for small
  tasks (the threshold concept)?
