# Hints — Problem 19: Future & Callable

## Level 1 — Nudge

`Future.get()` blocks until the result is ready (or throws). `CompletableFuture` lets you chain transformations without blocking. The key question for `runFirstCompleted` is: how do you get *the first finished result* from a set of submitted tasks without waiting for all of them?

---

## Level 2 — Direction

**`runAll`**: submit each `Callable` to get a `List<Future<T>>`, then iterate the list calling `future.get()` on each — preserves submission order.

**`runWithTimeout`**: `future.get(timeoutMs, TimeUnit.MILLISECONDS)` — throws `TimeoutException` if not done in time; let it propagate.

**`runFirstCompleted`**: use `ExecutorCompletionService`:
```java
ExecutorCompletionService<T> ecs = new ExecutorCompletionService<>(executor);
tasks.forEach(ecs::submit);
T result = ecs.take().get();  // blocks until the first completes
```
Cancel the remaining futures after taking the first result.

**`CompletablePipeline`**:
- `fetchAsync` → `CompletableFuture.supplyAsync(source, executor)`
- `processAsync` → `input.thenApplyAsync(transform, executor)`
- `combineResults` → `a.thenCombine(b, (ra, rb) -> ra + " | " + rb)`
- `withFallback` → `cf.exceptionally(e -> fallback)`

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `runAll` returns out of order | You sorted by completion time — use index-based iteration on the original future list |
| `runFirstCompleted` waits for all tasks | Using `invokeAll` instead of `CompletionService.take()` |
| `withFallback` throws instead of returning fallback | Applied `exceptionally` before the stage that can fail, not after |
