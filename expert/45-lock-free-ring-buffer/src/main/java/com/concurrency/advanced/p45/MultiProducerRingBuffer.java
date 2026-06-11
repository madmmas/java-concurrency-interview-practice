package com.concurrency.advanced.p45;
import java.util.concurrent.atomic.AtomicLong;
/**
 * Problem 45 – Lock-Free Ring Buffer (MPSC)
 * Multiple producers claim slots via AtomicLong.getAndIncrement(); single consumer.
 * ready[]: volatile boolean flags; producer sets true after writing; consumer spins until true.
 *
 * TODO offer(item): claimedTail=tail.getAndIncrement(); if claimedTail-head>=capacity → tail.decrementAndGet(); return false
 *                   array[claimedTail&mask]=item; ready[claimedTail&mask]=true; return true
 * TODO poll():      h=head; if h==tail.get() return null; spin while !ready[h&mask];
 *                   item=array[h&mask]; array[..]=null; ready[..]=false; head=h+1; return item
 */
public class MultiProducerRingBuffer<T> {
    private final Object[]  array;
    private final boolean[] ready;
    private final int capacity, mask;
    private final AtomicLong tail = new AtomicLong(0);
    private volatile long head = 0;
    public MultiProducerRingBuffer(int capacity) {
        if(capacity<=0||(capacity&(capacity-1))!=0) throw new IllegalArgumentException("must be power of 2");
        this.capacity=capacity; this.mask=capacity-1; this.array=new Object[capacity]; this.ready=new boolean[capacity];
    }
    public boolean offer(T item)  { throw new UnsupportedOperationException("Implement offer()"); }
    @SuppressWarnings("unchecked")
    public T poll()               { throw new UnsupportedOperationException("Implement poll()"); }
    public boolean isEmpty()  { return tail.get()==head; }
    public int capacity()     { return capacity; }
    public int size()         { return (int)(tail.get()-head); }
}
