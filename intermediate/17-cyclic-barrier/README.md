# Problem 17 — CyclicBarrier

## 🟡 Difficulty: Intermediate

## 📖 Background

A `CyclicBarrier` makes a fixed set of threads wait for each other to reach a
common **barrier point** before any of them proceed. Unlike `CountDownLatch`
(which is one-shot), a `CyclicBarrier` **resets automatically** and can be
reused for multiple rounds/phases.

```java
CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    System.out.println("All 3 threads reached barrier — starting next phase");
});

// In each of 3 threads:
doPhaseWork();
barrier.await();  // waits until all 3 threads call await()
doNextPhase();
```

Key API:
| Method | Description |
|--------|-------------|
| `await()` | Wait for all parties to arrive; throws `BrokenBarrierException` if barrier is broken |
| `await(long, TimeUnit)` | Timed wait |
| `getParties()` | Number of threads required to trip the barrier |
| `getNumberWaiting()` | Threads currently waiting |
| `reset()` | Reset barrier (breaks waiting threads with `BrokenBarrierException`) |
| `isBroken()` | True if the barrier was broken |

**vs CountDownLatch:**
| | CountDownLatch | CyclicBarrier |
|---|---|---|
| Reusable | ❌ One-shot | ✅ Resets automatically |
| Who counts down | Any thread (not necessarily the waiting ones) | The waiting threads themselves |
| Barrier action | ❌ | ✅ Optional Runnable runs before release |

## 🎯 Task

### `ParallelMatrixMultiplier`
Multiplies two N×N matrices using N² worker threads — each thread computes
one cell of the result. A `CyclicBarrier` synchronises all workers so that:
- **Phase 1:** All workers compute their cell value
- **Barrier trips:** All cells are done, result is ready to read
- `multiply(int[][] a, int[][] b)` — returns the result matrix

### `PhaseSimulator`
Simulates a multi-phase scientific simulation where all worker threads must
complete each phase before any proceeds to the next:
- `PhaseSimulator(int workers, int phases)` — creates the simulator
- `run(Runnable phaseWork)` — starts all worker threads; each runs `phaseWork`
  then hits the barrier, repeating for `phases` total phases
- `getCompletedPhases()` — the number of barrier trips so far (the barrier
  action increments this)
- `awaitCompletion()` — blocks caller until all phases are done

## 💡 Hints
- `ParallelMatrixMultiplier`: create `n*n` threads each computing `result[i][j] = sum(a[i][k] * b[k][j])`; use a single `CyclicBarrier(n*n)` — the barrier action can be a no-op since you just wait for all cells to finish
- `PhaseSimulator`: use `CyclicBarrier(workers, barrierAction)` where the action increments a phase counter; after `phases` trips all threads should exit their loops

## 🧠 Interview Talking Points
- What is `BrokenBarrierException` and when is it thrown?
- When would you choose `CyclicBarrier` over `CountDownLatch`?
- What happens if one thread times out or is interrupted at the barrier?
- How does the optional barrier action run (on which thread)?
