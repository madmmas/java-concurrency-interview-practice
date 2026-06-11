package com.concurrency.intermediate.p24;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;

/**
 * Problem 24 – StampedLock: Read-Optimized Cache
 *
 * Uses StampedLock to maximise read throughput with optimistic reads.
 * Tracks optimistic-hit vs read-lock-fallback counts for observability.
 */
public class StampedCache<K, V> {

    private final Map<K, V> map = new HashMap<>();
    private final StampedLock lock = new StampedLock();
    private final AtomicLong optimisticHits    = new AtomicLong(0);
    private final AtomicLong readLockFallbacks = new AtomicLong(0);

    /**
     * Returns the value for key using an optimistic read first.
     * Falls back to a read lock if validation fails.
     */
    public V get(K key) {
        // TODO:
        //  stamp = lock.tryOptimisticRead()
        //  V val = map.get(key)
        //  if (lock.validate(stamp)) { optimisticHits++; return val; }
        //  // fallback
        //  readLockFallbacks++
        //  stamp = lock.readLock()
        //  try { return map.get(key); } finally { lock.unlockRead(stamp); }
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Stores a key-value mapping under a write lock.
     */
    public void put(K key, V value) {
        // TODO: write lock → map.put → unlock in finally
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Returns the value for key, computing it if absent.
     *
     * Strategy (lock upgrade pattern):
     *  1. Acquire read lock
     *  2. If present, return it
     *  3. Attempt tryConvertToWriteLock(stamp)
     *  4. If successful, compute and put
     *  5. If conversion fails, release read lock, acquire write lock, compute and put
     *  6. Unlock in finally using lock.unlock(stamp) — handles both read and write stamps
     */
    public V computeIfAbsent(K key, Function<K, V> mappingFn) {
        // TODO: implement with read lock → tryConvertToWriteLock → fallback write lock
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns current map size under a read lock. */
    public int size() {
        long stamp = lock.readLock();
        try { return map.size(); }
        finally { lock.unlockRead(stamp); }
    }

    public long getOptimisticHits()    { return optimisticHits.get(); }
    public long getReadLockFallbacks() { return readLockFallbacks.get(); }
}
