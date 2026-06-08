package com.concurrency.beginner.p06;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class StopFlagTest {

    @Test
    void workerStartsAndCanBeStopped() throws InterruptedException {
        StopFlag flag = new StopFlag();
        flag.start();
        assertTrue(flag.isRunning(), "Worker should be running after start()");

        Thread.sleep(50);
        flag.stop();

        assertFalse(flag.isRunning(), "Worker should have stopped after stop()");
    }

    @Test
    void workerAccumulatesCount() throws InterruptedException {
        StopFlag flag = new StopFlag();
        flag.start();
        Thread.sleep(100);
        flag.stop();

        assertTrue(flag.getCount() > 0, "Count should be > 0 after running for 100 ms");
    }

    @Test
    void stopIsVisiblePromptly() throws InterruptedException {
        StopFlag flag = new StopFlag();
        flag.start();
        Thread.sleep(30);

        long before = System.currentTimeMillis();
        flag.stop();
        long elapsed = System.currentTimeMillis() - before;

        assertTrue(elapsed < 2000,
                "stop() should cause the worker to exit quickly (elapsed: " + elapsed + " ms)");
    }

    @Test
    void canRestartAfterStop() throws InterruptedException {
        StopFlag flag = new StopFlag();
        flag.start();
        Thread.sleep(20);
        flag.stop();
        assertFalse(flag.isRunning());

        // Second lifecycle
        flag.start();
        assertTrue(flag.isRunning());
        flag.stop();
        assertFalse(flag.isRunning());
    }
}
