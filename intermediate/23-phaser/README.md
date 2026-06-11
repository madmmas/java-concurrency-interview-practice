# Problem 23 — Phaser

## 🟡 Difficulty: Intermediate

## 📖 Background

`Phaser` is the most flexible Java synchronisation barrier. It supersedes both
`CountDownLatch` (one-shot) and `CyclicBarrier` (fixed parties, reusable) by
supporting **dynamic party registration** and **tiered (tree) phasers**.

### Key API

```java
Phaser phaser = new Phaser(parties);   // register `parties` upfront

// Inside a thread:
phaser.arriveAndAwaitAdvance();   // arrive + wait for all others → blocks until phase advances
phaser.arrive();                  // arrive but don't wait (async)
phaser.arriveAndDeregister();     // arrive and permanently leave (reduces party count)
phaser.register();                // add one more party dynamically
phaser.awaitAdvance(phase);       // wait for a specific phase number to complete
phaser.getPhase();                // current phase number
phaser.getRegisteredParties();    // current registered party count
```

### Termination
Override `onAdvance(phase, registeredParties)` to control when the `Phaser`
terminates. Return `true` to terminate (which causes all waiting threads to
be released and `isTerminated()` to return `true`).

```java
Phaser phaser = new Phaser(workers) {
    @Override
    protected boolean onAdvance(int phase, int registeredParties) {
        return phase >= (totalPhases - 1) || registeredParties == 0;
    }
};
```

### Tiered Phasers
For very large numbers of threads, chain child phasers to a parent to reduce
contention — each child handles a subset of threads; the parent sees only
its children as parties.

## 🎯 Task

### `DynamicWorkerPool`
A pool where workers can join and leave between phases, coordinated by a `Phaser`:

- `DynamicWorkerPool(int initialWorkers, int phases)` — pre-registers `initialWorkers`
- `start(Runnable phaseWork)` — each initial worker runs `phaseWork` then calls
  `arriveAndAwaitAdvance()` for `phases` rounds
- `addWorker(Runnable phaseWork, int startPhase)` — a new thread registers itself
  with the phaser (`phaser.register()`) and starts participating from `startPhase`
- `removeWorkerAfterPhase()` — a worker calls `arriveAndDeregister()` to leave
  after completing the current phase
- `awaitCompletion()` — blocks until the phaser terminates
- `getCompletedPhases()` — returns `phaser.getPhase()` (or total completed phases)

### `PipelinedDataProcessor`
Uses a `Phaser` to implement a 3-stage data pipeline where all workers must
complete stage N before any worker starts stage N+1:

- `PipelinedDataProcessor(int workers)` — `workers` threads each process all 3 stages
- `process(List<String> data)` — assigns chunks of `data` to each worker; each
  worker runs 3 stages (trim → toUpperCase → append "✓") using `arriveAndAwaitAdvance()`
  between stages; returns the merged, processed list
- Results from all workers are merged in order after completion

## 💡 Hints
- `DynamicWorkerPool`: use the `onAdvance` override to terminate after `phases` rounds
  — return `true` when `phase >= phases - 1`
- `PipelinedDataProcessor`: create a `Phaser(1)` (the "main" thread is a party);
  each worker calls `phaser.register()` before starting; use `awaitAdvance(0)`,
  `awaitAdvance(1)`, `awaitAdvance(2)` from the main thread to gate stages
- Alternatively use `arriveAndAwaitAdvance()` inside workers for each of the 3 stages

## 🧠 Interview Talking Points
- How does `Phaser` differ from `CyclicBarrier`? Give a use case for each.
- What does `arriveAndDeregister()` do? When is it useful?
- How does `onAdvance()` control phaser termination?
- What is a tiered (tree) Phaser and when would you need one?
- What does a negative return from `getPhase()` mean?
