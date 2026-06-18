# Hints — Problem 21: BlockingQueue Deep Dive

## Level 1 — Nudge

The poison-pill pattern: put a sentinel value into the queue that each worker recognises as "stop." When a worker consumes the poison pill, it re-enqueues it (so the next worker also sees it) and exits its loop. For `DelayQueue`, the item controls its own release time by implementing `Delayed`.

---

## Level 2 — Direction

**`WorkStealingPipeline`** structure:
- `queue1` between stage 1 workers and stage 2 workers
- `queue2` as the sink (or call a sink callback)
- Poison pill: `private static final String POISON = "__POISON__"`
- Shutdown: put N poison pills into queue1 (one per stage-1 worker); each stage-1 worker that sees it re-enqueues it into queue2 *and* stops — stage-2 workers see pills from queue2

**`DelayedTask` implementing `Delayed`**:
```java
public long getDelay(TimeUnit unit) {
    return unit.convert(executeAt - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
}
public int compareTo(Delayed other) {
    return Long.compare(this.executeAt, ((DelayedTask)other).executeAt);
}
```

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Pipeline never shuts down | Poison pill not re-enqueued; next worker never sees it |
| `DelayQueue` releases tasks immediately | `getDelay()` returns 0 or negative from the start — `executeAt` set incorrectly |
| Stage-2 workers miss items | Stage-1 puts poison into queue2 before all real items are processed |
