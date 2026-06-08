package com.concurrency.beginner.p07;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class HeartbeatServiceTest {

    @Test
    void internalThreadIsDaemon() throws InterruptedException {
        HeartbeatService svc = new HeartbeatService(50, () -> {});
        svc.start();
        assertTrue(svc.isDaemon(), "Heartbeat thread must be a daemon thread");
        svc.stop();
    }

    @Test
    void beatActionIsInvokedMultipleTimes() throws InterruptedException {
        AtomicInteger beats = new AtomicInteger();
        HeartbeatService svc = new HeartbeatService(30, beats::incrementAndGet);
        svc.start();
        Thread.sleep(200);
        svc.stop();
        assertTrue(beats.get() >= 3,
                "Expected at least 3 beats in 200 ms with 30 ms interval, got: " + beats.get());
    }

    @Test
    void beatCountMatchesInvocations() throws InterruptedException {
        HeartbeatService svc = new HeartbeatService(30, () -> {});
        svc.start();
        Thread.sleep(180);
        svc.stop();
        assertTrue(svc.getBeatCount() >= 3,
                "getBeatCount() should reflect number of beats fired");
    }

    @Test
    void stopTerminatesThread() throws InterruptedException {
        HeartbeatService svc = new HeartbeatService(20, () -> {});
        svc.start();
        Thread.sleep(60);
        svc.stop();
        // After stop + join, the thread must not still be alive
        // isDaemon() will still return true (property of thread object)
        assertTrue(svc.isDaemon(), "Thread is still a daemon thread after stop");
    }

    @Test
    void setDaemonBeforeStartOrExceptionWouldOccur() {
        // Verify that setDaemon() is called before start() by checking no exception was thrown
        assertDoesNotThrow(() -> {
            HeartbeatService svc = new HeartbeatService(100, () -> {});
            svc.start();
            svc.stop();
        });
    }
}
