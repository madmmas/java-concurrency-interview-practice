package com.concurrency.advanced.p31;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Problem 31 – Async Pipeline: Fan-Out / Fan-In Merger
 *
 * Demonstrates three composition patterns:
 *   fetchAll   — parallel fan-out, all-of fan-in (preserves order)
 *   fetchFirst — parallel fan-out, any-of fan-in (first wins)
 *   withFallback — single pipeline with exceptionally() fallback
 */
public class PipelineMerger {

    private final Executor executor;

    public PipelineMerger(Executor executor) {
        this.executor = executor;
    }

    /**
     * Launches all suppliers in parallel on the executor.
     * Waits for ALL of them to complete and returns the results in the
     * same order as the input list.
     *
     * Uses CompletableFuture.allOf() + stream().map(CF::join).
     *
     * @param sources list of suppliers; each may block or throw
     * @return CompletableFuture that completes with a list of all results
     *         in submission order, or completes exceptionally if any source fails
     */
    public CompletableFuture<List<String>> fetchAll(List<Supplier<String>> sources) {
        // TODO:
        //   List<CompletableFuture<String>> futures = sources.stream()
        //       .map(s -> CompletableFuture.supplyAsync(s, executor))
        //       .collect(Collectors.toList());
        //   CompletableFuture<Void> all = CompletableFuture.allOf(
        //       futures.toArray(new CompletableFuture[0]));
        //   return all.thenApply(v ->
        //       futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        throw new UnsupportedOperationException("Implement fetchAll()");
    }

    /**
     * Launches all suppliers in parallel on the executor.
     * Returns the result of whichever supplier completes first.
     *
     * Uses CompletableFuture.anyOf().
     *
     * @param sources non-empty list of suppliers
     * @return CompletableFuture that completes with the first available result
     */
    public CompletableFuture<String> fetchFirst(List<Supplier<String>> sources) {
        // TODO:
        //   CompletableFuture<?>[] futures = sources.stream()
        //       .map(s -> CompletableFuture.supplyAsync(s, executor))
        //       .toArray(CompletableFuture[]::new);
        //   return CompletableFuture.anyOf(futures).thenApply(o -> (String) o);
        throw new UnsupportedOperationException("Implement fetchFirst()");
    }

    /**
     * Returns primary's result on success.
     * If primary completes exceptionally, invokes fallback.get() and returns
     * that value instead (the exception is swallowed).
     *
     * Uses exceptionally().
     *
     * @param primary  the preferred async computation
     * @param fallback called synchronously when primary fails
     * @return CompletableFuture that never completes exceptionally (assuming fallback succeeds)
     */
    public CompletableFuture<String> withFallback(CompletableFuture<String> primary,
                                                   Supplier<String> fallback) {
        // TODO: return primary.exceptionally(ex -> fallback.get());
        throw new UnsupportedOperationException("Implement withFallback()");
    }
}
