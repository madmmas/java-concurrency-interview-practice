package com.concurrency.intermediate.p23;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

/**
 * Problem 23 – Phaser: Dynamic Worker Pool
 *
 * Workers can join and leave between phases. The Phaser terminates automatically
 * after `phases` rounds by overriding onAdvance().
 */
public class DynamicWorkerPool {

    private final int phases;
    private final List<Thread> workers = new ArrayList<>();

    /**
     * Phaser that auto-terminates after `phases` rounds.
     * Override onAdvance() to return true when phase >= phases - 1.
     */
    private final Phaser phaser;

    public DynamicWorkerPool(int initialWorkers, int phases) {
        this.phases = phases;
        // TODO: create Phaser(initialWorkers) with onAdvance override
        //       that returns true when phase >= phases - 1 || registeredParties == 0
        this.phaser = new Phaser(initialWorkers) {
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                throw new UnsupportedOperationException("Implement onAdvance");
            }
        };
    }

    /**
     * Starts initialWorkers threads. Each runs phaseWork then calls
     * arriveAndAwaitAdvance() for `phases` rounds, then exits.
     */
    public void start(Runnable phaseWork) {
        // TODO: for each registered party, create a thread that:
        //   while (!phaser.isTerminated()) {
        //       phaseWork.run();
        //       phaser.arriveAndAwaitAdvance();
        //   }
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Registers a new worker dynamically and starts it.
     * The new thread calls phaser.register() before beginning.
     * It participates starting from the current phase.
     *
     * @param phaseWork work to do each phase
     */
    public void addWorker(Runnable phaseWork) {
        // TODO: phaser.register() then start a thread with the same loop as start()
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Blocks the calling thread until the phaser terminates.
     */
    public void awaitCompletion() throws InterruptedException {
        // TODO: phaser.awaitAdvance(phaser.getPhase()) until isTerminated()
        //       OR: join all worker threads
        for (Thread t : workers) t.join();
    }

    /** Returns the number of phases the phaser has advanced through. */
    public int getCompletedPhases() {
        // After termination, getPhase() returns a negative number whose absolute value
        // is the phase at which termination occurred. Use Math.abs.
        return Math.abs(phaser.getPhase());
    }

    public boolean isTerminated() {
        return phaser.isTerminated();
    }
}
