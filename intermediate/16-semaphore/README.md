# Problem 16 — Semaphore

## 🟡 Difficulty: Intermediate

## 📖 Background

A `Semaphore` maintains a set of **permits**. Threads acquire permits before
accessing a resource and release them when done. Unlike a mutex (which is binary),
a semaphore can allow **N threads concurrently**.

```java
Semaphore sem = new Semaphore(3); // allows 3 concurrent threads

sem.acquire();          // blocks if no permits available
try {
    // use shared resource
} finally {
    sem.release();      // always release — even on exception
}
```

Key API:
| Method | Description |
|--------|-------------|
| `acquire()` | Block until a permit is available, then take it |
| `acquire(int n)` | Acquire n permits atomically |
| `release()` | Return one permit |
| `release(int n)` | Return n permits |
| `tryAcquire()` | Non-blocking attempt; returns false if none available |
| `tryAcquire(long, TimeUnit)` | Timed attempt |
| `availablePermits()` | Current permit count |

**Fairness:** `new Semaphore(n, true)` grants permits in FIFO order (prevents
starvation). `new Semaphore(n, false)` (default) may be faster but can starve
threads under high contention.

**Binary semaphore vs mutex:** A semaphore with 1 permit behaves like a mutex,
but with a critical difference — it can be released by a **different thread**
than the one that acquired it (useful for signalling between threads).

## 🎯 Task

### `ConnectionPool`
A thread-safe pool of database connections with a fixed maximum size:
- `ConnectionPool(int maxConnections)` — creates the pool with the given capacity
- `acquire()` — blocks until a connection is available, returns a connection ID (int)
- `release(int connectionId)` — returns the connection back to the pool
- `availableConnections()` — number of currently available connections
- `getMaxConnections()` — the pool capacity

Use a `Semaphore` to limit concurrency and a `BlockingQueue` (or `ConcurrentLinkedQueue`
+ semaphore) to manage the pool of connection IDs.

### `RateLimiter`
A token-bucket style rate limiter using a semaphore + a refill thread:
- `RateLimiter(int maxRequestsPerWindow, long windowMs)` — creates the limiter
- `tryAcquire()` — returns `true` if a request is permitted right now (non-blocking)
- `acquire()` — blocks until a permit is available
- `start()` — starts the background refill thread (daemon)
- `stop()` — stops the background refill thread

The refill thread wakes every `windowMs` milliseconds and releases all permits
back up to `maxRequestsPerWindow` (i.e., resets the window).

## 💡 Hints
- `ConnectionPool`: use `new Semaphore(maxConnections)` and an `ArrayBlockingQueue`
  pre-populated with IDs 1..maxConnections
- `RateLimiter`: the semaphore starts at `maxRequestsPerWindow`; the refill thread
  calls `sem.release(toRefill)` where `toRefill = maxRequestsPerWindow - availablePermits()`
- Mark the refill thread as a daemon so it doesn't prevent JVM shutdown

## 🧠 Interview Talking Points
- How does a `Semaphore` differ from a `ReentrantLock`?
- What is the "binary semaphore as mutex" pattern, and what can it do that `synchronized` cannot?
- How does a token-bucket rate limiter work?
- What is the difference between fair and non-fair semaphores?
- Can `release()` be called without a matching `acquire()`? What happens?
