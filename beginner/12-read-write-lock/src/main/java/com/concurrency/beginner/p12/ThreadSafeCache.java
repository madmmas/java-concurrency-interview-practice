package com.concurrency.beginner.p12;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Problem 12 – ReadWriteLock: Thread-Safe Cache
 *
 * Use ReentrantReadWriteLock to allow concurrent reads but exclusive writes.
 * Do NOT use synchronized or ReentrantLock.
 */
public class ThreadSafeCache<K, V> {

    private final Map<K, V> store = new HashMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final AtomicLong readCount  = new AtomicLong(0);
    private final AtomicLong writeCount = new AtomicLong(0);

    /**
     * Stores a key-value mapping.
     * Requires the write lock.
     */
    public void put(K key, V value) {
        // TODO: writeLock → writeCount++ → store.put → unlock(finally)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Returns the value for key, or null if absent.
     * Requires only the read lock.
     */
    public V get(K key) {
        // TODO: readLock → readCount++ → return store.get → unlock(finally)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Removes the mapping for key.
     * Requires the write lock.
     */
    public void remove(K key) {
        // TODO: writeLock → writeCount++ → store.remove → unlock(finally)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Returns whether the cache contains the given key.
     * Requires only the read lock.
     */
    public boolean containsKey(K key) {
        // TODO: readLock → readCount++ → return store.containsKey → unlock(finally)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Returns the number of entries in the cache.
     * Requires only the read lock.
     */
    public int size() {
        // TODO: readLock → readCount++ → return store.size() → unlock(finally)
        throw new UnsupportedOperationException("Implement this method");
    }

    public long getReadCount()  { return readCount.get(); }
    public long getWriteCount() { return writeCount.get(); }
}
