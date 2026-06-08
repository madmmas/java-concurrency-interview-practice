# Problem 08 — Thread Interruption

## 🟢 Difficulty: Beginner

## 📖 Background

Java's cooperative cancellation mechanism is based on the **interrupt flag**:

- `thread.interrupt()` — sets the interrupted flag on the target thread
- `Thread.interrupted()` — returns and **clears** the flag (static, checks current thread)
- `thread.isInterrupted()` — returns the flag **without clearing** it
- Blocking methods (`sleep`, `wait`, `join`) throw `InterruptedException` when interrupted, and **clear the flag** automatically

**The golden rules of interruption:**
1. Never swallow `InterruptedException` silently — either rethrow it or restore the flag
2. Check `Thread.currentThread().isInterrupted()` in long loops
3. Prefer cooperative cancellation over `Thread.stop()` (which is deprecated and unsafe)

```java
// Correct pattern for interruptible loops
while (!Thread.currentThread().isInterrupted()) {
    doWork();
}

// Correct pattern when catching InterruptedException
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt(); // restore flag
    return;                             // or rethrow
}
```

## 🎯 Task

Implement `InterruptibleWorker`:
- `startCounting(long durationMs)` — starts a thread that counts up for `durationMs` ms, then stops naturally. Checks the interrupt flag in its loop.
- `cancel()` — interrupts the worker thread immediately
- `getCount()` — returns how many iterations ran
- `wasInterrupted()` — returns `true` if the thread exited due to interruption

Implement `GracefulSleeper`:
- `sleepFor(long millis)` — sleeps for the given duration on a new thread
- `interrupt()` — interrupts the sleeping thread
- `didCompleteNormally()` — returns `true` if sleep finished without interruption
- `didGetInterrupted()` — returns `true` if an `InterruptedException` was caught

## 💡 Hints
- In the counting loop, check `!Thread.currentThread().isInterrupted()` as the condition
- When catching `InterruptedException`, set a flag to record it happened, then return
- Use `System.currentTimeMillis()` to implement `durationMs` bound

## 🧠 Interview Talking Points
- Why is `Thread.stop()` deprecated?
- What is the difference between `Thread.interrupted()` and `isInterrupted()`?
- Why must you never do `catch (InterruptedException e) {}`?
- How does `InterruptedException` interact with the interrupt flag?
