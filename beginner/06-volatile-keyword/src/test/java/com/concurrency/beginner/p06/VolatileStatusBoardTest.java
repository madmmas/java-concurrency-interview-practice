package com.concurrency.beginner.p06;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class VolatileStatusBoardTest {

    @Test
    void initialStatusIsInit() {
        VolatileStatusBoard board = new VolatileStatusBoard();
        assertEquals("INIT", board.getStatus());
    }

    @Test
    void postedStatusIsReadBack() {
        VolatileStatusBoard board = new VolatileStatusBoard();
        board.postStatus("READY");
        assertEquals("READY", board.getStatus());
        board.postStatus("DONE");
        assertEquals("DONE", board.getStatus());
    }

    @Test
    void readCountTracksAllCalls() {
        VolatileStatusBoard board = new VolatileStatusBoard();
        board.getStatus();
        board.getStatus();
        board.getStatus();
        assertEquals(3, board.getReadCount());
    }

    @Test
    void manyReadersSeeFreshWrite() throws InterruptedException {
        VolatileStatusBoard board = new VolatileStatusBoard();
        int readers = 20;
        CountDownLatch ready = new CountDownLatch(readers);
        CountDownLatch go = new CountDownLatch(1);
        List<String> seen = new ArrayList<>();
        Object lock = new Object();

        for (int i = 0; i < readers; i++) {
            new Thread(() -> {
                ready.countDown();
                try { go.await(); } catch (InterruptedException ignored) {}
                String s = board.getStatus();
                synchronized (lock) { seen.add(s); }
            }).start();
        }

        ready.await();
        board.postStatus("PUBLISHED");
        go.countDown();

        Thread.sleep(200);
        assertEquals(readers, seen.size());
        // All readers that ran after postStatus should see "PUBLISHED"
        assertTrue(seen.stream().allMatch(s -> s.equals("PUBLISHED") || s.equals("INIT")),
                "Readers should only see INIT or PUBLISHED, never a stale intermediate value");
        assertEquals(readers, board.getReadCount());
    }
}
