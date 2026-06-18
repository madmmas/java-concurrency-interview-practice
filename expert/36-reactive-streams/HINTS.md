# Hints — Problem 36: Reactive Streams

## Level 1 — Nudge

Reactive Streams (Java 9 `java.util.concurrent.Flow`) has four contracts: `Publisher` produces items, `Subscriber` consumes them, `Subscription` controls demand, and `Processor` is both. The critical rule: a publisher must never emit more items than the subscriber has requested via `subscription.request(n)`.

---

## Level 2 — Direction

**`SimplePublisher<T>`**:
- On subscribe: create a `Subscription` that tracks `requested` count (use `AtomicLong`)
- `Subscription.request(n)`: add n to `requested`; start emitting items up to that count
- Emit loop: while `requested > 0` and items remain: `subscriber.onNext(item)`, decrement `requested`
- After all items: `subscriber.onComplete()`
- Run the emit loop on a separate thread so `subscribe()` is non-blocking

**`TransformProcessor<T, R>`** implements both `Subscriber<T>` and `Publisher<R>`:
- Stores the downstream `subscriber` and the upstream `subscription`
- `onNext(item)`: apply the transform function, call `downstream.onNext(result)`
- `onComplete()`: call `downstream.onComplete()`
- `request(n)` in downstream subscription: delegate to upstream `subscription.request(n)`

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `backPressureIsHonoured` test fails (too many items) | Not tracking `requested` count — emitting all items regardless |
| `awaitCompletion` never returns | `onComplete()` not called, or the latch in `BufferingSubscriber` not counted down |
| Processor test fails | Processor not forwarding `request()` calls upstream — subscriber requests go into a void |
