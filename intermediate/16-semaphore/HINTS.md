# Hints — Problem 16: Semaphore

## Level 1 — Nudge

A `Semaphore` is a counter of available permits. `acquire()` decrements it (blocking if at zero); `release()` increments it. For a connection pool, the semaphore size is the pool capacity. For a rate limiter, the semaphore holds the current token count.

---

## Level 2 — Direction

**`ConnectionPool`**:
- Create `new Semaphore(maxConnections)` and an `ArrayBlockingQueue<Integer>` pre-filled with IDs 1..maxConnections
- `acquire()`: call `semaphore.acquire()`, then `queue.poll()` (the queue now has a connection guaranteed)
- `release(id)`: call `queue.offer(id)`, then `semaphore.release()`
- `availableConnections()`: `semaphore.availablePermits()`

**`RateLimiter`**:
- `new Semaphore(maxRequestsPerWindow)` — starts full
- Refill daemon thread: every `windowMs`, compute `toRefill = maxRequestsPerWindow - sem.availablePermits()`, then `sem.release(toRefill)`
- `tryAcquire()` → `sem.tryAcquire()`
- `acquire()` → `sem.acquire()`

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Pool allows more connections than capacity | Not calling `semaphore.acquire()` before `queue.poll()` |
| Rate limiter allows unlimited requests | Refill logic uses `sem.release(maxRequestsPerWindow)` instead of releasing only the deficit |
| Test hangs on `acquire()` | Forgot to call `release()` in a test teardown, or pool is exhausted and no one is returning connections |
