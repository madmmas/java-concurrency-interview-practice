package com.concurrency.beginner.p10;

/**
 * Problem 10 – ThreadLocal: Per-Request Context
 *
 * Stores per-thread request metadata without any synchronization.
 * Each thread has its own userId and requestId.
 */
public class RequestContext {

    // TODO: declare two ThreadLocal fields — one for userId, one for requestId
    private static final ThreadLocal<String> userIdLocal    = new ThreadLocal<>();
    private static final ThreadLocal<String> requestIdLocal = new ThreadLocal<>();

    /** Sets the user ID for the current thread. */
    public static void setUserId(String userId) {
        // TODO
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the user ID for the current thread, or null if not set. */
    public static String getUserId() {
        // TODO
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Sets the request ID for the current thread. */
    public static void setRequestId(String requestId) {
        // TODO
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the request ID for the current thread, or null if not set. */
    public static String getRequestId() {
        // TODO
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Clears both ThreadLocal values for the current thread.
     * MUST be called when the thread finishes its task (e.g., in a finally block)
     * to avoid memory leaks in thread pools.
     */
    public static void clear() {
        // TODO: call remove() on both ThreadLocals
        throw new UnsupportedOperationException("Implement this method");
    }
}
