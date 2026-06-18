# Hints — Problem 02: Runnable vs Thread

## Level 1 — Nudge

You need to run tasks on threads *without* extending `Thread`. The `Runnable` interface has one method: `run()`. A lambda works perfectly. The `Thread` constructor accepts a `Runnable` and an optional name string.

---

## Level 2 — Direction

**`runTask`**: create a `Thread` with the given runnable and name, start it, then call `join()` on it before returning. Without the join, the method returns before the task finishes.

**`runTasksParallel`**: the pattern is: *start all threads first, then join all threads*. Do not start-then-immediately-join in the same loop — that defeats parallelism.

**`buildCountingRunnable`**: return a lambda that loops `times` times, calling `collector.add(value)` each iteration. The collector is described as thread-safe, so no additional locking is needed inside the lambda.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Tasks run sequentially instead of in parallel | You joined each thread immediately after starting it |
| `buildCountingRunnable` adds wrong number of items | Off-by-one in loop; loop should run exactly `times` iterations |
| Thread name test fails | Missing the name argument in `new Thread(runnable, name)` |

---

## Key insight

`join()` establishes a happens-before guarantee: anything the joined thread wrote is visible to the joining thread after `join()` returns. This is why results are safe to read after joining — no extra synchronization needed.

