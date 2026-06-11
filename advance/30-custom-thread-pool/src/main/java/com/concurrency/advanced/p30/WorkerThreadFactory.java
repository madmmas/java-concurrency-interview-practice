package com.concurrency.advanced.p30;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 30 – Custom ThreadPool: Named Worker Thread Factory
 *
 * Creates threads named "pool-{poolName}-worker-{N}" where N is monotonically
 * increasing from 1. Supports configurable daemon status, creation counting,
 * and uncaught exception recording.
 */
public class WorkerThreadFactory implements ThreadFactory {

    private final String poolName;
    private final boolean daemon;

    /** Monotonically increasing thread number; starts at 0, first thread gets 1. */
    private final AtomicInteger threadNumber = new AtomicInteger(0);

    /** All uncaught exceptions thrown by threads created by this factory. */
    private final List<Throwable> uncaughtExceptions =
            Collections.synchronizedList(new ArrayList<>());

    /**
     * @param poolName unique name embedded in every thread name
     * @param daemon   true → threads are daemon threads; false → user threads
     */
    public WorkerThreadFactory(String poolName, boolean daemon) {
        this.poolName = poolName;
        this.daemon   = daemon;
    }

    /**
     * Creates a new worker thread:
     *  - Name:   "pool-{poolName}-worker-{N}"  (N = threadNumber.incrementAndGet())
     *  - Daemon: as configured
     *  - UncaughtExceptionHandler: appends the Throwable to uncaughtExceptions
     */
    @Override
    public Thread newThread(Runnable r) {
        // TODO:
        //   int n = threadNumber.incrementAndGet();
        //   Thread t = new Thread(r, "pool-" + poolName + "-worker-" + n);
        //   t.setDaemon(daemon);
        //   t.setUncaughtExceptionHandler((thread, ex) -> uncaughtExceptions.add(ex));
        //   return t;
        throw new UnsupportedOperationException("Implement newThread()");
    }

    /** Returns the total number of threads created by this factory so far. */
    public int getCreatedCount() {
        return threadNumber.get();
    }

    /**
     * Returns an unmodifiable snapshot of all uncaught exceptions thrown by
     * threads created by this factory.
     */
    public List<Throwable> getUncaughtExceptions() {
        return Collections.unmodifiableList(new ArrayList<>(uncaughtExceptions));
    }
}
