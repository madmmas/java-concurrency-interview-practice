# Hints — Problem 42: Async Event Bus

## Level 1 — Nudge

The event bus maps event types to subscriber lists. `ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Consumer<?>>>` gives you thread-safe registration and snapshot-iteration during dispatch. Events are dispatched asynchronously on an `ExecutorService`.

---

## Level 2 — Direction

**Core structure**:
```java
private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Consumer<Object>>> subscribers
    = new ConcurrentHashMap<>();
private final ExecutorService executor;
```

**`subscribe(type, handler)`**: `subscribers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(handler)`. Return a `Subscription` token that stores the type and the handler reference.

**`post(event)`**: look up `event.getClass()`, get the list, dispatch each handler on the executor:
```java
List<Consumer<Object>> handlers = subscribers.get(event.getClass());
if (handlers != null) handlers.forEach(h -> executor.submit(() -> {
    try { h.accept(event); }
    catch (Exception e) { if (exceptionHandler != null) exceptionHandler.handle(e, event, h); }
}));
```

**`postToAll(event)`**: dispatch to handlers registered for the event'\''s exact class *and* any supertype/interface — iterate all registered types and check `type.isAssignableFrom(event.getClass())`.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `unsubscribeStopsDelivery` fails | Not removing the specific handler — `remove()` on `CopyOnWriteArrayList` uses `equals()` — store the same lambda reference in the subscription token |
| Exception handler not called | Swallowing exception in the dispatch wrapper instead of forwarding to `exceptionHandler` |
| `postToAll` misses interface subscribers | Using `==` for type comparison instead of `isAssignableFrom` |
