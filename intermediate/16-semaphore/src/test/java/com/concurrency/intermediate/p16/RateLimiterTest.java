package com.concurrency.intermediate.p16;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class RateLimiterTest {

    private RateLimiter limiter;

    @BeforeEach
    void setUp() {
        limiter = new RateLimiter(5, 500); // 5 requests per 500 ms window
        limiter.start();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        limiter.stop();
    }

    @Test
    void permitsUpToMaxAreGrantedImmediately() {
        int granted = 0;
        for (int i = 0; i < 5; i++) {
            if (limiter.tryAcquire()) granted++;
        }
        assertEquals(5, granted, "All 5 permits should be granted immediately");
    }

    @Test
    void excessRequestsAreRejected() {
        for (int i = 0; i < 5; i++) limiter.tryAcquire(); // exhaust permits
        assertFalse(limiter.tryAcquire(), "6th request must be rejected when rate limit is exceeded");
    }

    @Test
    void permitsAreRefreshedAfterWindow() throws InterruptedException {
        for (int i = 0; i < 5; i++) limiter.tryAcquire(); // exhaust
        assertEquals(0, limiter.availablePermits());

        Thread.sleep(700); // wait past the 500 ms window
        assertTrue(limiter.availablePermits() > 0,
                "Permits should be refilled after the window expires");
    }

    @Test
    void acquireBlocksUntilPermitAvailable() throws InterruptedException {
        for (int i = 0; i < 5; i++) limiter.tryAcquire(); // exhaust

        AtomicInteger acquired = new AtomicInteger(0);
        Thread waiter = new Thread(() -> {
            try { limiter.acquire(); acquired.incrementAndGet(); }
            catch (InterruptedException ignored) {}
        });
        waiter.start();
        Thread.sleep(100);
        assertEquals(0, acquired.get(), "acquire() must block when no permits are available");

        Thread.sleep(600); // refill happens
        waiter.join(2000);
        assertEquals(1, acquired.get(), "acquire() should unblock after refill");
    }
}
