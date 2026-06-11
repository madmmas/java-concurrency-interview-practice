package com.concurrency.intermediate.p21;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 21 – BlockingQueue: Two-Stage Processing Pipeline
 *
 * Items flow through two transformation stages, each backed by a
 * LinkedBlockingQueue and served by stageWorkers threads.
 *
 * Stage 1: item.toUpperCase()
 * Stage 2: item + "!"
 *
 * Shutdown uses the poison-pill pattern.
 */
public class WorkStealingPipeline {

    // Sentinel value — a worker that reads this must forward it and stop
    static final String POISON = "__POISON__";

    private final int stageWorkers;
    private final LinkedBlockingQueue<String> stage1Queue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<String> stage2Queue = new LinkedBlockingQueue<>();

    private final AtomicInteger processedCount = new AtomicInteger(0);
    private final List<Thread> allWorkers = new ArrayList<>();

    public WorkStealingPipeline(int stageWorkers) {
        this.stageWorkers = stageWorkers;
    }

    /**
     * Starts stageWorkers threads for stage 1 and stageWorkers threads for stage 2.
     *
     * Stage-1 worker loop:
     *   item = stage1Queue.take()
     *   if POISON → forward POISON to stage2Queue; stop
     *   stage2Queue.put(item.toUpperCase())
     *
     * Stage-2 worker loop:
     *   item = stage2Queue.take()
     *   if POISON → forward POISON to stage1Queue (so other s1 workers keep draining)
     *              Actually just stop — stage1 workers are already stopped by now.
     *              Increment processedCount and stop.
     *   processedCount.incrementAndGet(); (count the sink arrival)
     *   // (In a real pipeline you'd hand off to a sink here)
     */
    public void start() {
        // TODO: create and start stageWorkers stage-1 threads and stageWorkers stage-2 threads
        //       add all to allWorkers list
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Enqueues an item for stage-1 processing.
     */
    public void submit(String item) throws InterruptedException {
        // TODO: stage1Queue.put(item)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Gracefully shuts down the pipeline:
     * 1. Put stageWorkers POISON pills into stage1Queue
     * 2. Join all worker threads
     */
    public void shutdown() throws InterruptedException {
        // TODO: enqueue stageWorkers POISON pills into stage1Queue,
        //       then join all threads in allWorkers
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the number of items that completed both stages. */
    public int getProcessedCount() {
        return processedCount.get();
    }
}
