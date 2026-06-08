package com.concurrency.beginner.p10;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 10 – ThreadLocal: Per-Thread DecimalFormat
 *
 * DecimalFormat is NOT thread-safe, but creating one per call is expensive.
 * Solution: keep one instance per thread using ThreadLocal.
 *
 * Each thread must get its own DecimalFormat; sharing between threads is a bug.
 */
public class PerThreadFormatter {

    private final AtomicInteger instanceCount = new AtomicInteger(0);

    // TODO: declare a ThreadLocal<DecimalFormat> that:
    //  - initialises with new DecimalFormat("0.00")
    //  - increments instanceCount each time a new instance is created
    private final ThreadLocal<DecimalFormat> formatter = ThreadLocal.withInitial(() -> {
        instanceCount.incrementAndGet();
        return new DecimalFormat("0.00"); // placeholder — move logic into your implementation
    });

    /**
     * Formats a double to 2 decimal places using the current thread's DecimalFormat.
     * Must be thread-safe without any synchronized blocks.
     *
     * @param value the number to format
     * @return formatted string, e.g. "3.14"
     */
    public String format(double value) {
        // TODO: use the ThreadLocal formatter
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Returns how many distinct DecimalFormat instances were created.
     * Should equal the number of distinct threads that called format().
     */
    public int getFormatterInstanceCount() {
        return instanceCount.get();
    }
}
