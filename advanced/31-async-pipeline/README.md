# Problem 31 — Async Pipeline with CompletableFuture

## 🔴 Difficulty: Advanced

## 📖 Background

A **reactive/async pipeline** chains asynchronous stages where each stage
transforms data and passes it to the next, all without blocking threads.
`CompletableFuture` is Java's built-in mechanism for this style.

### Key `CompletableFuture` composition operators

| Operator | Input → Output | Notes |
|----------|---------------|-------|
| `thenApply(fn)` | `T → U` | Transform result on same pool |
| `thenApplyAsync(fn, exec)` | `T → U` | Transform on given executor |
| `thenCompose(fn)` | `T → CF<U>` | Flat-map — avoids `CF<CF<T>>` |
| `thenCombine(other, fn)` | `T, U → V` | Join two independent futures |
| `allOf(cfs...)` | `CF<Void>` | Wait for all; results via `join()` |
| `anyOf(cfs...)` | `CF<Object>` | First to complete wins |
| `exceptionally(fn)` | `Throwable → T` | Fallback on failure |
| `handle(fn)` | `(T, Throwable) → U` | Always runs; handles both paths |
| `whenComplete(action)` | side-effect | Does not transform the result |

### Pipeline patterns

```
fetch → validate → enrich → persist
         │                     │
         └── fallback on err   └── audit log (whenComplete)
```

The power is that **no thread blocks** waiting for another stage —
the continuation is registered as a callback and only runs when the
upstream `CompletableFuture` completes.

## 🎯 Task

### `AsyncOrderProcessor`

Simulates an order-processing pipeline with 4 async stages:

1. **Validate** — checks `order.quantity > 0` and `order.price > 0`; returns
   the order if valid, completes exceptionally with `ValidationException` if not
2. **CheckInventory** — looks up stock in a simulated (slow) store; completes
   exceptionally with `OutOfStockException` if insufficient
3. **ApplyDiscount** — applies a percentage discount to the order price
   (runs concurrently alongside CheckInventory using `thenCombine`)
4. **Persist** — writes the final order to a result list; returns a receipt string

Pipeline entry point:
- `process(Order order)` → `CompletableFuture<String>` (the receipt)
- Each stage runs on the provided `ExecutorService`

### `PipelineMerger`

Demonstrates fan-out / fan-in patterns:
- `fetchAll(List<Supplier<String>> sources)` → `CompletableFuture<List<String>>`
  Launches all sources in parallel and returns all results in submission order
  (uses `allOf` + `join`)
- `fetchFirst(List<Supplier<String>> sources)` → `CompletableFuture<String>`
  Returns the result from whichever source completes first
  (uses `anyOf`)
- `withFallback(CompletableFuture<String> primary, Supplier<String> fallback)`
  → `CompletableFuture<String>`
  Returns `primary`'s result if successful; otherwise returns `fallback.get()`

## 💡 Hints

- `process()` chain: `validate(order)` → `.thenComposeAsync(o -> checkInventory(o), exec)`
  then `thenCombineAsync(applyDiscount(o), (checked, discounted) -> merge)` → `thenApplyAsync(persist)`
- `fetchAll`: `List<CompletableFuture<String>> futures = sources.stream().map(s ->
  CompletableFuture.supplyAsync(s, exec)).collect(...)`;
  then `CompletableFuture.allOf(array).thenApply(v -> futures.stream().map(CF::join).collect(...))`
- `fetchFirst`: `CompletableFuture.anyOf(array).thenApply(o -> (String) o)`
- `withFallback`: `primary.exceptionally(ex -> fallback.get())`

## 🧠 Interview Talking Points

- What is the difference between `thenApply` and `thenCompose`?
- What happens if an intermediate stage throws an exception and there is no
  `exceptionally` / `handle` downstream?
- What executor does `thenApply` (no `Async` suffix) use?
- How does `CompletableFuture.allOf` differ from `ExecutorService.invokeAll`?
- What is the difference between `exceptionally` and `handle`?
- When would you use `whenComplete` vs `handle`?
