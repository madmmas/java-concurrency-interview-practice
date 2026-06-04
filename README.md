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

### 🟡 Intermediate (16–35) *(coming soon)*
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
