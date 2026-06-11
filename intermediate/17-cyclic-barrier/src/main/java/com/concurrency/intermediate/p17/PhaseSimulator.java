package com.concurrency.intermediate.p17;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 17 – CyclicBarrier: Multi-Phase Simulator
 *
 * Runs `workers` threads through `phases` rounds of work.
 * All threads must complete one phase before any proceeds to the next.
 * The CyclicBarrier resets automatically between phases.
 */
public class PhaseSimulator {

    private final int workers;
    private final int phases;
    private final AtomicInteger completedPhases = new AtomicInteger(0);
    private CountDownLatch allDone;
    private CyclicBarrier barrier;

    public PhaseSimulator(int workers, int phases) {
        this.workers = workers;
        this.phases  = phases;
    }

    /**
     * Starts all worker threads. Each worker:
     *  1. Calls phaseWork.run()
     *  2. Calls barrier.await()   ← all workers synchronise here
     *  3. Repeats for `phases` total phases, then exits
     *
     * The barrier's Runnable action increments completedPhases.
     * Sets up allDone latch so awaitCompletion() can block.
     */
    public void run(Runnable phaseWork) {
        // TODO:
        //  1. allDone = new CountDownLatch(workers)
        //  2. barrier = new CyclicBarrier(workers, () -> completedPhases.incrementAndGet())
        //  3. Start `workers` threads, each looping phases times:
        //       for (int p = 0; p < phases; p++) { phaseWork.run(); barrier.await(); }
        //       then allDone.countDown()
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Blocks until all workers have completed all phases.
     */
    public void awaitCompletion() throws InterruptedException {
        // TODO: allDone.await()
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the number of barrier trips completed so far. */
    public int getCompletedPhases() {
        return completedPhases.get();
    }
}
