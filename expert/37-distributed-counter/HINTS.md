# Hints — Problem 37: Distributed Counter

## Level 1 — Nudge

`LongAdder` (and its striping logic) reduces contention by maintaining a `long[]` array where each thread typically updates its own stripe. `sum()` adds all stripes. `StripedCounter` asks you to implement this idea manually — one `AtomicLong` per stripe, thread assignment by `Thread.getId() % stripes`.

---

## Level 2 — Direction

**`StripedCounter`**:
```java
private final AtomicLong[] stripes;

public StripedCounter(int stripeCount) {
    stripes = new AtomicLong[stripeCount];
    Arrays.fill(stripes, new AtomicLong(0));  // wrong — all same ref
    for (int i = 0; i < stripeCount; i++) stripes[i] = new AtomicLong(0);
}

private AtomicLong stripe() {
    return stripes[(int)(Thread.currentThread().getId() % stripes.length)];
}

public void increment() { stripe().incrementAndGet(); }
public void add(long delta) { stripe().addAndGet(delta); }
public long sum() { return Arrays.stream(stripes).mapToLong(AtomicLong::get).sum(); }
public void reset() { Arrays.stream(stripes).forEach(s -> s.set(0)); }
```

**`MetricsCollector`**: use `ConcurrentHashMap<String, AtomicLong>` for request counts and `ConcurrentHashMap<String, LongAdder>` for latency totals. `getTopEndpoints(n)`: stream entries, sort by count descending, limit to n, extract keys.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Concurrent sum is wrong | All stripes point to the same `AtomicLong` object (array fill mistake) |
| `reset()` not zeroing correctly | `set(0)` on each stripe; do not create new `AtomicLong` objects (references are held by callers) |
| `getAverageLatency` returns NaN | Dividing by zero when no requests recorded — guard with null/zero check |
