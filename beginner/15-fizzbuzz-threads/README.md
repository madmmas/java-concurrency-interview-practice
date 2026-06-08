# Problem 15 — FizzBuzz with Four Threads

## 🟢 Difficulty: Beginner  *(coordination focus)*

## 📖 Background

This is LeetCode #1195 — "FizzBuzz Multithreaded". It looks simple but is a
great test of inter-thread coordination with `wait()`/`notify()` or `Semaphore`.

The classic FizzBuzz rules for numbers 1..n:
- divisible by 15 → print "FizzBuzz"
- divisible by  3 → print "Fizz"
- divisible by  5 → print "Buzz"
- otherwise       → print the number

**The multithreaded twist:** You have exactly **four threads**, each responsible
for exactly one case. They must coordinate so the output is in order 1..n.

The challenge: without coordination, the threads would race. With too much
coordination (e.g., a single global lock), it degrades to sequential. The
goal is to produce output in the correct order using `wait()`/`notifyAll()`
or semaphores, letting only the appropriate thread proceed at each step.

## 🎯 Task

Implement `FizzBuzzPrinter` with four methods meant to be called on four
separate threads simultaneously:

- `fizz(Runnable printFizz)` — calls `printFizz.run()` for each multiple of 3
  (but not 15)
- `buzz(Runnable printBuzz)` — calls `printBuzz.run()` for each multiple of 5
  (but not 15)
- `fizzbuzz(Runnable printFizzBuzz)` — calls `printFizzBuzz.run()` for each
  multiple of 15
- `number(IntConsumer printNumber)` — calls `printNumber.accept(i)` for each
  number that is not divisible by 3 or 5

All four methods run **concurrently**, but the `printXxx` callbacks must be
invoked in the correct numerical order (1, 2, Fizz, 4, Buzz, Fizz, 7, ...).

The constructor takes `int n` — the upper bound (inclusive).

## 💡 Hints

**Strategy using `synchronized` + `wait()`/`notifyAll()`:**
- Keep a shared `current` counter (starts at 1)
- Each thread loops: acquire lock → check if it's "my turn" → if not, `wait()` →
  if yes, invoke the callback, increment `current`, `notifyAll()`
- Use `while` (not `if`) before `wait()` to guard against spurious wakeups

```java
synchronized (lock) {
    while (current <= n && current % 3 != 0 || current % 15 == 0) {
        lock.wait();
    }
    if (current > n) return;
    printFizz.run();
    current++;
    lock.notifyAll();
}
```

Alternatively: use four `Semaphore`s — one per thread, each starting at 0
except the "number" semaphore which starts at 1. Each thread acquires its own
semaphore, does work, then releases the correct next semaphore.

## 🧠 Interview Talking Points
- Why use `while` instead of `if` before `wait()`?
- How would you implement this with Semaphores instead of wait/notify?
- What is the risk of using `notify()` instead of `notifyAll()` here?
- How does this pattern generalise to "N threads, each handling one case"?
