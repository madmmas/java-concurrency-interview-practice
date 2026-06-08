# Problem 06 — The `volatile` Keyword

## 🟢 Difficulty: Beginner

## 📖 Background

Without synchronization, the Java Memory Model (JMM) allows each thread to cache
variables in CPU registers or thread-local caches. A write by Thread A may never
become visible to Thread B — leading to infinite loops or stale reads.

`volatile` solves the **visibility** problem:
- Every write to a `volatile` variable is flushed to main memory immediately.
- Every read of a `volatile` variable is fetched from main memory, never a cache.
- Writes to `volatile` happen-before subsequent reads of the same variable.

**What `volatile` does NOT do:**
- It does NOT make compound operations (like `count++`) atomic.
- `count++` is still read-modify-write — three non-atomic steps.

Good uses of `volatile`:
- Stop-flags / cancellation flags (`volatile boolean running`)
- Status fields written by one thread and read by many
- Safe publication of immutable objects

## 🎯 Task

Implement two classes:

### `StopFlag`
A cancellable background worker:
- `start()` — starts an internal thread that loops, incrementing a counter, until stopped
- `stop()` — signals the worker to stop (must be visible immediately to the worker thread)
- `getCount()` — returns how many loop iterations completed
- `isRunning()` — returns whether the worker thread is still alive

### `VolatileStatusBoard`
A notice board where one writer thread posts a status string and many reader threads
observe it:
- `postStatus(String status)` — stores the status (written by one thread at a time)
- `getStatus()` — returns the latest posted status (must always return the freshest write)
- `getReadCount()` — total number of `getStatus()` calls made across all threads

## 💡 Hints
- `running` flag and `status` field must be `volatile`
- `getCount()` does NOT need to be exact under concurrency — approximate is fine for a volatile counter
- `getReadCount()` must be thread-safe — use `AtomicLong` or `synchronized`

## 🧠 Interview Talking Points
- What is the Java Memory Model and why does it allow caching?
- What is a happens-before relationship?
- Why can't `volatile` replace `synchronized` for compound operations?
- When would you use `volatile` vs `AtomicInteger` vs `synchronized`?
