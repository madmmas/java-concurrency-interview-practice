# Problem 28 — Deadlock Detection & Prevention

## 🔴 Difficulty: Advanced

## 📖 Background

A **deadlock** occurs when two or more threads each hold a resource and wait for
a resource held by another — forming a **cycle** in the wait-for graph.

### Coffman's four necessary conditions
All four must hold simultaneously for a deadlock to occur:

| Condition | Meaning | How to break |
|-----------|---------|-------------|
| **Mutual exclusion** | Resources can't be shared | Use lock-free structures |
| **Hold and wait** | Thread holds one resource while waiting for another | `tryLock` with timeout |
| **No preemption** | Resources can't be forcibly taken | N/A in Java |
| **Circular wait** | Cycle in the wait-for graph | Lock ordering |

### Prevention strategies

**Lock ordering** — always acquire locks in a consistent global order (e.g., by
`System.identityHashCode`). Two threads acquiring the same two locks in the same
order can never form a cycle.

**`tryLock` with timeout** — if the second lock can't be acquired within the
deadline, release the first lock and retry. Breaks the "hold and wait" condition.

**Resource Allocation Graph (RAG)** — runtime detection: model which threads hold
which resources and build a directed wait-for graph; a cycle means deadlock.

### JVM deadlock detection via `ThreadMXBean`
```java
ThreadMXBean bean = ManagementFactory.getThreadMXBean();
long[] deadlockedIds = bean.findDeadlockedThreads();
// null if no deadlock; otherwise IDs of all threads in deadlock cycles
```

## 🎯 Task

### `ResourceAllocationGraph`
A runtime deadlock detector modelling the RAG:
- `requestResource(threadName, resource)` — request edge: thread → resource
- `assignResource(threadName, resource)` — assignment edge: resource → thread (and remove request)
- `releaseResource(threadName, resource)` — remove both edges
- `hasDeadlock()` — DFS cycle detection in the wait-for graph; `true` = deadlock
- `getDeadlockedThreads()` — set of thread names in detected cycles

### `DeadlockPreventer`
Two lock-acquisition helpers that guarantee freedom from deadlock:
- `acquireInOrder(lockA, lockB)` — acquires both in `identityHashCode` order;
  returns a `Runnable` releaser that unlocks both in reverse order
- `tryAcquireBothWithTimeout(lockA, lockB, timeoutMs)` — CAS-retry loop using
  `tryLock`; releases first lock if second times out; returns `true` on success

### `DeadlockDemo`
Educational: creates a real deadlock and detects it:
- `createDeadlock()` — starts two threads that acquire two plain `Object` monitors
  in opposite orders; returns `Thread[]{t1, t2}` after both are stuck
- `detectDeadlock(t1, t2)` — uses `ThreadMXBean.findDeadlockedThreads()` to confirm

## 💡 Hints
- `ResourceAllocationGraph`: build a `Map<String,String> waitForThread` where
  `waitForThread.get(T)` = "the thread holding the resource T is waiting for";
  then DFS with a `visited` set and a `onStack` set to detect back-edges
- `acquireInOrder`: tie-break equal `identityHashCode` values with a static
  `ReentrantLock tieBreakerLock` (acquire it as a third lock around both acquisitions)
- `createDeadlock`: use `synchronized(objA)` / `synchronized(objB)` blocks; sleep
  100 ms inside the outer block so both threads enter before either hits the inner lock

## 🧠 Interview Talking Points
- State Coffman's four conditions. Which is easiest to break in Java?
- How does lock ordering break circular wait? What is its limitation?
- How does `tryLock` break hold-and-wait?
- What is a livelock? How does it differ from deadlock?
- Walk me through building a wait-for graph and detecting a cycle.
