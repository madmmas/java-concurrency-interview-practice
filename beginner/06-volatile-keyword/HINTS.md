# Hints — Problem 06: Volatile Keyword

## Level 1 — Nudge

A `boolean` field that controls a running loop must be declared `volatile`. Without it, the worker thread may cache the value in a register and never see the update written by another thread. `volatile` forces every read to come from main memory.

---

## Level 2 — Direction

**`StopFlag`**:
- Declare `private volatile boolean running`
- The worker thread loops `while (running) { counter++; }`
- `stop()` sets `running = false`
- `isRunning()` should check whether the internal thread is still alive: `thread.isAlive()`

**`VolatileStatusBoard`**:
- `private volatile String status` — one writer, many readers, volatile is sufficient
- `getReadCount()` requires a separate counter that multiple threads update — use `AtomicLong` here, since volatile alone does not make `++` atomic

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Worker loop never stops | `running` field is not `volatile` — worker reads cached value |
| `getCount()` is inaccurate | Expected — volatile counter with `++` is not atomic; tests for this field use approximate assertions |
| `getReadCount()` is inaccurate | Used `volatile int` with `++` instead of `AtomicLong.incrementAndGet()` |

---

## What volatile does NOT do

`volatile` guarantees visibility (every read sees the latest write) but not atomicity. `count++` on a volatile field is still three operations — read, increment, write — and two threads can interleave them. For atomic increment, use `AtomicLong`.

