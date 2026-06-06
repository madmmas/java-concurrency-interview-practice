# Problem 02 — Runnable vs Thread

## 🟢 Difficulty: Beginner

## 📖 Background

Extending `Thread` works, but is considered bad practice for most cases because Java only allows **single inheritance**. If your class already extends something else, you can't also extend `Thread`.

The preferred approach is to implement the `Runnable` interface:

```java
Runnable task = () -> System.out.println("Hello from runnable");
Thread t = new Thread(task, "my-thread");
t.start();
```

Benefits of `Runnable`:
- Separates the **task** (what to do) from the **execution mechanism** (how/when to run it)
- Compatible with `ExecutorService` and thread pools
- Works with lambda expressions

## 🎯 Task

Implement `TaskRunner` which:

1. Has a method `runTask(Runnable task, String threadName)` that executes `task` on a **new thread** with the given name, then **waits for it to finish** before returning
2. Has a method `runTasksParallel(List<Runnable> tasks)` that runs **all tasks in parallel** on separate threads (each named `"worker-N"` starting from 0) and waits for all to complete before returning
3. Has a method `buildCountingRunnable(List<Integer> collector, int value, int times)` that returns a `Runnable` which adds `value` to `collector` exactly `times` times. This list will be a thread-safe list passed in by the caller.

## 📋 Skeleton

See `src/main/java/com/concurrency/beginner/p02/TaskRunner.java`

## 💡 Hints

- Use `new Thread(runnable, name)` constructor
- Don't forget to call `thread.join()` to wait for completion
- For parallel tasks, start all threads first, then join all of them

## 🧠 Interview Talking Points

- Why prefer `Runnable` over extending `Thread`?
- What is `Callable` and how does it differ from `Runnable`?
- What does `join()` guarantee about memory visibility?
