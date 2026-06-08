# Problem 13 — CountDownLatch

## 🟢 Difficulty: Beginner

## 📖 Background

`CountDownLatch` is a one-shot synchronisation barrier. You initialise it with a
count; threads call `countDown()` to decrement it; threads call `await()` to
block until the count reaches zero.

```java
CountDownLatch latch = new CountDownLatch(3);

// Worker threads each call:
latch.countDown();   // count: 3 → 2 → 1 → 0

// Coordinator thread calls:
latch.await();       // blocks until count == 0
```

**Key characteristics:**
- One-shot: once it reaches zero it **cannot be reset** (use `CyclicBarrier` if you
  need repeated barriers)
- `await(long timeout, TimeUnit unit)` returns `false` if it times out before
  reaching zero
- Any number of threads can `await()` simultaneously — they all unblock when
  count hits zero

**Classic patterns:**
1. **Starting gun** — initialise with count=1; workers all `await()`; coordinator
   calls `countDown()` once to release all workers simultaneously
2. **Completion gate** — initialise with N; N workers each call `countDown()` when
   done; coordinator `await()`s for all to finish

## 🎯 Task

Implement `ServiceStartupCoordinator`:
- `registerService(String name)` — registers a service that must start before the
  system is ready (call before `waitForAll()`)
- `serviceReady(String name)` — signals that one service has finished starting
  (calls `countDown()` internally)
- `waitForAll()` — blocks until all registered services have called `serviceReady()`
- `waitForAll(long timeoutMs)` — same but with a timeout; returns `true` if all
  services became ready within the timeout
- `getReadyCount()` — number of services that have called `serviceReady()` so far

Implement `RaceStartGun`:
- `register()` — a racer thread registers and blocks, waiting for the gun
- `fire()` — releases all waiting racers simultaneously (one-shot)
- `getWaitingCount()` — number of threads currently waiting for the gun

## 💡 Hints
- For `ServiceStartupCoordinator`, create the `CountDownLatch` lazily when
  `waitForAll()` is called (or eagerly in a `start()` method), sized to the number
  of registered services
- For `RaceStartGun`, use a latch initialised to 1; `fire()` calls `countDown()`
- Track `readyCount` with `AtomicInteger`; increment it inside `serviceReady()`

## 🧠 Interview Talking Points
- What is the difference between `CountDownLatch` and `CyclicBarrier`?
- Can `CountDownLatch` be reused after it reaches zero?
- What happens if `countDown()` is called more times than the initial count?
- How would you implement a `CountDownLatch` from scratch using `wait()`/`notify()`?
