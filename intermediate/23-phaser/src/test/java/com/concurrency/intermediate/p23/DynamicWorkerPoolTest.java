package com.concurrency.intermediate.p23;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class DynamicWorkerPoolTest {

    @Test
    void exactPhasesAreCompleted() throws InterruptedException {
        int workers = 3, phases = 4;
        AtomicInteger workCount = new AtomicInteger(0);
        DynamicWorkerPool pool = new DynamicWorkerPool(workers, phases);
        pool.start(workCount::incrementAndGet);
        pool.awaitCompletion();
        assertEquals(phases, pool.getCompletedPhases(),
                "Phaser must advance exactly `phases` times");
    }

    @Test
    void allWorkersRunEachPhase() throws InterruptedException {
        int workers = 4, phases = 3;
        AtomicInteger workCount = new AtomicInteger(0);
        DynamicWorkerPool pool = new DynamicWorkerPool(workers, phases);
        pool.start(workCount::incrementAndGet);
        pool.awaitCompletion();
        assertEquals(workers * phases, workCount.get(),
                "Each worker must run phaseWork once per phase");
    }

    @Test
    void phaserTerminatesAfterAllPhases() throws InterruptedException {
        DynamicWorkerPool pool = new DynamicWorkerPool(2, 3);
        pool.start(() -> {});
        pool.awaitCompletion();
        assertTrue(pool.isTerminated(), "Phaser must be terminated after all phases complete");
    }

    @Test
    void singleWorkerSinglePhase() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        DynamicWorkerPool pool = new DynamicWorkerPool(1, 1);
        pool.start(count::incrementAndGet);
        pool.awaitCompletion();
        assertEquals(1, count.get());
        assertEquals(1, pool.getCompletedPhases());
    }

    @Test
    void dynamicWorkerJoinsAndParticipates() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        int phases = 5;
        DynamicWorkerPool pool = new DynamicWorkerPool(2, phases);
        pool.start(count::incrementAndGet);
        // Add a third worker mid-way
        pool.addWorker(count::incrementAndGet);
        pool.awaitCompletion();
        // At minimum, original 2 workers each ran `phases` times = 2*phases
        assertTrue(count.get() >= 2 * phases,
                "At least original workers must complete all phases; count=" + count.get());
    }
}
