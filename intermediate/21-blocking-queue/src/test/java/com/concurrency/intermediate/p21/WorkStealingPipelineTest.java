package com.concurrency.intermediate.p21;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class WorkStealingPipelineTest {

    @Test
    void pipelineProcessesAllItems() throws InterruptedException {
        WorkStealingPipeline pipeline = new WorkStealingPipeline(2);
        pipeline.start();

        int items = 20;
        for (int i = 0; i < items; i++) pipeline.submit("item-" + i);
        pipeline.shutdown();

        assertEquals(items, pipeline.getProcessedCount(),
                "All submitted items must reach the sink");
    }

    @Test
    void singleWorkerSingleItem() throws InterruptedException {
        WorkStealingPipeline pipeline = new WorkStealingPipeline(1);
        pipeline.start();
        pipeline.submit("hello");
        pipeline.shutdown();
        assertEquals(1, pipeline.getProcessedCount());
    }

    @Test
    void shutdownDrainsQueueBeforeStopping() throws InterruptedException {
        WorkStealingPipeline pipeline = new WorkStealingPipeline(2);
        pipeline.start();
        int items = 100;
        for (int i = 0; i < items; i++) pipeline.submit("x" + i);
        pipeline.shutdown(); // must wait for all items to flow through
        assertEquals(items, pipeline.getProcessedCount(),
                "shutdown() must drain all in-flight items before returning");
    }

    @Test
    void multipleWorkersHandleBurst() throws InterruptedException {
        WorkStealingPipeline pipeline = new WorkStealingPipeline(4);
        pipeline.start();
        int items = 200;
        for (int i = 0; i < items; i++) pipeline.submit("burst-" + i);
        pipeline.shutdown();
        assertEquals(items, pipeline.getProcessedCount());
    }
}
