package com.concurrency.advanced.p44;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=15, unit=TimeUnit.SECONDS)
class RateLimitedExecutorTest {
    @Test void tryAcquireSucceedsWithTokens() {
        var l = new RateLimitedExecutor.TokenBucketRateLimiter(10,5);
        assertTrue(l.tryAcquire());
    }
    @Test void tryAcquireFailsWhenExhausted() {
        var l = new RateLimitedExecutor.TokenBucketRateLimiter(1,2);
        l.tryAcquire(); l.tryAcquire();
        assertFalse(l.tryAcquire());
    }
    @Test void tokensRefillOverTime() throws Exception {
        var l = new RateLimitedExecutor.TokenBucketRateLimiter(10,10);
        for(int i=0;i<10;i++) l.tryAcquire();
        Thread.sleep(300);
        assertTrue(l.getAvailableTokens()>=2);
    }
    @Test void timedAcquireSucceeds() throws Exception {
        var l = new RateLimitedExecutor.TokenBucketRateLimiter(10,1);
        l.tryAcquire();
        assertTrue(l.tryAcquire(500));
    }
    @Test void timedAcquireFailsWhenTooShort() throws Exception {
        var l = new RateLimitedExecutor.TokenBucketRateLimiter(1,1);
        l.tryAcquire();
        assertFalse(l.tryAcquire(50));
    }
    @Test void allTasksExecute() throws Exception {
        var exec = new RateLimitedExecutor(2,200,20);
        var ran = new AtomicInteger(0);
        for(int i=0;i<10;i++) exec.execute(ran::incrementAndGet);
        exec.shutdown(); assertEquals(10,ran.get());
    }
    @Test void rateLimitIsEnforced() throws Exception {
        var exec = new RateLimitedExecutor(2,5,1);
        long t=System.currentTimeMillis();
        for(int i=0;i<5;i++) exec.execute(()->{});
        exec.shutdown();
        assertTrue(System.currentTimeMillis()-t>=700, "5 tasks at 5/s with burst=1 must take ≥700ms");
    }
}
