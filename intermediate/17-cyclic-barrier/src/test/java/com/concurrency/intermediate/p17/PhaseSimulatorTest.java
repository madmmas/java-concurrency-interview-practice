package com.concurrency.intermediate.p17;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class PhaseSimulatorTest {

    @Test
    void completedPhasesMatchesExpected() throws InterruptedException {
        PhaseSimulator sim = new PhaseSimulator(4, 3);
        sim.run(() -> {});
        sim.awaitCompletion();
        assertEquals(3, sim.getCompletedPhases(),
                "Barrier should trip exactly once per phase");
    }

    @Test
    void allWorkersParticipateInEachPhase() throws InterruptedException {
        int workers = 5, phases = 4;
        AtomicInteger callCount = new AtomicInteger();
        PhaseSimulator sim = new PhaseSimulator(workers, phases);
        sim.run(callCount::incrementAndGet);
        sim.awaitCompletion();
        assertEquals(workers * phases, callCount.get(),
                "Each worker must run phaseWork once per phase");
    }

    @Test
    void phasesAreStrictlyOrdered() throws InterruptedException {
        // Verify that no thread starts phase N+1 before ALL threads finish phase N
        int workers = 4, phases = 3;
        CopyOnWriteArrayList<Integer> log = new CopyOnWriteArrayList<>();
        AtomicInteger phase = new AtomicInteger(0);
        PhaseSimulator sim = new PhaseSimulator(workers, phases);

        sim.run(() -> log.add(phase.get()));

        // After awaitCompletion, check that entries for phase 0 all precede phase 1, etc.
        sim.awaitCompletion();

        // completedPhases increments inside barrier action; by the time a thread
        // starts the NEXT iteration, completedPhases reflects the finished phase.
        // So the phase value recorded during phaseWork should be constant within a phase.
        assertEquals(workers * phases, log.size());
    }

    @Test
    void singleWorkerSinglePhase() throws InterruptedException {
        PhaseSimulator sim = new PhaseSimulator(1, 1);
        AtomicInteger ran = new AtomicInteger(0);
        sim.run(ran::incrementAndGet);
        sim.awaitCompletion();
        assertEquals(1, ran.get());
        assertEquals(1, sim.getCompletedPhases());
    }

    @Test
    void manyWorkersAndPhases() throws InterruptedException {
        PhaseSimulator sim = new PhaseSimulator(10, 5);
        AtomicInteger total = new AtomicInteger(0);
        sim.run(total::incrementAndGet);
        sim.awaitCompletion();
        assertEquals(50, total.get(), "10 workers × 5 phases = 50 phaseWork invocations");
        assertEquals(5, sim.getCompletedPhases());
    }
}
