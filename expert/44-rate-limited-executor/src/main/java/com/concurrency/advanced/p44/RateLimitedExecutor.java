package com.concurrency.advanced.p44;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
/**
 * Problem 44 – Rate-Limited Executor (Token Bucket, lazy refill)
 *
 * TokenBucketRateLimiter: tokens start at burstCapacity; refill lazily on each acquire().
 *   refill(): elapsed=(now-lastRefillNanos)/1e9; tokens=min(burst,tokens+elapsed*rate); update lastRefill
 *   TODO acquire():          loop: refill(); if tokens>=1 → tokens--; return; else sleep(waitNanos)
 *   TODO tryAcquire():       refill(); if tokens>=1 → tokens--; return true; else false
 *   TODO tryAcquire(ms):     deadline loop; refill(); if tokens>=1 → consume; return true; else sleep; check deadline
 *
 * RateLimitedExecutor:
 *   TODO execute(task):  try{limiter.acquire(); executor.execute(task);}catch(IE){rejectedCount++; restore interrupt}
 *   TODO submit(task):   same but return executor.submit(task) or failedFuture on interrupt
 *   TODO shutdown():     executor.shutdown(); awaitTermination(5s)
 */
public class RateLimitedExecutor {
    private final TokenBucketRateLimiter limiter;
    private final ExecutorService executor;
    private final AtomicLong rejectedCount = new AtomicLong(0);
    public RateLimitedExecutor(int threads, double rate, double burst) {
        this.limiter=new TokenBucketRateLimiter(rate,burst);
        this.executor=Executors.newFixedThreadPool(threads);
    }
    public void execute(Runnable task) { throw new UnsupportedOperationException("Implement execute()"); }
    public Future<?> submit(Runnable task) { throw new UnsupportedOperationException("Implement submit()"); }
    public long getRejectedCount() { return rejectedCount.get(); }
    public void shutdown() throws InterruptedException { throw new UnsupportedOperationException("Implement shutdown()"); }

    public static class TokenBucketRateLimiter {
        private final double rate, burst;
        private double tokens;
        private long lastNanos;
        public TokenBucketRateLimiter(double rate, double burst) {
            this.rate=rate; this.burst=burst; this.tokens=burst; this.lastNanos=System.nanoTime();
        }
        public synchronized void    acquire() throws InterruptedException { throw new UnsupportedOperationException("Implement acquire()"); }
        public synchronized boolean tryAcquire()                          { throw new UnsupportedOperationException("Implement tryAcquire()"); }
        public synchronized boolean tryAcquire(long ms) throws InterruptedException { throw new UnsupportedOperationException("Implement tryAcquire(long)"); }
        public synchronized double  getAvailableTokens()                  { throw new UnsupportedOperationException("Implement getAvailableTokens()"); }
        private void refill() {
            long now=System.nanoTime(); tokens=Math.min(burst,tokens+(now-lastNanos)/1e9*rate); lastNanos=now;
        }
    }
}
