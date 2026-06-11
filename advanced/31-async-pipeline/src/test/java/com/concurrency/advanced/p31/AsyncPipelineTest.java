package com.concurrency.advanced.p31;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class AsyncPipelineTest {

    private ExecutorService exec;

    @BeforeEach  void setUp()                     { exec = Executors.newFixedThreadPool(4); }
    @AfterEach   void tearDown() throws Exception { exec.shutdown(); exec.awaitTermination(3, TimeUnit.SECONDS); }

    // ── AsyncOrderProcessor ───────────────────────────────────────────────────

    private AsyncOrderProcessor processor(Map<String, Integer> inv, double disc) {
        return new AsyncOrderProcessor(exec, new ConcurrentHashMap<>(inv), disc);
    }

    @Test
    void validOrderProducesReceipt() throws Exception {
        AsyncOrderProcessor proc = processor(Map.of("item-1", 10), 0);
        String receipt = proc.process(new Order("item-1", 2, 100.0)).get();
        assertNotNull(receipt);
        assertTrue(receipt.startsWith("RECEIPT:item-1"),
                "Receipt must identify the order; got: " + receipt);
    }

    @Test
    void persistedReceiptsAreRecorded() throws Exception {
        AsyncOrderProcessor proc = processor(Map.of("item-1", 10), 0);
        proc.process(new Order("item-1", 1, 50.0)).get();
        assertEquals(1, proc.getPersistedReceipts().size());
    }

    @Test
    void invalidQuantityFailsWithValidationException() {
        AsyncOrderProcessor proc = processor(Map.of("item-1", 10), 0);
        ExecutionException ex = assertThrows(ExecutionException.class,
                () -> proc.process(new Order("item-1", 0, 100.0)).get());
        assertInstanceOf(Order.ValidationException.class, ex.getCause(),
                "Zero quantity must cause ValidationException");
    }

    @Test
    void negativePriceFailsWithValidationException() {
        AsyncOrderProcessor proc = processor(Map.of("item-1", 10), 0);
        ExecutionException ex = assertThrows(ExecutionException.class,
                () -> proc.process(new Order("item-1", 1, -5.0)).get());
        assertInstanceOf(Order.ValidationException.class, ex.getCause());
    }

    @Test
    void insufficientInventoryFailsWithOutOfStockException() {
        AsyncOrderProcessor proc = processor(Map.of("item-1", 1), 0);
        ExecutionException ex = assertThrows(ExecutionException.class,
                () -> proc.process(new Order("item-1", 5, 100.0)).get());
        assertInstanceOf(Order.OutOfStockException.class, ex.getCause(),
                "Insufficient stock must cause OutOfStockException");
    }

    @Test
    void discountIsAppliedToFinalPrice() throws Exception {
        AsyncOrderProcessor proc = processor(Map.of("item-1", 10), 20); // 20% off
        String receipt = proc.process(new Order("item-1", 1, 100.0)).get();
        // discountedPrice = 80.0
        assertTrue(receipt.contains("price=80.0") || receipt.contains("price=80"),
                "Receipt must show discounted price 80.0; got: " + receipt);
    }

    @Test
    void multipleOrdersProcessedConcurrently() throws Exception {
        Map<String, Integer> inv = new ConcurrentHashMap<>();
        for (int i = 0; i < 10; i++) inv.put("item-" + i, 100);
        AsyncOrderProcessor proc = new AsyncOrderProcessor(exec, inv, 0);

        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            futures.add(proc.process(new Order("item-" + i, 1, 50.0)));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        assertEquals(10, proc.getPersistedReceipts().size(),
                "All 10 orders must produce receipts");
    }

    @Test
    void failedOrderDoesNotAddReceipt() throws Exception {
        AsyncOrderProcessor proc = processor(Map.of("item-1", 1), 0);
        try { proc.process(new Order("item-1", 99, 100.0)).get(); }
        catch (ExecutionException ignored) {}
        assertEquals(0, proc.getPersistedReceipts().size(),
                "Failed order must not produce a receipt");
    }

    // ── PipelineMerger ────────────────────────────────────────────────────────

    @Test
    void fetchAllReturnsAllResultsInOrder() throws Exception {
        PipelineMerger merger = new PipelineMerger(exec);
        List<String> results = merger.fetchAll(List.of(
                () -> "alpha", () -> "beta", () -> "gamma"
        )).get();
        assertEquals(List.of("alpha", "beta", "gamma"), results,
                "fetchAll must preserve submission order");
    }

    @Test
    void fetchAllFailsIfAnySourceFails() {
        PipelineMerger merger = new PipelineMerger(exec);
        ExecutionException ex = assertThrows(ExecutionException.class,
                () -> merger.fetchAll(List.of(
                        () -> "ok",
                        () -> { throw new RuntimeException("source down"); },
                        () -> "ok2"
                )).get());
        assertNotNull(ex.getCause());
    }

    @Test
    void fetchFirstReturnsASingleResult() throws Exception {
        PipelineMerger merger = new PipelineMerger(exec);
        String result = merger.fetchFirst(List.of(
                () -> { try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } return "slow"; },
                () -> "fast",
                () -> { try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } return "also-slow"; }
        )).get();
        assertEquals("fast", result, "fetchFirst must return the fastest result");
    }

    @Test
    void withFallbackReturnsPrimaryOnSuccess() throws Exception {
        PipelineMerger merger = new PipelineMerger(exec);
        CompletableFuture<String> primary = CompletableFuture.completedFuture("primary");
        String result = merger.withFallback(primary, () -> "fallback").get();
        assertEquals("primary", result);
    }

    @Test
    void withFallbackReturnsFallbackOnFailure() throws Exception {
        PipelineMerger merger = new PipelineMerger(exec);
        CompletableFuture<String> failing = CompletableFuture.failedFuture(
                new RuntimeException("primary failed"));
        String result = merger.withFallback(failing, () -> "fallback").get();
        assertEquals("fallback", result,
                "withFallback must return the fallback value when primary fails");
    }

    @Test
    void fetchAllWithTenSourcesIsCorrect() throws Exception {
        PipelineMerger merger = new PipelineMerger(exec);
        List<Supplier<String>> sources = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final int idx = i;
            sources.add(() -> "result-" + idx);
        }
        List<String> results = merger.fetchAll(sources).get();
        assertEquals(10, results.size());
        for (int i = 0; i < 10; i++) {
            assertEquals("result-" + i, results.get(i));
        }
    }
}
