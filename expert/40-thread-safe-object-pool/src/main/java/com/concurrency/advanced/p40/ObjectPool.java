package com.concurrency.advanced.p40;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
/**
 * Problem 40 – Thread-Safe Object Pool
 * Pre-fills a LinkedBlockingQueue with factory-created objects; threads borrow and return.
 *
 * TODO borrow():           if closed throw ISE; pool.take(); borrowCount++
 * TODO borrow(timeoutMs):  if closed throw ISE; pool.poll(ms,MILLISECONDS)
 * TODO returnObject(obj):  if !closed pool.offer(obj)
 * TODO shutdown():         closed=true; pool.clear()
 * TODO borrowResource():   new PooledResource(borrow())
 */
public class ObjectPool<T> {
    private final BlockingQueue<T> pool;
    private final int totalSize;
    private volatile boolean closed = false;
    private final AtomicInteger borrowCount = new AtomicInteger(0);
    public ObjectPool(int size, Supplier<T> factory) {
        if (size<=0) throw new IllegalArgumentException("size must be > 0");
        this.totalSize = size; this.pool = new LinkedBlockingQueue<>(size);
        for (int i=0;i<size;i++) pool.offer(factory.get());
    }
    public T borrow() throws InterruptedException               { throw new UnsupportedOperationException("Implement borrow()"); }
    public T borrow(long ms) throws InterruptedException        { throw new UnsupportedOperationException("Implement borrow(long)"); }
    public void returnObject(T obj)                             { throw new UnsupportedOperationException("Implement returnObject()"); }
    public void shutdown()                                      { throw new UnsupportedOperationException("Implement shutdown()"); }
    public int availableCount() { return pool.size(); }
    public int totalSize()      { return totalSize; }
    public PooledResource borrowResource() throws InterruptedException { throw new UnsupportedOperationException("Implement borrowResource()"); }
    public PooledResource borrowResource(long ms) throws InterruptedException { throw new UnsupportedOperationException("Implement borrowResource(long)"); }
    public class PooledResource implements AutoCloseable {
        private final T obj;
        PooledResource(T obj) { this.obj = obj; }
        public T get() { return obj; }
        @Override public void close() { returnObject(obj); }
    }
}
