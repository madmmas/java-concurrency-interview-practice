package com.concurrency.intermediate.p25;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Problem 25 – ConcurrentHashMap: Thread-Safe Inventory
 *
 * All operations use ConcurrentHashMap atomic methods — no synchronized blocks.
 */
public class ConcurrentInventory {

    private final ConcurrentHashMap<String, Integer> stock = new ConcurrentHashMap<>();

    /**
     * Adds quantity to the stock for item.
     * Uses map.merge() for a lock-free atomic update.
     */
    public void addItem(String item, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        // TODO: stock.merge(item, quantity, Integer::sum)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Decreases stock by quantity. Removes the key when stock reaches 0.
     * Throws IllegalStateException if stock would go negative.
     *
     * Use stock.compute(item, (k, current) -> { ... }) — the lambda is called atomically.
     */
    public void removeItem(String item, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        // TODO:
        //  stock.compute(item, (k, current) -> {
        //      if (current == null || current < quantity)
        //          throw new IllegalStateException("Insufficient stock for: " + item);
        //      return current == quantity ? null : current - quantity;
        //  })
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Atomically reserves (decreases) stock if sufficient quantity is available.
     * Returns true if reservation succeeded; false if insufficient stock.
     *
     * Use a CAS loop with stock.replace(item, expectedQty, newQty) for atomicity.
     */
    public boolean reserveItem(String item, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        // TODO: CAS loop:
        //  while (true) {
        //    Integer current = stock.get(item);
        //    if (current == null || current < quantity) return false;
        //    int newQty = current - quantity;
        //    if (newQty == 0 ? stock.remove(item, current) : stock.replace(item, current, newQty))
        //        return true;
        //    // else: CAS failed → retry
        //  }
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns current stock for item, or 0 if not present. */
    public int getStock(String item) {
        return stock.getOrDefault(item, 0);
    }

    /** Returns an unmodifiable snapshot of the entire inventory. */
    public Map<String, Integer> getSnapshot() {
        return Collections.unmodifiableMap(new HashMap<>(stock));
    }
}
