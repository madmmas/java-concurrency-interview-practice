# Problem 26 — Lock-Free Data Structures (Treiber Stack & Michael-Scott Queue)

## 🔴 Difficulty: Advanced

## 📖 Background

Lock-free data structures guarantee system-wide progress: at least one thread
always makes forward progress, even if individual threads are paused.
They achieve this using hardware **Compare-And-Swap (CAS)** via `AtomicReference`.

### Treiber Stack (1986) — lock-free LIFO

```
push(v):
    newNode = Node(v)
    do:
        oldTop = top.get()
        newNode.next = oldTop
    while !top.CAS(oldTop, newNode)   // retry if another thread raced us

pop():
    do:
        oldTop = top.get()
        if oldTop == null: return empty
        newTop = oldTop.next
    while !top.CAS(oldTop, newTop)
    return oldTop.value
```

### Michael-Scott Queue (1996) — lock-free FIFO
Uses a **dummy sentinel** head node so head and tail are never null:
- `enqueue`: CAS `tail.next` from null → newNode, then swing `tail` forward
- `dequeue`: CAS `head` from sentinel → `sentinel.next`

### ABA Problem
Thread 1 reads `top = A`. Thread 2 pops A, pushes B, pushes A back.
Thread 1's CAS sees `top == A` and succeeds — but the structure changed!
Fix: `AtomicStampedReference<T>` pairs value with an integer version stamp.

## 🎯 Task

### `TreiberStack<T>`
- `push(T item)` — CAS retry loop
- `pop()` — CAS retry loop; returns `null` if empty
- `peek()` — non-blocking volatile read of head
- `isEmpty()` / `size()`

### `LockFreeQueue<T>`
- Sentinel-node design (head always points to dummy)
- `enqueue(T item)` — CAS on `tail.next`; swing `tail`
- `dequeue()` — CAS on `head`; returns `null` if empty
- `isEmpty()` / `size()`

### `ABADemonstrator`
- `demonstrateABA()` — returns `true` when naive `AtomicReference` CAS
  succeeds despite the value having been changed-and-restored
- `demonstrateABAFix()` — returns `false` when `AtomicStampedReference`
  correctly rejects the same scenario because the stamp changed

## 💡 Hints
- Push: set `newNode.next = oldTop` **before** the CAS so the node is
  fully linked even if another thread reads the stack mid-update
- MS-Queue enqueue: if you see `tail.next != null`, help advance `tail`
  first (another thread's partial enqueue)
- ABA: change ref A→B→A between Thread 1's read and CAS; naive ref
  can't tell the difference, stamped ref can

## 🧠 Interview Talking Points
- What does "lock-free" guarantee vs "wait-free"?
- Describe the ABA problem with a concrete linked-list example.
- How does `AtomicStampedReference` prevent ABA?
- What is safe memory reclamation (hazard pointers) and why does it matter
  in lock-free structures?
- Why is the Treiber Stack's `size()` only approximate?
