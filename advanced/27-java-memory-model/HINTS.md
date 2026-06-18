# Hints — Problem 27: Java Memory Model

## Level 1 — Nudge

The JMM defines when writes by one thread are guaranteed visible to reads by another thread. A happens-before (HB) relationship is the guarantee. Key HB rules: volatile write HB subsequent volatile read; monitor unlock HB subsequent lock of the same monitor; thread termination HB join on that thread.

---

## Level 2 — Direction

**`SafePublicationShowcase`**:
- `synchronized`: any write inside the synchronized block happens-before any subsequent read inside the same monitor — the HB is on the monitor, not on the field type
- `volatile`: the write to the `volatile` field HB the read — but both reader and writer must access the *same* volatile variable
- `final` fields: a write to a `final` field in the constructor HB any subsequent read of that field, as long as the object reference does not escape during construction
- "Unsafe" publication: no HB → reader may see 0 or stale values; this is the educational contrast

**`HappensBeforeChain`**:
- Thread i writes `values[i]` to a plain (non-volatile) field, then counts down a `CountDownLatch`
- Thread i+1 awaits that latch before reading the value — `countDown` HB `await`, so the write is visible
- Chain: T0→latch0→T1→latch1→T2... by transitivity, T2 sees T0'\''s write

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `BuggyVisibility` test is flaky (sometimes works) | JIT optimisation varies; add `Thread.sleep` and try with `-server` JVM flag |
| `HappensBeforeChain` sum is wrong | Threads read values before the preceding thread'\''s `countDown` — check the await happens before the read |
| Unsafe publication test always passes | The JVM on your machine happens to be consistent; this bug is not deterministically reproducible — the test documents the risk |
