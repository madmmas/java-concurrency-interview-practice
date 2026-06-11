# Problem 19 — Future & Callable

## 🟡 Difficulty: Intermediate

## 📖 Background

`Callable<V>` is like `Runnable` but can **return a value** and **throw checked
exceptions**. `Future<V>` represents the pending result of an async computation.

```java
Callable<Integer> task = () -> heavyComputation();
Future<Integer>   f    = executor.submit(task);

// Do other work in the meantime...

Integer result = f.get();              // blocks until done
Integer result = f.get(1, SECONDS);   // with timeout
f.cancel(true);                        // attempt cancellation
f.isDone();                            // non-blocking check
f.isCancelled();                       // was it cancelled?
```

**`ExecutionException`:** If the `Callable` throws, `f.get()` wraps it in an
`ExecutionException`. Always unwrap with `e.getCause()`.

**`CompletableFuture` (Java 8+):** A richer, non-blocking alternative that
supports chaining (`.thenApply()`, `.thenCompose()`), combining
(`.allOf()`, `.anyOf()`), and exception handling (`.exceptionally()`).

```java
CompletableFuture<String> cf = CompletableFuture
    .supplyAsync(() -> fetchData())
    .thenApply(data -> transform(data))
    .exceptionally(ex -> "fallback");
```

## 🎯 Task

### `AsyncTaskRunner`
Runs tasks asynchronously and collects results using `Future`:
- `submitTask(Callable<T> task)` — submits and returns a `Future<T>`
- `runAll(List<Callable<T>> tasks)` — submits all and returns results as `List<T>`,
  preserving submission order, blocking until all complete
- `runWithTimeout(Callable<T> task, long timeoutMs)` — returns the result, or
  throws `TimeoutException` if not done within `timeoutMs`
- `runFirstCompleted(List<Callable<T>> tasks)` — returns the result of whichever
  task completes first (cancel the rest)
- `shutdown()`

### `CompletablePipeline`
A data-processing pipeline using `CompletableFuture`:
- `fetchAsync(Supplier<String> source)` — wraps a supplier in `supplyAsync`
- `processAsync(CompletableFuture<String> input, Function<String,String> transform)` — chains `thenApplyAsync`
- `combineResults(CompletableFuture<String> a, CompletableFuture<String> b)` — combines
  both results as `a + " | " + b` using `thenCombine`
- `withFallback(CompletableFuture<String> cf, String fallback)` — returns `cf`
  with `exceptionally` applied to return `fallback` on any exception
- `runAll(List<CompletableFuture<Void>> futures)` — waits for all using `CompletableFuture.allOf`

## 💡 Hints
- `runFirstCompleted`: submit all tasks, loop with `executor.invokeAny()` — or
  submit manually and use a `CompletionService` (`ExecutorCompletionService`)
- `CompletablePipeline` methods should each be thin wrappers; no raw thread management

## 🧠 Interview Talking Points
- What is the difference between `Future.get()` and `CompletableFuture.join()`?
- How does `ExecutorCompletionService` help you get the first completed result?
- What is the difference between `thenApply` and `thenApplyAsync`?
- How do you handle exceptions in a `CompletableFuture` chain?
- What does `CompletableFuture.allOf()` return, and why is `join()` needed afterwards?
