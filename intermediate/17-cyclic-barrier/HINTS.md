# Hints — Problem 17: CyclicBarrier

## Level 1 — Nudge

`CyclicBarrier(n)` waits until `n` threads have all called `await()`. Once they all arrive, the barrier trips and all threads are released simultaneously. It is *cyclic* — it resets automatically after tripping, so the same barrier can be used for multiple phases.

---

## Level 2 — Direction

**`ParallelMatrixMultiplier`**:
- Create `n*n` threads, each computing one cell `result[i][j]`
- A single `CyclicBarrier(n*n)` — when all cells are computed, the barrier trips
- After `barrier.await()` in each thread, the result matrix is fully populated
- The calling thread (in `multiply()`) doesn'\''t need to wait — just `join()` all worker threads after starting them

**`PhaseSimulator`**:
- `CyclicBarrier(workers, barrierAction)` where the barrier action increments `completedPhases`
- Each worker loops for `phases` iterations: `phaseWork.run(); barrier.await();`
- Override `onAdvance` to return `true` when `phase >= phases - 1` — this terminates the phaser after the last phase
- Use a `CountDownLatch` for `awaitCompletion()`

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `BrokenBarrierException` thrown | One thread threw an exception or timed out — the barrier is poisoned for all waiters |
| Barrier action runs more or fewer times than expected | Incorrect `phases` count, or barrier action increments wrong counter |
| Workers run phases out of order | Each thread must call `await()` after *every* phase, not just the last one |
