package com.concurrency.advanced.p39;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
/**
 * Problem 39 – Concurrent LRU Cache
 * Backed by LinkedHashMap(accessOrder=true) + ReentrantReadWriteLock.
 *
 * TODO get(key):              write lock (get() mutates LRU order); cache.get(key)
 * TODO put(key,value):        write lock; cache.put(key,value)
 * TODO computeIfAbsent(k,fn): write lock; check cache.get; if null compute+put; return
 * TODO containsKey(key):      read lock; cache.containsKey(key)
 * TODO size():                read lock; cache.size()
 * TODO evict(key):            write lock; cache.remove(key)
 * TODO getKeysLRUOrder():     read lock; new ArrayList<>(cache.keySet())
 */
public class ConcurrentLRUCache<K,V> {
    private final int capacity;
    private final LinkedHashMap<K,V> cache;
    private final ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    public ConcurrentLRUCache(int capacity) {
        if (capacity<=0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
        this.cache = new LinkedHashMap<K,V>(capacity,0.75f,true) {
            @Override protected boolean removeEldestEntry(Map.Entry<K,V> e) { return size()>capacity; }
        };
    }
    public V get(K key)                             { throw new UnsupportedOperationException("Implement get()"); }
    public void put(K key, V value)                 { throw new UnsupportedOperationException("Implement put()"); }
    public V computeIfAbsent(K key, Function<K,V> fn){ throw new UnsupportedOperationException("Implement computeIfAbsent()"); }
    public boolean containsKey(K key)               { throw new UnsupportedOperationException("Implement containsKey()"); }
    public int size()                               { throw new UnsupportedOperationException("Implement size()"); }
    public void evict(K key)                        { throw new UnsupportedOperationException("Implement evict()"); }
    public List<K> getKeysLRUOrder()                { throw new UnsupportedOperationException("Implement getKeysLRUOrder()"); }
}
