# Hints — Problem 47: Transaction Manager (2PC)

## Level 1 — Nudge

Two-Phase Commit: Phase 1 (prepare) asks all participants if they can commit. If *any* participant says no, all must roll back. Phase 2 (commit or rollback) executes the decision. Running phases in parallel across participants requires collecting all results before making the global decision.

---

## Level 2 — Direction

**`TransactionCoordinator.execute(txId)`**:
```java
// Phase 1 — parallel prepare
List<Future<Boolean>> prepFutures = participants.stream()
    .map(p -> executor.submit(() -> p.prepare(txId)))
    .collect(toList());
boolean allPrepared = prepFutures.stream().allMatch(f -> f.get());

// Phase 2 — parallel commit or rollback
if (allPrepared) {
    participants.forEach(p -> executor.submit(() -> p.commit(txId)));
    committedCount.incrementAndGet();
    return COMMITTED;
} else {
    participants.forEach(p -> executor.submit(() -> p.rollback(txId)));
    abortedCount.incrementAndGet();
    return ABORTED;
}
```

Wait for all Phase 2 operations to complete before returning — use `invokeAll` or collect and join futures.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `abortCallsRollbackOnAll` fails — reliable participant not rolled back | Phase 2 rollback not sent to *all* participants, only to the one that failed |
| Count wrong after multiple transactions | Not resetting participant state between transactions in the test — but check that you'\''re using atomic increments for counts |
| `noParticipantsCommitsVacuously` throws | `allMatch` on an empty stream returns `true` — correct, should commit; check you'\''re not throwing on empty participant list |
