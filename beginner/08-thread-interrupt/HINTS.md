# Hints — Problem 08: Thread Interruption

## Level 1 — Nudge

Java's cancellation model is cooperative: you signal a thread, it decides what to do about it. The signal is the interrupt flag. Check `Thread.currentThread().isInterrupted()` in your loop condition. When blocking methods (`sleep`, `wait`, `join`) are interrupted, they throw `InterruptedException` and clear the flag.

---

## Level 2 — Direction

**`InterruptibleWorker`** loop pattern:
```java
while (!Thread.currentThread().isInterrupted()) {
    count++;
    // optional: also check System.currentTimeMillis() < deadline
}
```
When `cancel()` is called, call `thread.interrupt()`. In the thread's logic, catch `InterruptedException` from any sleep calls, record that interruption happened, and return.

**`GracefulSleeper`**:
```java
try {
    Thread.sleep(millis);
    completedNormally = true;
} catch (InterruptedException e) {
    gotInterrupted = true;
    // do NOT re-throw; just record and return
}
```

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `wasInterrupted()` always returns false | You caught `InterruptedException` but never set your flag |
| Loop does not stop after `cancel()` | Missing `isInterrupted()` check in loop condition |
| `didCompleteNormally()` is wrong | `completedNormally` set before the catch block, not inside the try after sleep |

---

## The golden rule

Never swallow `InterruptedException` silently with an empty catch block. Either restore the flag (`Thread.currentThread().interrupt()`) or record that it happened and exit cleanly.

