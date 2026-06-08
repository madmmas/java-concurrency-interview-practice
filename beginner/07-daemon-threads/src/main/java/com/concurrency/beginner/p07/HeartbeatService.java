package com.concurrency.beginner.p07;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Problem 07 – Daemon Threads: Heartbeat Service
 *
 * A background service that fires a beat action on a fixed interval.
 * The internal thread MUST be a daemon thread.
 */
public class HeartbeatService {

    private final long intervalMs;
    private final Runnable beatAction;
    private final AtomicLong beatCount = new AtomicLong(0);

    private volatile boolean running = false;
    private Thread heartbeatThread;

    /**
     * @param intervalMs milliseconds between beats
     * @param beatAction called once per beat interval
     */
    public HeartbeatService(long intervalMs, Runnable beatAction) {
        this.intervalMs = intervalMs;
        this.beatAction = beatAction;
    }

    /**
     * Starts the heartbeat daemon thread.
     * The thread must be marked as a daemon before starting.
     */
    public void start() {
        // TODO:
        //  1. Create a thread that loops: sleep intervalMs, invoke beatAction, increment beatCount
        //  2. Set it as a daemon thread
        //  3. Start it
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Signals the heartbeat thread to stop and waits for termination. */
    public void stop() throws InterruptedException {
        // TODO: set running = false, join the thread
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the number of times the beat action has been fired. */
    public long getBeatCount() {
        return beatCount.get();
    }

    /** Returns true if the internal thread is a daemon thread. */
    public boolean isDaemon() {
        return heartbeatThread != null && heartbeatThread.isDaemon();
    }
}
