package com.concurrency.advanced.p31;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * Problem 31 – Async Pipeline: Order Processor
 *
 * A four-stage async processing pipeline built with CompletableFuture.
 *
 * Stage flow:
 *   validate(order)
 *     └─ thenComposeAsync ──► checkInventory(order)   ──┐
 *                              applyDiscount(order)   ──┤  thenCombine
 *                                                        └─► merge ──► persist ──► receipt
 *
 * All stages run on the provided executor — no stage blocks a thread.
 */
public class AsyncOrderProcessor {

    private final Executor executor;

    /** Simulated inventory: item-id → available quantity */
    private final Map<String, Integer> inventory;

    /** Discount percentage to apply (0–100). */
    private final double discountPct;

    /** Receipts for successfully persisted orders — thread-safe. */
    private final List<String> persistedReceipts = new CopyOnWriteArrayList<>();

    public AsyncOrderProcessor(Executor executor,
                                Map<String, Integer> inventory,
                                double discountPct) {
        this.executor    = executor;
        this.inventory   = inventory;
        this.discountPct = discountPct;
    }

    // ── Public entry point ────────────────────────────────────────────────────

    /**
     * Processes an order through the full pipeline asynchronously.
     *
     * @return a CompletableFuture that completes with a receipt string on
     *         success, or completes exceptionally with ValidationException /
     *         OutOfStockException on failure
     */
    public CompletableFuture<String> process(Order order) {
        // TODO: chain the four stages:
        //
        //   return validate(order)
        //       .thenComposeAsync(validOrder -> {
        //           CompletableFuture<Order> inventoryCF = checkInventory(validOrder);
        //           CompletableFuture<Order> discountCF  = applyDiscount(validOrder);
        //           return inventoryCF.thenCombineAsync(discountCF,
        //               (checkedOrder, discountedOrder) -> {
        //                   checkedOrder.discountedPrice = discountedOrder.discountedPrice;
        //                   return checkedOrder;
        //               }, executor);
        //       }, executor)
        //       .thenApplyAsync(this::persist, executor);
        throw new UnsupportedOperationException("Implement process()");
    }

    // ── Pipeline stages ───────────────────────────────────────────────────────

    /**
     * Stage 1 – Validate.
     * Completes immediately (no I/O). Returns the order if valid; otherwise
     * completes exceptionally with ValidationException.
     */
    CompletableFuture<Order> validate(Order order) {
        // TODO:
        //   if (order.quantity <= 0 || order.price <= 0) {
        //       CompletableFuture<Order> failed = new CompletableFuture<>();
        //       failed.completeExceptionally(new Order.ValidationException("Invalid order: " + order));
        //       return failed;
        //   }
        //   return CompletableFuture.completedFuture(order);
        throw new UnsupportedOperationException("Implement validate()");
    }

    /**
     * Stage 2 – Check inventory (simulated slow I/O).
     * Completes exceptionally with OutOfStockException if insufficient stock.
     * Otherwise decrements the inventory and returns the order.
     */
    CompletableFuture<Order> checkInventory(Order order) {
        // TODO: run on executor using CompletableFuture.supplyAsync(() -> { ... }, executor)
        //
        // Use an atomic compute() to avoid a check-then-act race under concurrent orders:
        //
        //   boolean[] outOfStock = { false };
        //   inventory.compute(order.id, (id, current) -> {
        //       int stock = (current == null) ? 0 : current;
        //       if (stock < order.quantity) { outOfStock[0] = true; return stock; }
        //       return stock - order.quantity == 0 ? null : stock - order.quantity;
        //   });
        //   if (outOfStock[0]) throw new Order.OutOfStockException(order.id);
        //   return order;
        throw new UnsupportedOperationException("Implement checkInventory()");
    }

    /**
     * Stage 3 – Apply discount (CPU-only, no I/O).
     * Sets order.discountedPrice = order.price * (1 - discountPct/100).
     * Returns the same order object.
     */
    CompletableFuture<Order> applyDiscount(Order order) {
        // TODO: return CompletableFuture.supplyAsync(() -> {
        //   Order copy = new Order(order.id, order.quantity, order.price);
        //   copy.discountedPrice = order.price * (1.0 - discountPct / 100.0);
        //   return copy;
        // }, executor);
        throw new UnsupportedOperationException("Implement applyDiscount()");
    }

    /**
     * Stage 4 – Persist (simulated write).
     * Adds a receipt string to persistedReceipts and returns it.
     * Receipt format: "RECEIPT:{order.id}:qty={order.quantity}:price={order.discountedPrice}"
     */
    String persist(Order order) {
        // TODO:
        //   String receipt = "RECEIPT:" + order.id
        //       + ":qty=" + order.quantity
        //       + ":price=" + order.discountedPrice;
        //   persistedReceipts.add(receipt);
        //   return receipt;
        throw new UnsupportedOperationException("Implement persist()");
    }

    /** Returns all receipts for orders that completed successfully. */
    public List<String> getPersistedReceipts() {
        return persistedReceipts;
    }
}
