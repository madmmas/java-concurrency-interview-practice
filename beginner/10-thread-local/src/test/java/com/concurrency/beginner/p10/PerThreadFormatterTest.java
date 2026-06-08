package com.concurrency.beginner.p10;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class PerThreadFormatterTest {

    @Test
    void formatsTwoDecimalPlaces() {
        PerThreadFormatter fmt = new PerThreadFormatter();
        assertEquals("3.14", fmt.format(3.14159));
        assertEquals("1.00", fmt.format(1.0));
        assertEquals("0.50", fmt.format(0.5));
    }

    @Test
    void sameThreadReusesInstance() {
        PerThreadFormatter fmt = new PerThreadFormatter();
        fmt.format(1.0);
        fmt.format(2.0);
        fmt.format(3.0);
        assertEquals(1, fmt.getFormatterInstanceCount(),
                "Same thread should reuse its formatter — only 1 instance created");
    }

    @Test
    void differentThreadsGetDifferentInstances() throws InterruptedException {
        PerThreadFormatter fmt = new PerThreadFormatter();
        int numThreads = 5;
        CountDownLatch done = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            new Thread(() -> {
                fmt.format(Math.PI);
                done.countDown();
            }).start();
        }
        done.await();
        assertEquals(numThreads, fmt.getFormatterInstanceCount(),
                "Each of the " + numThreads + " threads should create exactly one formatter instance");
    }

    @Test
    void concurrentFormattingIsCorrect() throws InterruptedException {
        PerThreadFormatter fmt = new PerThreadFormatter();
        CopyOnWriteArrayList<String> results = new CopyOnWriteArrayList<>();
        int threads = 10;
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            final double val = i + 0.5;
            new Thread(() -> {
                results.add(fmt.format(val));
                done.countDown();
            }).start();
        }
        done.await();

        assertEquals(threads, results.size());
        // Verify none are garbled (each should match pattern \d+\.\d{2})
        for (String r : results) {
            assertTrue(r.matches("\\d+\\.\\d{2}"),
                    "Result '" + r + "' does not match expected decimal format");
        }
    }
}
