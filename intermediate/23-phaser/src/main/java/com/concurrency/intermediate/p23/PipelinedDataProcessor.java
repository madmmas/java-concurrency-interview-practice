package com.concurrency.intermediate.p23;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Phaser;

/**
 * Problem 23 – Phaser: 3-Stage Pipelined Data Processor
 *
 * Divides a list of strings across `workers` threads.
 * Each worker processes its chunk through 3 sequential stages.
 * All workers must complete stage N before any proceeds to stage N+1.
 *
 * Stage 1: trim()
 * Stage 2: toUpperCase()
 * Stage 3: append " ✓"
 */
public class PipelinedDataProcessor {

    private final int workers;

    public PipelinedDataProcessor(int workers) {
        this.workers = workers;
    }

    /**
     * Processes the given list through 3 stages using a Phaser.
     * Returns the fully-processed list in the original order.
     *
     * Implementation outline:
     *  1. Partition data into `workers` chunks (as evenly as possible)
     *  2. Create Phaser(1) — main thread is the first party
     *  3. Start worker threads; each calls phaser.register() then processes its chunk:
     *       Stage 1: chunk.replaceAll(String::trim); phaser.arriveAndAwaitAdvance()
     *       Stage 2: chunk.replaceAll(String::toUpperCase); phaser.arriveAndAwaitAdvance()
     *       Stage 3: chunk.replaceAll(s -> s + " ✓"); phaser.arriveAndDeregister()
     *  4. Main thread awaits all 3 phases: phaser.arriveAndAwaitAdvance() × 3
     *  5. Merge chunks back into result list (preserving order)
     */
    public List<String> process(List<String> data) throws InterruptedException {
        // TODO: implement the phased pipeline as described above
        throw new UnsupportedOperationException("Implement this method");
    }
}
