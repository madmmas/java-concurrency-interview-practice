# Hints — Problem 15: FizzBuzz Threads

## Level 1 — Nudge

Four threads must print numbers in order, but only the right thread prints at each step. You need a shared `current` counter and a way for threads to know whether it is "their turn." Threads that are not up should wait; the thread that just printed should wake the others.

---

## Level 2 — Direction

**Core pattern** (using `synchronized` + `wait`/`notifyAll`):
```java
// inside fizz():
synchronized (lock) {
    while (current <= n) {
        if (current % 3 == 0 && current % 15 != 0) {
            printFizz.run();
            current++;
            lock.notifyAll();
        } else if (current % 15 == 0 || current % 5 == 0 || ...) {
            lock.wait();  // not my turn
        } else {
            lock.wait();  // not my turn
        }
    }
}
```

Simpler structure: each thread's while loop checks `current <= n`, then checks whether this number belongs to it. If not → `wait()`. If yes → print, increment, `notifyAll()`.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Output is out of order | Forgot `notifyAll()` after incrementing, or using `notify()` (wakes wrong thread) |
| One thread hogs all output | The `while` condition allows the same thread to run again before others check |
| Test deadlocks at the end | Threads wait forever after `current > n` — need `notifyAll()` when done so all threads can exit their loops |

---

## The exit condition

When `current > n`, all four threads must wake up and exit their loops. Make sure `notifyAll()` is called after the last increment, and each thread checks `current <= n` as the outer while condition — not just the divisibility check.

