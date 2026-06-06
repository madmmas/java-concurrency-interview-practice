# Problem 01 — Thread Basics

## 🟢 Difficulty: Beginner

## 📖 Background

In Java, a `Thread` represents an independent path of execution. Every Java application starts with a single **main thread**. You can create additional threads by:

1. **Extending `Thread`** and overriding `run()`
2. **Implementing `Runnable`** and passing it to a `Thread`

Key methods:
- `thread.start()` — schedules the thread for execution (non-blocking)
- `thread.run()` — executes the task **on the calling thread** (blocking, NOT what you want for concurrency)
- `Thread.currentThread().getName()` — returns the current thread's name
- `Thread.sleep(ms)` — pauses the current thread for a given duration

## 🎯 Task

Implement the `PrintingThread` class that:

1. Extends `Thread`
2. Accepts a `String message` and an `int repeatCount` in its constructor
3. When `start()` is called, the thread prints `message` exactly `repeatCount` times to stdout, each on its own line
4. The thread should also record (store) the name of the thread it ran on so it can be inspected after execution

## 📋 Skeleton

See `src/main/java/com/concurrency/beginner/p01/PrintingThread.java`

## 💡 Hints

- Call `super(name)` in the constructor to give the thread a meaningful name
- `Thread.currentThread().getName()` inside `run()` returns the thread's own name
- Do **NOT** call `run()` directly — always use `start()`

## 🧠 Interview Talking Points

- What is the difference between `start()` and `run()`?
- What happens if you call `run()` instead of `start()`?
- Can a thread be started more than once?
- What thread states exist in Java? (`NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`, `TERMINATED`)
