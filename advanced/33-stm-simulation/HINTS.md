# Hints — Problem 33: STM Simulation

## Level 1 — Nudge

Software Transactional Memory works like optimistic concurrency control: read freely, buffer writes locally, then at commit time validate that nothing you read has changed. If it has, abort and retry. The version number on each `TVar` is the staleness detector.

---

## Level 2 — Direction

**`TVar<T>`** fields: `volatile T value`, `volatile long version`, `ReentrantLock commitLock`.

**`Transaction`** internal state:
- `Map<TVar<?>, Object> writeSet` — buffered writes
- `Map<TVar<?>, Long> readSet` — TVar → version at read time

**`Transaction.commit()`**:
1. Lock all TVars in write set (in consistent order to avoid deadlock — sort by identity hash code)
2. Validate read set: for each `(tvar, version)` in readSet, check `tvar.getVersion() == version`; if any mismatch → unlock all → throw `RetryException`
3. Apply write set: set each TVar'\''s value and increment its version
4. Unlock all

**`STM.atomically()`**: catch `RetryException`, create a new `Transaction`, retry up to 100 times.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `commit` always succeeds even with conflicts | Not validating the read set — only checking write set |
| Deadlock on commit | Locking TVars in arbitrary order — must use consistent ordering (e.g., sort by `System.identityHashCode`) |
| Infinite retry loop | `RetryException` not thrown on conflict — returning normally causes STM.atomically to commit wrong values |
