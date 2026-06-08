package com.concurrency.beginner.p07;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Problem 07 – Daemon Threads: Background Logger
 *
 * A non-blocking logger that drains messages on a background daemon thread.
 */
public class BackgroundLogger {

    private final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final List<String> logged = new ArrayList<>();
    private volatile boolean running = false;
    private Thread loggerThread;

    /**
     * Starts the background daemon thread that drains the queue and records messages.
     * Use queue.poll(100, TimeUnit.MILLISECONDS) so the thread can periodically
     * check the stop flag even when the queue is empty.
     */
    public void start() {
        // TODO: create daemon thread, start it
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Enqueues a message for background logging. Must not block the caller.
     */
    public void log(String message) {
        // TODO: add to queue (offer is fine)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Signals the logger to stop, waits for it to finish, and returns all logged messages.
     */
    public List<String> stop() throws InterruptedException {
        // TODO: signal stop, join thread, return logged list
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns messages logged so far (snapshot). */
    public synchronized List<String> getLoggedMessages() {
        return new ArrayList<>(logged);
    }
}
