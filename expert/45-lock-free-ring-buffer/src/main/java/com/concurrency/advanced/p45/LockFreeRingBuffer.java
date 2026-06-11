package com.concurrency.advanced.p45;
/**
 * Problem 45 – Lock-Free Ring Buffer (SPSC)
 * Fixed-capacity circular array; capacity must be a power of 2.
 * head: consumer-owned volatile long. tail: producer-owned volatile long.
 * mask = capacity-1 → fast modulo: index & mask.
 *
 * TODO offer(item): if tail-head>=capacity return false; array[tail&mask]=item; tail++; return true
 * TODO poll():      if head==tail return null; item=array[head&mask]; array[..]=null; head++; return item
 * TODO peek():      if head==tail return null; return array[head&mask]
 */
public class LockFreeRingBuffer<T> {
    private final Object[] array;
    private final int capacity, mask;
    private volatile long head=0, tail=0;
    public LockFreeRingBuffer(int capacity) {
        if(capacity<=0||(capacity&(capacity-1))!=0) throw new IllegalArgumentException("must be power of 2");
        this.capacity=capacity; this.mask=capacity-1; this.array=new Object[capacity];
    }
    public boolean offer(T item)  { throw new UnsupportedOperationException("Implement offer()"); }
    @SuppressWarnings("unchecked")
    public T poll()               { throw new UnsupportedOperationException("Implement poll()"); }
    @SuppressWarnings("unchecked")
    public T peek()               { throw new UnsupportedOperationException("Implement peek()"); }
    public boolean isEmpty()  { return tail==head; }
    public boolean isFull()   { return tail-head>=capacity; }
    public int size()         { return (int)(tail-head); }
    public int capacity()     { return capacity; }
}
