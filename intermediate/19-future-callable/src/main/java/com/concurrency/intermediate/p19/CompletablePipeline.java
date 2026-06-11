package com.concurrency.intermediate.p19;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Problem 19 – Future & Callable: CompletableFuture Pipeline
 *
 * Thin wrappers around CompletableFuture that compose an async processing pipeline.
 * All methods are stateless — no fields needed.
 */
public class CompletablePipeline {

    private final Executor executor;

    public CompletablePipeline(Executor executor) {
        this.executor = executor;
    }

    /**
     * Wraps a supplier in a CompletableFuture that runs on the given executor.
     */
    public CompletableFuture<String> fetchAsync(Supplier<String> source) {
        // TODO: return CompletableFuture.supplyAsync(source, executor)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Applies transform to the result of input asynchronously.
     */
    public CompletableFuture<String> processAsync(
            CompletableFuture<String> input, Function<String, String> transform) {
        // TODO: return input.thenApplyAsync(transform, executor)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Combines results of two futures: result = a + " | " + b
     */
    public CompletableFuture<String> combineResults(
            CompletableFuture<String> a, CompletableFuture<String> b) {
        // TODO: return a.thenCombine(b, (ra, rb) -> ra + " | " + rb)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Returns a new future that completes with the original result,
     * or with `fallback` if the original completes exceptionally.
     */
    public CompletableFuture<String> withFallback(
            CompletableFuture<String> cf, String fallback) {
        // TODO: return cf.exceptionally(ex -> fallback)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Returns a CompletableFuture<Void> that completes when ALL input futures complete.
     */
    public CompletableFuture<Void> runAll(List<CompletableFuture<Void>> futures) {
        // TODO: return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        throw new UnsupportedOperationException("Implement this method");
    }
}
