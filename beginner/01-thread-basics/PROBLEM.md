# Problem 01 — Thread Basics

**Difficulty:** 🟢 Beginner  
**Topics:** `Thread` class, `start()`, `run()`, thread lifecycle

---

## Problem Statement

Implement a `ThreadBasics` class that demonstrates fundamental thread creation and lifecycle management.

### Tasks

1. **`createAndStartThread(Runnable task)`** — Create a `Thread` from the given `Runnable`, start it, and return the thread.
2. **`extendThread(String name)`** — Create a thread by extending `Thread` that prints `"Hello from <name>"` and return it (do NOT start it yet).
3. **`getThreadInfo(Thread t)`** — Return a string in the format `"name=<name>,state=<state>,daemon=<isDaemon>"`.
4. **`countActiveThreads()`** — Return the number of currently active threads in the current thread group.

### Constraints
- Do not call `run()` directly — always use `start()`
- `extendThread` must return a Thread subclass (anonymous or named)

---

## Example

```java
Thread t = createAndStartThread(() -> System.out.println("Running!"));
// t is alive and running
String info = getThreadInfo(t);
// info = "name=Thread-0,state=RUNNABLE,daemon=false"
```

---

## Hints
<details>
<summary>Hint 1</summary>
`new Thread(runnable).start()` starts a thread. `thread.getState()` returns a `Thread.State` enum.
</details>

<details>
<summary>Hint 2</summary>
`Thread.activeCount()` returns the count in the current thread group. Use `ThreadGroup.activeCount()` for accuracy.
</details>

---

## Interview Notes
> **Common question:** "What's the difference between calling `start()` and `run()`?"  
> `run()` executes on the *calling* thread — no new thread is created. `start()` spawns a new OS thread and calls `run()` on it.

> **Thread States:** NEW → RUNNABLE → (BLOCKED/WAITING/TIMED_WAITING) → TERMINATED
