# Problem 14 — Thread-Safe Singleton

## 🟢 Difficulty: Beginner

## 📖 Background

The Singleton pattern restricts instantiation to one object. Making it thread-safe
under lazy initialisation is a classic interview topic with several approaches:

### Approach 1: Eager Initialisation
```java
private static final Singleton INSTANCE = new Singleton();
public static Singleton getInstance() { return INSTANCE; }
```
✅ Simple, thread-safe by class-loading guarantee. ❌ Created even if never used.

### Approach 2: Synchronized Method (naive)
```java
public static synchronized Singleton getInstance() {
    if (instance == null) instance = new Singleton();
    return instance;
}
```
✅ Correct. ❌ Every call acquires the lock — huge performance cost after initialisation.

### Approach 3: Double-Checked Locking (DCL)
```java
private static volatile Singleton instance;
public static Singleton getInstance() {
    if (instance == null) {                    // check 1 — no lock
        synchronized (Singleton.class) {
            if (instance == null) {            // check 2 — under lock
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```
✅ Lazy, ✅ fast (lock only during creation), ✅ correct **if `volatile` is present**.
`volatile` prevents the JVM from reordering the write to `instance` before the
constructor body completes (the infamous "partially constructed object" bug).

### Approach 4: Initialisation-on-Demand Holder (Bill Pugh)
```java
private static class Holder {
    static final Singleton INSTANCE = new Singleton();
}
public static Singleton getInstance() { return Holder.INSTANCE; }
```
✅ Lazy, ✅ no synchronisation overhead, ✅ thread-safe via class-loader guarantee.
This is the **recommended** approach in modern Java.

## 🎯 Task

Implement **four** singleton variants — each in its own class — with a shared
`getCreationCount()` counter so tests can verify only one instance is ever made:

1. `EagerSingleton` — eager initialisation
2. `SynchronizedSingleton` — synchronized `getInstance()`
3. `DoubleCheckedSingleton` — DCL with `volatile`
4. `HolderSingleton` — initialisation-on-demand holder

Each class must:
- Have a private constructor
- Expose `getInstance()` returning the single instance
- Expose `static int getCreationCount()` returning how many times the constructor ran
- Have an `getId()` method returning a unique ID assigned at construction time
  (use `AtomicInteger` so concurrent construction is detectable)

## 💡 Hints
- Use `static AtomicInteger creationCount` — increment it in the private constructor
- For DCL, the field **must** be `volatile` — tests will stress-test concurrent calls
- The Holder's inner class must be `private static`

## 🧠 Interview Talking Points
- Why is DCL broken without `volatile`?
- What JMM guarantee makes the Holder pattern thread-safe without `volatile`?
- What is the "partially constructed object" problem?
- Is eager initialisation always a bad idea? When is it preferable?
