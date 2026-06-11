# ☕ Java Concurrency & Threads — Practice Problem Sets

A structured collection of **50 Java concurrency problems** organized by difficulty, both interview preparation and hands-on learning.

---

## 📚 Structure

```
java-concurrency-practice/
├── beginner/       # Problems 01–15  (Foundations)
├── intermediate/   # Problems 16–25  (Core Concurrency APIs)
└── advanced/       # Problems 26–35  (Core advanced patterns)
└── expert/         # Problems 36–50  (Deep systems design)
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

### 🟡 Intermediate (16–25)
| # | Problem | Classes | Core Concept |
|---|---------|---------|-------------|
| 16 | Semaphore            | `ConnectionPool`, `RateLimiter`         | Permits, bounded concurrency, token-bucket |
| 17 | CyclicBarrier        | `ParallelMatrixMultiplier`, `PhaseSimulator` | Reusable barrier, barrier action, phased execution |
| 18 | ExecutorService      | `TaskDispatcher`, `PrioritizedExecutor` | ThreadPoolExecutor, CallerRunsPolicy, PriorityBlockingQueue |
| 19 | Future & Callable    | `AsyncTaskRunner`, `CompletablePipeline` | Future.get(), timeout, first-completed, CompletableFuture chaining |
| 20 | ForkJoinPool         | `ParallelSum`, `ParallelMergeSort`, `ParallelSearch` | RecursiveTask, RecursiveAction, work-stealing, divide-and-conquer |
| 21 | BlockingQueue Deep Dive   | `WorkStealingPipeline`, `DelayedTaskScheduler` | Poison-pill shutdown, `DelayQueue`, `Delayed` interface |
| 22 | Exchanger                 | `DoubleBufferedLogger`, `GeneticCrossover`    | Bidirectional handoff, double-buffering pattern |
| 23 | Phaser                    | `DynamicWorkerPool`, `PipelinedDataProcessor` | Dynamic registration, `onAdvance`, `arriveAndDeregister` |
| 24 | StampedLock               | `OptimisticPoint`, `StampedCache`             | Optimistic reads, `validate()`, `tryConvertToWriteLock` |
| 25 | ConcurrentHashMap         | `WordFrequencyCounter`, `ConcurrentInventory` | `merge`, `compute`, `replace` CAS loop, parallel bulk ops |

### 🔴 Advanced (26–35)
| #  | Problem                  | Skeleton Classes                                          | Core Concept                                      |
|----|--------------------------|-----------------------------------------------------------|---------------------------------------------------|
| 26 | Lock-Free Data Structures| `TreiberStack`, `LockFreeQueue`, `ABADemonstrator`        | CAS retry loops, sentinel node, ABA problem       |
| 27 | Java Memory Model        | `SafePublicationShowcase`, `HappensBeforeChain`, `MemoryVisibilityProbe` | happens-before rules, safe publication, volatile |
| 28 | Deadlock Detection       | `ResourceAllocationGraph`, `DeadlockPreventer`, `DeadlockDemo` | RAG cycle detection, lock ordering, tryLock, ThreadMXBean |
| 29 | ScheduledExecutorService | `TaskScheduler`, `CircuitBreaker`                         | scheduleAtFixedRate vs fixedDelay, circuit breaker state machine |
| 30 | Custom ThreadPoolExecutor| `InstrumentedThreadPool`, `WorkerThreadFactory`, `BoundedCallerRunsPool` | hook methods, ThreadLocal latency, CallerRunsPolicy |
| 31 | Async Pipeline | `AsyncOrderProcessor`, `PipelineMerger`, `Order` | `thenCompose`, `thenCombine`, `allOf`, `anyOf`, `exceptionally`, fan-out/fan-in |
| 32 | Work-Stealing Deque | `WorkStealingDeque<T>`, `WorkStealingScheduler` | Owner LIFO pop, thief FIFO steal, circular array, grow(), ForkJoinPool internals |
| 33 | STM Simulation | `TVar<T>`, `Transaction`, `STM` | Optimistic concurrency, versioned reads, commit/retry protocol, composable atomicity |
| 34 | Actor Model | `Actor<S,M>`, `BankAccountActor`, `ActorSystem`, `PingPongActors` | Message-passing, mailbox, tell/ask, fire-and-forget vs request-reply, no shared state |
| 35 | Concurrent Skip List | `ConcurrentSkipListSet` (lock-based), `LockFreeSkipListSet` (lock-free), `SkipListBenchmark` | O(log n) probabilistic structure, hand-over-hand locking, AtomicMarkableReference, logical deletion, linearisability |

### 🔴 Expert (36–50)
| # | Problem | Key Concept |
|---|---------|-------------|
| 36 | Reactive Streams | Flow.Publisher/Subscriber, back-pressure, TransformProcessor |
| 37 | Distributed Counter | StripedCounter, LongAdder, MetricsCollector |
| 38 | Priority Task Executor | PriorityBlockingQueue, PriorityTask, FIFO tie-breaking |
| 39 | Concurrent LRU Cache | LinkedHashMap accessOrder, ReadWriteLock |
| 40 | Thread-Safe Object Pool | LinkedBlockingQueue, AutoCloseable borrow/return |
| 41 | Custom Barrier | ReentrantLock+Condition, generation counter, BrokenBarrierException |
| 42 | Async Event Bus | ConcurrentHashMap, CopyOnWriteArrayList, class hierarchy dispatch |
| 43 | Concurrent Graph Traversal | Parallel BFS (ExecutorService), Parallel DFS (ForkJoinPool) |
| 44 | Rate-Limited Executor | Token bucket (lazy refill), tryAcquire with timeout |
| 45 | Lock-Free Ring Buffer | SPSC volatile head/tail, MPSC AtomicLong+ready flags |
| 46 | Concurrent Trie | computeIfAbsent, volatile isEnd, DFS collect |
| 47 | 2PC Transaction Manager | Parallel prepare/commit/rollback, ExecutorService |
| 48 | Thread Confinement Pool | ThreadLocal, DateFormatterPool, ConfinedWorkerPool |
| 49 | Parallel Merge Sort Advanced | ForkJoinPool, RecursiveAction, adaptiveThreshold, binarySearch |
| 50 | Mini Job Scheduler (Capstone) | Priority queue, token bucket, metrics, graceful shutdown |
---

## 🚀 Getting Started

### Prerequisites
- Java 11+
- Maven 3.6+ 
- ⚠️ Java 17 required — Problems 34 and 35 use sealed interface and record types. Update your root pom.xml compiler target to 17:

```xml
<maven.compiler.source>17</maven.compiler.source>
<maven.compiler.target>17</maven.compiler.target>
```

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
