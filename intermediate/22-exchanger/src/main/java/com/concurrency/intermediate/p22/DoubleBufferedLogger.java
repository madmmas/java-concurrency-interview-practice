package com.concurrency.intermediate.p22;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Problem 22 – Exchanger: Double-Buffered Logger
 *
 * Uses an Exchanger<List<String>> so a producer (callers of log()) fills one
 * buffer while a background consumer flushes the other — then they swap.
 *
 * This pattern eliminates contention: the fill thread never waits for I/O and
 * the flush thread never waits for new data (only at the exchange point).
 */
public class DoubleBufferedLogger {

    private final int bufferSize;
    private final Exchanger<List<String>> exchanger = new Exchanger<>();

    // The buffer currently being filled by log() callers
    private List<String> fillBuffer = new ArrayList<>();

    // All messages that have been flushed (sink for this exercise)
    private final List<String> flushedMessages = new ArrayList<>();

    private volatile boolean running = false;
    private Thread flushThread;

    public DoubleBufferedLogger(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * Appends message to the fill buffer.
     * When the fill buffer reaches bufferSize, exchange it with the flush buffer.
     *
     * Must be thread-safe: use synchronized on (this) for buffer access.
     */
    public void log(String message) throws InterruptedException {
        List<String> toExchange = null;
        synchronized (this) {
            fillBuffer.add(message);
            if (fillBuffer.size() >= bufferSize) {
                toExchange = fillBuffer;
                fillBuffer = new ArrayList<>();
            }
        }
        if (toExchange != null) {
            // TODO: fillBuffer = exchanger.exchange(toExchange)
            //       (receive the empty buffer back from the flush thread)
            throw new UnsupportedOperationException("Implement exchange");
        }
    }

    /**
     * Starts the background daemon flush thread.
     *
     * Flush thread loop:
     *   List<String> full = exchanger.exchange(new ArrayList<>())  // give empty, get full
     *   synchronized(this) { flushedMessages.addAll(full) }
     *   (repeat until stopped)
     */
    public void start() {
        // TODO: create daemon flush thread and start it
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Stops the flush thread:
     * 1. Set running = false
     * 2. Exchange the current (possibly partial) fillBuffer to flush remaining messages
     * 3. Join the flush thread
     */
    public void stop() throws InterruptedException {
        // TODO: signal stop, force-exchange the partial fillBuffer, join thread
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns a snapshot of all flushed messages. */
    public synchronized List<String> getFlushedMessages() {
        return new ArrayList<>(flushedMessages);
    }
}
