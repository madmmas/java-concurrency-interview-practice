package com.concurrency.beginner.p09;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class LockFreeMetricsTest {

    private LockFreeMetrics metrics;

    @BeforeEach void setUp() { metrics = new LockFreeMetrics(); }

    @Test
    void singleRecordingUpdatesAllFields() {
        metrics.recordLatency(42);
        assertEquals(1,  metrics.getTotalCount());
        assertEquals(42, metrics.getTotalLatency());
        assertEquals(42, metrics.getMinLatency());
        assertEquals(42, metrics.getMaxLatency());
    }

    @Test
    void multipleRecordingsAggregateCorrectly() {
        metrics.recordLatency(10);
        metrics.recordLatency(50);
        metrics.recordLatency(30);
        assertEquals(3,  metrics.getTotalCount());
        assertEquals(90, metrics.getTotalLatency());
        assertEquals(10, metrics.getMinLatency());
        assertEquals(50, metrics.getMaxLatency());
    }

    @Test
    void defaultMinMaxWhenNoRecordings() {
        assertEquals(Integer.MAX_VALUE, metrics.getMinLatency());
        assertEquals(Integer.MIN_VALUE, metrics.getMaxLatency());
        assertEquals(0, metrics.getTotalCount());
    }

    @Test
    void resetClearsAll() {
        metrics.recordLatency(100);
        metrics.reset();
        assertEquals(0, metrics.getTotalCount());
        assertEquals(0, metrics.getTotalLatency());
        assertEquals(Integer.MAX_VALUE, metrics.getMinLatency());
        assertEquals(Integer.MIN_VALUE, metrics.getMaxLatency());
    }

    @Test
    void concurrentRecordingsAreAccurate() throws InterruptedException {
        int threads = 10, recordsEach = 1000;
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            workers.add(new Thread(() -> {
                for (int j = 1; j <= recordsEach; j++) metrics.recordLatency(j);
            }));
        }
        workers.forEach(Thread::start);
        for (Thread t : workers) t.join();

        assertEquals((long) threads * recordsEach, metrics.getTotalCount(),
                "Total count must reflect all concurrent recordings");
        // Each thread records 1..1000, sum per thread = 500500
        assertEquals(500_500L * threads, metrics.getTotalLatency(),
                "Total latency must be exact even under concurrent writes");
        assertEquals(1,    metrics.getMinLatency());
        assertEquals(1000, metrics.getMaxLatency());
    }
}
