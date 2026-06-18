# Hints — Problem 45: Lock-Free Ring Buffer

## Level 1 — Nudge

SPSC (Single Producer, Single Consumer): only one thread writes `head`, only one thread writes `tail`. `volatile` on both is sufficient for visibility — no atomic CAS needed. The buffer must be a power-of-two size so you can use bitmasking: `index & (capacity - 1)`.

---

## Level 2 — Direction

**`LockFreeRingBuffer` (SPSC)**:
```java
private volatile long head = 0;  // consumer reads here
private volatile long tail = 0;  // producer writes here
private final Object[] buffer;
private final int mask;

public boolean offer(T item) {
    long t = tail, h = head;
    if (t - h >= buffer.length) return false;  // full
    buffer[(int)(t & mask)] = item;
    tail = t + 1;  // volatile write — makes item visible
    return true;
}

public T poll() {
    long h = head, t = tail;
    if (h == t) return null;  // empty
    T item = (T) buffer[(int)(h & mask)];
    head = h + 1;
    return item;
}
```
Note: write item to buffer *before* advancing `tail` so consumer never reads an uninitialized slot.

**`MultiProducerRingBuffer`**: use `AtomicLong tail`; each producer CAS-claims a slot, writes the item, then sets a `ready[slot]` flag (AtomicBoolean array); consumer waits for `ready[head]` before reading.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `mustBePowerOfTwo` test ignored | Not throwing `IllegalArgumentException` in constructor when size is not a power of two: `(size & (size-1)) != 0` |
| SPSC concurrent test produces wrong order | Consumer reads before producer'\''s write is visible — ensure `tail` write is *after* buffer write (ordering matters for volatile) |
| MPSC misses items | Consumer reads slot before producer sets `ready` flag — spin on `ready[headSlot]` before reading |
