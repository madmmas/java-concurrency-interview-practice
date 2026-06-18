# Hints — Problem 32: Work-Stealing Deque

## Level 1 — Nudge

The owner always works from the *bottom* (push and pop). Thieves steal from the *top*. The owner is the only thread that moves `bottom`, so it does not need locking against itself. Thieves contend with each other on `top`, so they need a lock (or CAS in the full lock-free version).

---

## Level 2 — Direction

**`WorkStealingDeque`** simplified design:
- Circular array `Object[] tasks` of power-of-two size
- `int bottom` — owner'\''s index (no lock needed, only owner touches it)
- `AtomicInteger top` — thieves'\'' index

```java
// push (owner only)
tasks[bottom % capacity] = task;
bottom++;

// pop (owner only)
bottom--;
Object t = tasks[bottom % capacity];
if (bottom <= top.get()) { bottom++; return null; }  // empty
return (T) t;

// steal (any thread, under lock)
int t = top.get();
if (t >= bottom) return null;  // empty
Object task = tasks[t % capacity];
return top.compareAndSet(t, t+1) ? (T) task : null;  // CAS or lock
```

**`WorkStealingScheduler`**: round-robin submission to spread initial load; each worker'\''s loop: pop own deque, else pick a random victim and steal, else yield.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Items lost under concurrent steal + pop | Race between owner decrementing bottom and thief reading at that index — check boundary condition when `bottom <= top` |
| ArrayIndexOutOfBoundsException | Not using `% capacity` consistently, or capacity is 0 |
| Scheduler starves some workers | Steal picks the same victim repeatedly — randomize victim selection |
