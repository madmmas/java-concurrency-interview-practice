# ☕ Java Concurrency & Threads — Practice Problem Sets

A structured collection of **50 Java concurrency problems** organized by difficulty, both interview preparation and hands-on learning.

---

## 📚 Structure

```
java-concurrency-practice/
├── beginner/       # Problems 01–15  (Foundations)
├── intermediate/   # Problems 16–35  (Core Concurrency APIs)
└── advanced/       # Problems 36–50  (Expert-Level Patterns)
```

Each problem folder contains:
- `README.md` — Problem description, concepts covered, hints
- `src/main/java/` — Skeleton class(es) for you to implement
- `src/test/java/` — JUnit 5 tests to validate your solution

---

## 🗺️ Problem Index

### 🟢 Beginner (01–15)
| # | Problem | Key Concept |
|---|---------|-------------|
| 01 | Thread Basics | `Thread` class, `start()`, `run()` |
| 02 | Runnable vs Thread | `Runnable`, lambda threads |
| 03 | Synchronized Counter | `synchronized`, race conditions |
| 04 | Producer-Consumer Basic | `wait()`, `notify()` |
| 05 | Thread Join | `join()`, ordering guarantees |
| 06 | Volatile Keyword     | Memory visibility, JMM, stop-flags |
| 07 | Daemon Threads       | Daemon vs user threads, background services |
| 08 | Thread Interruption  | Cooperative cancellation, `InterruptedException` |
| 09 | AtomicInteger & CAS  | Lock-free programming, Compare-And-Swap |
| 10 | ThreadLocal          | Per-thread storage, memory-leak pitfall |
| 11 | ReentrantLock | Explicit locking, `Condition`, deadlock-free transfer |
| 12 | ReadWriteLock | Concurrent reads, exclusive writes, lock downgrading |
| 13 | CountDownLatch | Startup gate, starting-gun pattern, timed await |
| 14 | Thread-Safe Singleton | Eager / Synchronized / DCL+volatile / Holder |
| 15 | FizzBuzz Threads | 4-thread coordination with wait/notifyAll |

### 🟡 Intermediate (16–35)
| # | Problem | Classes | Core Concept |
|---|---------|---------|-------------|
| 16 | Semaphore            | `ConnectionPool`, `RateLimiter`         | Permits, bounded concurrency, token-bucket |
| 17 | CyclicBarrier        | `ParallelMatrixMultiplier`, `PhaseSimulator` | Reusable barrier, barrier action, phased execution |
| 18 | ExecutorService      | `TaskDispatcher`, `PrioritizedExecutor` | ThreadPoolExecutor, CallerRunsPolicy, PriorityBlockingQueue |
| 19 | Future & Callable    | `AsyncTaskRunner`, `CompletablePipeline` | Future.get(), timeout, first-completed, CompletableFuture chaining |
| 20 | ForkJoinPool         | `ParallelSum`, `ParallelMergeSort`, `ParallelSearch` | RecursiveTask, RecursiveAction, work-stealing, divide-and-conquer |

### 🔴 Advanced (36–50) *(coming soon)*

---

## 🚀 Getting Started

### Prerequisites
- Java 11+
- Maven 3.6+

### Run Tests for a Single Problem
```bash
cd beginner/01-thread-basics
mvn test
```

---

## 💡 How to Use This Repo

1. Read the `README.md` inside each problem folder
2. Open the skeleton class in `src/main/java/`
3. Implement the solution — **do not modify the test files**
4. Run `mvn test` to verify

---
