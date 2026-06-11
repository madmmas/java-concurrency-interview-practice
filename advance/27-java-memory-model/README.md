# Problem 27 — Java Memory Model & happens-before

## 🔴 Difficulty: Advanced

## 📖 Background

The **Java Memory Model (JMM)** defines when writes by one thread are **guaranteed**
to be visible to reads by another. Without a happens-before (HB) relationship, the
JVM and CPU are free to reorder operations and keep values in caches, making writes
invisible to other threads.

### happens-before rules (complete list)

| Rule | Establishes HB between |
|------|----------------------|
| **Program order** | Each action and the next in the same thread |
| **Monitor unlock** | `unlock()` and every subsequent `lock()` on the same monitor |
| **Volatile write** | A `volatile` write and every subsequent read of that field |
| **Thread start** | `thread.start()` and every action in the new thread |
| **Thread join** | Every action in a thread and a successful return from `join()` |
| **Interruption** | `thread.interrupt()` and the point the interrupted thread detects it |
| **Finalizer** | End of constructor and start of the object's finalizer |
| **Transitivity** | If A HB B and B HB C → A HB C |

### Safe publication
An object is **safely published** if its reference is made visible after construction
completes, via: volatile field, final field, synchronized block, or a thread-safe
collection.

### `final` fields
A `final` field written in a constructor is visible to **any** thread that obtains
the reference **after the constructor returns**, without additional synchronisation —
the JMM's "freeze" guarantee.

## 🎯 Task

### `SafePublicationShowcase`
Four publication patterns — two correct, emphasising *why* each works:
- `publishViaSynchronized(int value)` / `getSynchronized()` — monitor unlock HB monitor lock
- `publishViaVolatile(int value)` / `getVolatile()` — volatile write HB volatile read
- `publishViaFinalField(int value)` / `getFinalFieldHolder()` — final freeze + thread join HB
- `publishUnsafe(int value)` — plain field, no HB → NOT guaranteed visible (educational)

### `HappensBeforeChain`
Demonstrates **transitivity** across a chain of N threads, each passing a value to
the next through a volatile handoff:
- `runChain(int[] values)` — starts N threads; thread[i] writes `values[i]` to a
  volatile variable, then signals thread[i+1] via a `CountDownLatch`; blocks until
  all N threads complete; returns the sum of all values (proves all writes were visible)

### `MemoryVisibilityProbe`
Two inner classes that contrast the visibility bug and its fix:
- `BuggyVisibility` — plain `boolean running`; worker may spin forever (JVM caches flag)
- `FixedVisibility` — `volatile boolean running`; worker always sees the stop signal

## 💡 Hints
- `HappensBeforeChain`: the **volatile write + countDown** in thread[i] happens-before the
  **await + volatile read** in thread[i+1]; by transitivity thread[N-1] sees all prior writes
- `FixedVisibility.stop()` must call `join()` so the test can assert the thread has terminated
- `BuggyVisibility` is structurally correct in the single-threaded view; the test only
  verifies flag-setting, not the actual spin (which is JVM/hardware-dependent)

## 🧠 Interview Talking Points
- List all eight happens-before rules in the JMM.
- Why is `volatile` insufficient for compound operations like `count++`?
- What are the four ways to safely publish an object in Java?
- What is the `final` field freeze guarantee? How does it relate to immutability?
- What is instruction reordering and which layer does it? (compiler / JIT / CPU)
