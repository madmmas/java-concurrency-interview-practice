package com.concurrency.beginner.p13;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 13 – CountDownLatch: Race Start Gun
 *
 * Uses a latch initialised to 1 as a "starting gun".
 * All racer threads block on await(); fire() releases them all at once.
 */
public class RaceStartGun {

    // Latch for releasing all racers simultaneously (count = 1)
    private final CountDownLatch startGun = new CountDownLatch(1);
    private final AtomicInteger waitingCount = new AtomicInteger(0);

    /**
     * Called by each racer thread. Blocks until fire() is called.
     * Increments waitingCount before blocking, decrements after released.
     */
    public void register() throws InterruptedException {
        // TODO: increment waitingCount, await startGun, decrement waitingCount
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Releases all waiting racers simultaneously. One-shot.
     */
    public void fire() {
        // TODO: startGun.countDown()
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the number of threads currently blocked in register(). */
    public int getWaitingCount() {
        return waitingCount.get();
    }
}
