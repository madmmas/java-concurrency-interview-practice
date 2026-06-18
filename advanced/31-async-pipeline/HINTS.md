# Hints — Problem 31: Async Pipeline

## Level 1 — Nudge

`thenCompose` chains a stage that itself returns a `CompletableFuture` (flatMap). `thenApply` chains a stage that returns a plain value (map). `thenCombine` runs two stages concurrently and combines their results when both complete. `exceptionally` catches any exception in the chain and returns a fallback.

---

## Level 2 — Direction

**`AsyncOrderProcessor` pipeline**:
```java
CompletableFuture<Order> validated = validateAsync(order);
CompletableFuture<Integer> stock   = validated.thenComposeAsync(o -> checkInventoryAsync(o), exec);
CompletableFuture<Double>  discount = validated.thenApplyAsync(o -> applyDiscount(o), exec);
return stock.thenCombine(discount, (s, d) -> mergeOrder(order, d))
            .thenComposeAsync(o -> persistAsync(o), exec)
            .exceptionally(e -> handleError(e));
```
CheckInventory and ApplyDiscount run concurrently on different branches of the same `validated` future.

**`PipelineMerger.fetchAll`**:
```java
List<CompletableFuture<String>> futures = sources.stream()
    .map(s -> CompletableFuture.supplyAsync(s, exec))
    .collect(toList());
return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
    .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(toList()));
```

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Inventory and discount run sequentially | Both chained with `thenCompose` from `validated` — correct — but executor is null so they share the same thread |
| `fetchFirst` returns wrong result | Using `allOf` instead of `anyOf` |
| Exceptions swallowed silently | `exceptionally` not applied to the terminal stage |
