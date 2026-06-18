# Hints — Problem 48: Thread Confinement Pool

## Level 1 — Nudge

Thread confinement means one instance per thread, created on first access and reused for that thread'\''s lifetime. `ThreadLocal.withInitial(factory)` does exactly this. The "pool" here is conceptual — it'\''s not a shared pool of objects, it'\''s a per-thread registry.

---

## Level 2 — Direction

**`ConfinedResourcePool<T>`**:
```java
private final Supplier<T> factory;
private final AtomicInteger instanceCount = new AtomicInteger(0);
private final ThreadLocal<T> local = ThreadLocal.withInitial(() -> {
    instanceCount.incrementAndGet();
    return factory.get();
});

public T get() { return local.get(); }
public void remove() { local.remove(); }
public int getInstanceCount() { return instanceCount.get(); }
```

**`DateFormatterPool`**: same pattern with `ThreadLocal<SimpleDateFormat>`:
```java
private final ThreadLocal<SimpleDateFormat> formatter;

public DateFormatterPool(String pattern) {
    formatter = ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern));
}
public String format(Date date) { return formatter.get().format(date); }
public Date parse(String s) throws ParseException { return formatter.get().parse(s); }
```
`SimpleDateFormat` is not thread-safe — one per thread is the canonical fix.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `differentThreadsDifferentInstances` fails | Using a regular field or static field instead of `ThreadLocal` |
| `removeCreatesNewOnNextGet` fails | `remove()` not called before checking — or factory not invoked again after removal |
| `dateFormatterConcurrentNeverCorrupts` flaky | Sharing one `SimpleDateFormat` across threads — `ThreadLocal` not applied to the formatter |
