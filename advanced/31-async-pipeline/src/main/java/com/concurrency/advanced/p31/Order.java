package com.concurrency.advanced.p31;

/**
 * Problem 31 – Async Pipeline: Domain objects and custom exceptions.
 */
public class Order {

    public final String id;
    public final int    quantity;
    public final double price;
    public volatile double discountedPrice;  // set by the discount stage

    public Order(String id, int quantity, double price) {
        this.id       = id;
        this.quantity = quantity;
        this.price    = price;
        this.discountedPrice = price;
    }

    @Override public String toString() {
        return "Order{id=" + id + ", qty=" + quantity + ", price=" + price + "}";
    }

    // ── Custom exceptions ─────────────────────────────────────────────────────

    public static class ValidationException extends RuntimeException {
        public ValidationException(String msg) { super(msg); }
    }

    public static class OutOfStockException extends RuntimeException {
        public OutOfStockException(String item) {
            super("Out of stock: " + item);
        }
    }
}
