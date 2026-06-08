package com.concurrency.beginner.p08;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class GracefulSleeperTest {

    @Test
    void sleepCompletesNormallyWithoutInterruption() throws InterruptedException {
        GracefulSleeper sleeper = new GracefulSleeper();
        sleeper.sleepFor(50);
        sleeper.join();

        assertTrue(sleeper.didCompleteNormally(), "Sleep should complete normally");
        assertFalse(sleeper.didGetInterrupted(), "Should not report interrupted");
    }

    @Test
    void interruptionIsDetected() throws InterruptedException {
        GracefulSleeper sleeper = new GracefulSleeper();
        sleeper.sleepFor(10_000); // very long sleep
        Thread.sleep(30);
        sleeper.interrupt();

        assertTrue(sleeper.didGetInterrupted(), "Should detect the interruption");
        assertFalse(sleeper.didCompleteNormally(), "Should not report normal completion");
    }

    @Test
    void interruptReturnsQuickly() throws InterruptedException {
        GracefulSleeper sleeper = new GracefulSleeper();
        sleeper.sleepFor(30_000);
        Thread.sleep(20);

        long start = System.currentTimeMillis();
        sleeper.interrupt();
        long elapsed = System.currentTimeMillis() - start;

        assertTrue(elapsed < 2000,
                "interrupt() should return promptly after waking the thread (elapsed: " + elapsed + " ms)");
    }

    @Test
    void normalAndInterruptedAreExclusive() throws InterruptedException {
        // Sleeping naturally — only one flag should be true
        GracefulSleeper s1 = new GracefulSleeper();
        s1.sleepFor(30);
        s1.join();
        assertNotEquals(s1.didCompleteNormally(), s1.didGetInterrupted(),
                "Exactly one of completedNormally / gotInterrupted should be true");

        // Interrupted — opposite should hold
        GracefulSleeper s2 = new GracefulSleeper();
        s2.sleepFor(30_000);
        Thread.sleep(20);
        s2.interrupt();
        assertNotEquals(s2.didCompleteNormally(), s2.didGetInterrupted(),
                "Exactly one of completedNormally / gotInterrupted should be true");
    }
}
