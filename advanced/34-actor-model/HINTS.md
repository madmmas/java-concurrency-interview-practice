# Hints — Problem 34: Actor Model

## Level 1 — Nudge

An actor'\''s mailbox is a `LinkedBlockingQueue`. The processing loop runs on a single thread — this is what makes the state safe without locks. External threads never touch the actor'\''s state directly; they only put messages in the mailbox.

---

## Level 2 — Direction

**`Actor` mailbox loop** (runs on the executor):
```java
private void processNext() {
    envelope = mailbox.poll();
    if (envelope != null) {
        Object result = receive(state, envelope.message);
        if (envelope.replyTo != null) envelope.replyTo.complete(result);
    }
    if (!stopped) executor.execute(this::processNext);  // re-schedule
}
```
Start the loop by calling `executor.execute(this::processNext)` in the constructor or `start()`.

**`ask(message)`**:
```java
CompletableFuture<Object> future = new CompletableFuture<>();
mailbox.offer(new Envelope(message, future));
return future;
```

**`BankAccountActor.receive`**: pattern-match on the command type using `instanceof` (or sealed classes), update the `state` (balance), return the new balance.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `ask()` future never completes | `replyTo` future not completed in the processing loop — check `envelope.replyTo` is non-null before completing |
| State is corrupted | Accessing actor state from outside `receive()` — all state changes must go through the mailbox |
| Actor processes messages out of order | Using a thread pool with concurrency > 1 — actor'\''s executor must be single-threaded |
