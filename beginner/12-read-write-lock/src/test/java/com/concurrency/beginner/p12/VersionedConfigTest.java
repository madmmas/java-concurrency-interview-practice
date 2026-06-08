package com.concurrency.beginner.p12;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class VersionedConfigTest {

    @Test
    void updateConfigReturnsWrittenValue() {
        VersionedConfig cfg = new VersionedConfig();
        String result = cfg.updateConfig("host", "localhost");
        assertEquals("localhost", result,
                "updateConfig() should return the value just written");
    }

    @Test
    void getConfigReadsCorrectValue() {
        VersionedConfig cfg = new VersionedConfig();
        cfg.updateConfig("port", "8080");
        assertEquals("8080", cfg.getConfig("port"));
    }

    @Test
    void getAllKeysReturnsSnapshot() {
        VersionedConfig cfg = new VersionedConfig();
        cfg.updateConfig("a", "1");
        cfg.updateConfig("b", "2");
        Set<String> keys = cfg.getAllKeys();
        assertTrue(keys.containsAll(Set.of("a", "b")));
        assertEquals(2, keys.size());
    }

    @Test
    void lockDowngradeAllowsConcurrentReadsAfterWrite() throws InterruptedException {
        // During updateConfig the write lock is downgraded; other readers should
        // be able to proceed after the write lock is released.
        VersionedConfig cfg = new VersionedConfig();
        cfg.updateConfig("x", "initial");

        int readers = 10;
        AtomicInteger successes = new AtomicInteger();
        CountDownLatch done = new CountDownLatch(readers);
        for (int i = 0; i < readers; i++) {
            new Thread(() -> {
                try {
                    String v = cfg.getConfig("x");
                    if (v != null) successes.incrementAndGet();
                } finally { done.countDown(); }
            }).start();
        }
        done.await();
        assertEquals(readers, successes.get(),
                "All readers should succeed after write lock is downgraded/released");
    }

    @Test
    void concurrentUpdatesAreVisible() throws InterruptedException {
        VersionedConfig cfg = new VersionedConfig();
        int threads = 10;
        CountDownLatch done = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            final String key = "cfg-" + i;
            new Thread(() -> {
                cfg.updateConfig(key, "value");
                done.countDown();
            }).start();
        }
        done.await();
        assertEquals(threads, cfg.getAllKeys().size(),
                "All " + threads + " config entries should be present after concurrent updates");
    }
}
