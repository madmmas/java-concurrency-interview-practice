# Hints — Problem 44: Rate-Limited Executor

## Level 1 — Nudge

Lazy refill means you do not use a background thread to add tokens. Instead, when `tryAcquire()` is called, compute how many tokens should have been added since the last refill time, add them (up to capacity), update the timestamp, then try to acquire.

---

## Level 2 — Direction

**`TokenBucketRateLimiter` — lazy refill**:
```java
private long lastRefillTime = System.currentTimeMillis();
private double tokens;  // can be fractional

private synchronized void refill() {
    long now = System.currentTimeMillis();
    double tokensToAdd = (now - lastRefillTime) * ratePerMs;
    tokens = Math.min(capacity, tokens + tokensToAdd);
    lastRefillTime = now;
}

public synchronized boolean tryAcquire() {
    refill();
    if (tokens >= 1.0) { tokens -= 1.0; return true; }
    return false;
}
```

**`tryAcquire(long timeoutMs)`**: spin with a small sleep between attempts, checking `System.currentTimeMillis()` for the timeout.

**`RateLimitedExecutor`**: wrap the thread pool; before submitting, acquire a permit from the `TokenBucketRateLimiter`; if rate-limited, block (spin or `Thread.sleep`) until a token is available.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Rate limit not enforced over time | Refill gives all tokens at once instead of proportional to elapsed time |
| `tokensRefillOverTime` test fails | `ratePerMs` computed incorrectly — `maxTokensPerSecond / 1000.0` |
| `rateLimitIsEnforced` timing test flaky | Sleep granularity on Windows is ~15ms — tests may need wider tolerance |
