package com.concurrency.intermediate.p22;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class DoubleBufferedLoggerTest {

    private DoubleBufferedLogger logger;

    @BeforeEach void setUp() {
        logger = new DoubleBufferedLogger(5);
        logger.start();
    }

    @AfterEach void tearDown() throws InterruptedException { logger.stop(); }

    @Test
    void fullBufferIsFlushed() throws InterruptedException {
        for (int i = 0; i < 5; i++) logger.log("msg-" + i);
        Thread.sleep(200);
        List<String> flushed = logger.getFlushedMessages();
        assertEquals(5, flushed.size(), "A full buffer must trigger a flush");
    }

    @Test
    void multipleBuffersAreFlushed() throws InterruptedException {
        int total = 25; // 5 full buffers
        for (int i = 0; i < total; i++) logger.log("m" + i);
        Thread.sleep(300);
        assertEquals(total, logger.getFlushedMessages().size(),
                "All " + total + " messages must be flushed");
    }

    @Test
    void stopFlushesPartialBuffer() throws InterruptedException {
        // Log 3 messages (buffer size 5 → not a full buffer)
        logger.log("a");
        logger.log("b");
        logger.log("c");
        logger.stop(); // must flush the partial buffer
        // Re-init for AfterEach to work cleanly
        logger = new DoubleBufferedLogger(5); logger.start();
        // The stopped logger should have flushed a, b, c
        // (we can't easily re-check the stopped logger here — see next test)
    }

    @Test
    void stopFlushesPartialBufferMessages() throws InterruptedException {
        DoubleBufferedLogger l = new DoubleBufferedLogger(10);
        l.start();
        l.log("x");
        l.log("y");
        l.stop();
        List<String> flushed = l.getFlushedMessages();
        assertTrue(flushed.containsAll(List.of("x", "y")),
                "stop() must flush partial buffer; got: " + flushed);
    }

    @Test
    void messagesAreNotLostUnderConcurrentLogging() throws InterruptedException {
        DoubleBufferedLogger l = new DoubleBufferedLogger(10);
        l.start();
        int threads = 5, msgsEach = 20;
        java.util.concurrent.CountDownLatch done = new java.util.concurrent.CountDownLatch(threads);
        for (int t = 0; t < threads; t++) {
            final int id = t;
            new Thread(() -> {
                try {
                    for (int i = 0; i < msgsEach; i++) l.log("t" + id + "-m" + i);
                } catch (InterruptedException ignored) {}
                finally { done.countDown(); }
            }).start();
        }
        done.await();
        l.stop();
        assertEquals(threads * msgsEach, l.getFlushedMessages().size(),
                "No messages must be lost under concurrent logging");
    }
}
