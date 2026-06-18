# Hints — Problem 23: Phaser

## Level 1 — Nudge

`Phaser` is a generalization of `CyclicBarrier` that supports dynamic registration. Threads call `arriveAndAwaitAdvance()` to synchronise at a phase boundary. Threads can join mid-flight via `register()` and leave via `arriveAndDeregister()`. Override `onAdvance()` to control termination.

---

## Level 2 — Direction

**`DynamicWorkerPool`**: override `onAdvance`:
```java
Phaser phaser = new Phaser(initialWorkers) {
    @Override
    protected boolean onAdvance(int phase, int registeredParties) {
        return phase >= phases - 1;  // terminate after N phases
    }
};
```
Each worker loop: `while (!phaser.isTerminated()) { phaseWork.run(); phaser.arriveAndAwaitAdvance(); }`

**`PipelinedDataProcessor`**:
- Register the main thread as party: `phaser.register()`
- Each worker registers itself before starting
- After each stage: `phaser.arriveAndAwaitAdvance()`
- Main thread can use `phaser.awaitAdvance(phaser.arrive())` to participate in the gate
- After all workers finish all stages, collect results in order

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Workers never terminate | `onAdvance` always returns `false` |
| Workers skip stages | Only calling `arrive()` instead of `arriveAndAwaitAdvance()` — worker doesn'\''t wait |
| Late-registered worker causes `IllegalStateException` | Calling `register()` after the phaser has terminated |
