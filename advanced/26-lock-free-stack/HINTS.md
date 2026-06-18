# Hints — Problem 26: Lock-Free Data Structures

## Level 1 — Nudge

Lock-free means using `AtomicReference.compareAndSet()` instead of `synchronized`. The CAS retry loop pattern: read the current head, build the new state, attempt CAS — if it fails (someone else changed head), loop and try again. The node must be fully linked *before* the CAS.

---

## Level 2 — Direction

**`TreiberStack.push`**:
```java
Node<T> newNode = new Node<>(item);
Node<T> oldTop;
do {
    oldTop = head.get();
    newNode.next = oldTop;        // link first
} while (!head.compareAndSet(oldTop, newNode));  // then CAS
```

**`LockFreeQueue`** (Michael-Scott queue):
- Sentinel/dummy node: `head` and `tail` both start pointing to it
- `enqueue`: CAS `tail.next` from null to new node; then swing `tail` forward
- `dequeue`: CAS `head` from current dummy to `head.next` (which becomes new dummy); return old `head.next.value`

**`ABADemonstrator`**:
- Create shared `AtomicReference<String> ref = new AtomicReference<>("A")`
- Thread 1 reads "A" then is paused
- Thread 2 changes A→B→A
- Thread 1'\''s CAS succeeds (sees "A") even though state changed — that is the ABA
- Fix: `AtomicStampedReference` — stamp changes even when value reverts

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Stack loses items under concurrency | Setting `newNode.next` after the CAS — another thread may read a partially linked node |
| Queue dequeue returns wrong item | Returning `head.get().value` (the dummy node) instead of `head.get().next.value` |
| ABA fix still demonstrates the bug | Using `AtomicStampedReference` but always passing stamp 0 — stamps must actually change |
