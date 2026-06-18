# Hints — Problem 22: Exchanger

## Level 1 — Nudge

`Exchanger<T>` is a two-party rendezvous. Thread A calls `exchange(myValue)` and blocks; Thread B calls `exchange(itsValue)` and both unblock — A gets B'\''s value, B gets A'\''s value. It always requires exactly two threads. The producer fills a buffer, the consumer drains it — they swap at the handoff point.

---

## Level 2 — Direction

**`DoubleBufferedLogger`**:
- Two lists: `fillBuffer` (callers write here) and `flushBuffer` (background thread drains this)
- When `fillBuffer` reaches capacity: `flushBuffer = exchanger.exchange(fillBuffer)` — gives the full buffer to the flusher, gets back an empty one
- Background flush thread loop: `List<String> full = exchanger.exchange(new ArrayList<>())` → copy `full` to `flushedMessages` → repeat
- `stop()`: signal stop, then do a final exchange so the flush thread unblocks

**`GeneticCrossover`**:
- Each thread extracts its second half into an array
- Both call `exchanger.exchange(myHalf)` and get the other'\''s half back
- Each thread copies the received half into its chromosome using `System.arraycopy`

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Flush thread blocks forever on `stop()` | No final exchange call — flush thread is still waiting at `exchanger.exchange()` |
| `getFlushedMessages()` is always empty | Flush thread never runs, or exchanged buffer is discarded instead of being added to results |
| Crossover result wrong | Copying the received array back into the wrong half of the chromosome (check your `arraycopy` indices) |
