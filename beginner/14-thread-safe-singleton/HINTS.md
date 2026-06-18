# Hints — Problem 14: Thread-Safe Singleton

## Level 1 — Nudge

You need four separate singleton implementations. For each one, ask: when does the instance get created — at class load time or on first use? And: what prevents two threads from creating two instances simultaneously?

---

## Level 2 — Direction

**Eager**: `private static final Singleton INSTANCE = new Singleton();` — the JVM initialises this exactly once when the class loads. No synchronization needed.

**Synchronized**: straightforward — add `synchronized` to `getInstance()`. Correct but every call acquires the lock even after the instance exists.

**DCL (Double-Checked Locking)**:
```java
private static volatile Singleton instance;
public static Singleton getInstance() {
    if (instance == null) {                   // first check — no lock
        synchronized (Singleton.class) {
            if (instance == null) {           // second check — under lock
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```
The `volatile` is mandatory. Without it, the JVM may publish a partially-constructed object.

**Initialization-on-Demand Holder**:
```java
private static class Holder {
    static final Singleton INSTANCE = new Singleton();
}
public static Singleton getInstance() { return Holder.INSTANCE; }
```
The inner class is not loaded until `getInstance()` is called — lazy. The class loader guarantees one-time initialisation — thread-safe without locks.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `getCreationCount()` > 1 in DCL test | Missing `volatile` on the instance field |
| Holder pattern not lazy | Accessed the `Holder` class somewhere before `getInstance()` is called |
| Synchronized test fails under concurrency | `synchronized` on a wrong object (instance method instead of static method or class lock) |

