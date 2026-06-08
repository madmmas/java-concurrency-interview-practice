package com.concurrency.beginner.p07;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class BackgroundLoggerTest {

    @Test
    void loggedMessagesAreCollected() throws InterruptedException {
        BackgroundLogger logger = new BackgroundLogger();
        logger.start();
        logger.log("event-A");
        logger.log("event-B");
        logger.log("event-C");
        List<String> messages = logger.stop();

        assertEquals(3, messages.size(), "All 3 logged messages should be collected");
        assertTrue(messages.containsAll(List.of("event-A", "event-B", "event-C")));
    }

    @Test
    void logDoesNotBlockCaller() throws InterruptedException {
        BackgroundLogger logger = new BackgroundLogger();
        logger.start();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) logger.log("msg-" + i);
        long elapsed = System.currentTimeMillis() - start;

        logger.stop();
        assertTrue(elapsed < 1000,
                "Logging 1000 messages should be nearly instantaneous (elapsed: " + elapsed + " ms)");
    }

    @Test
    void stopDrainsRemainingMessages() throws InterruptedException {
        BackgroundLogger logger = new BackgroundLogger();
        logger.start();
        for (int i = 0; i < 50; i++) logger.log("item-" + i);
        List<String> messages = logger.stop();

        assertEquals(50, messages.size(), "stop() should drain all remaining queued messages");
    }

    @Test
    void getLoggedMessagesIsSnapshot() throws InterruptedException {
        BackgroundLogger logger = new BackgroundLogger();
        logger.start();
        logger.log("first");
        Thread.sleep(100);
        List<String> snap1 = logger.getLoggedMessages();
        logger.log("second");
        Thread.sleep(100);
        List<String> snap2 = logger.getLoggedMessages();
        logger.stop();

        assertTrue(snap1.contains("first"));
        assertTrue(snap2.containsAll(List.of("first", "second")));
    }
}
