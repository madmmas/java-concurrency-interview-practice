# Hints — Problem 28: Deadlock Detection

## Level 1 — Nudge

A deadlock is a cycle in the resource allocation graph. Model it as a wait-for graph: if thread T1 holds resource R and thread T2 holds resource S, and T1 is waiting for S while T2 is waiting for R — that is a cycle. DFS on the graph detects cycles.

---

## Level 2 — Direction

**`ResourceAllocationGraph`** — simplify to a wait-for graph:
- `Map<String, String> waitingFor` — thread → thread it is waiting for (who holds the resource it needs)
- `requestResource(thread, resource)`: find who holds `resource` (if anyone), add edge thread → holder
- `assignResource(thread, resource)`: mark thread as holder of resource, remove its request edge
- `hasDeadlock()`: DFS from each node; track visited + recursion stack — if you re-visit a node in the current stack, there is a cycle

**`DeadlockPreventer.acquireInOrder`**: compare `System.identityHashCode(lockA)` vs `lockB`; always lock the lower-hash object first.

**`DeadlockDemo.detectDeadlock`**:
```java
long[] deadlocked = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
// returns null if no deadlock, or array of thread IDs
```

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `hasDeadlock()` never returns true | DFS not tracking recursion stack — visited set prevents detecting cycles in the current path |
| `acquireInOrder` still deadlocks | Using object reference comparison (`lockA == lockB` address) which is not stable across JVM runs — use `identityHashCode` |
| `findDeadlockedThreads` returns null | Deadlock not actually established yet — add a sleep after starting the two threads |
