package com.concurrency.intermediate.p19;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class CompletablePipelineTest {

    private CompletablePipeline pipeline;
    private ExecutorService exec;

    @BeforeEach
    void setUp() {
        exec = Executors.newFixedThreadPool(4);
        pipeline = new CompletablePipeline(exec);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() throws InterruptedException {
        exec.shutdown();
        exec.awaitTermination(2, TimeUnit.SECONDS);
    }

    @Test
    void fetchAsyncReturnsSupplierValue() throws Exception {
        CompletableFuture<String> cf = pipeline.fetchAsync(() -> "hello");
        assertEquals("hello", cf.get());
    }

    @Test
    void processAsyncAppliesTransform() throws Exception {
        CompletableFuture<String> input = pipeline.fetchAsync(() -> "world");
        CompletableFuture<String> result = pipeline.processAsync(input, String::toUpperCase);
        assertEquals("WORLD", result.get());
    }

    @Test
    void combineResultsJoinsBothValues() throws Exception {
        CompletableFuture<String> a = pipeline.fetchAsync(() -> "foo");
        CompletableFuture<String> b = pipeline.fetchAsync(() -> "bar");
        CompletableFuture<String> combined = pipeline.combineResults(a, b);
        assertEquals("foo | bar", combined.get());
    }

    @Test
    void withFallbackReturnsFallbackOnException() throws Exception {
        CompletableFuture<String> failing = CompletableFuture.failedFuture(
                new RuntimeException("oops"));
        CompletableFuture<String> safe = pipeline.withFallback(failing, "default");
        assertEquals("default", safe.get());
    }

    @Test
    void withFallbackReturnsOriginalWhenSuccessful() throws Exception {
        CompletableFuture<String> ok = CompletableFuture.completedFuture("success");
        CompletableFuture<String> safe = pipeline.withFallback(ok, "fallback");
        assertEquals("success", safe.get());
    }

    @Test
    void runAllWaitsForAllFutures() throws Exception {
        AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger(0);
        List<CompletableFuture<Void>> futures = List.of(
                CompletableFuture.runAsync(counter::incrementAndGet, exec),
                CompletableFuture.runAsync(counter::incrementAndGet, exec),
                CompletableFuture.runAsync(counter::incrementAndGet, exec)
        );
        pipeline.runAll(futures).get();
        assertEquals(3, counter.get(), "runAll() must wait for all futures to complete");
    }

    @Test
    void fullPipeline() throws Exception {
        CompletableFuture<String> fetched   = pipeline.fetchAsync(() -> "  raw data  ");
        CompletableFuture<String> processed = pipeline.processAsync(fetched, String::trim);
        CompletableFuture<String> other     = pipeline.fetchAsync(() -> "extra");
        CompletableFuture<String> combined  = pipeline.combineResults(processed, other);
        assertEquals("raw data | extra", combined.get(),
                "Full pipeline should compose fetch → process → combine correctly");
    }
}
