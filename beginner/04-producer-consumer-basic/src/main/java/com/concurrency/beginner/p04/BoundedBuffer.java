package com.concurrency.beginner.p04;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Problem 04 - Producer-Consumer Basic
 *
 * A thread-safe bounded buffer using wait/notify.
 * Producers block when full; consumers block when empty.
 *
 * @param <T> the type of items stored in the buffer
 */
public class BoundedBuffer<T> {

    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

    public BoundedBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
    }

    /**
     * Adds an item to the buffer.
     * Blocks if the buffer is full until space becomes available.
     *
     * @param item the item to add (must not be null)
     */
    public synchronized void put(T item) throws InterruptedException {
        // TODO:
        //  1. while buffer is full, wait()
        //  2. add item to queue
        //  3. notifyAll()
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Removes and returns an item from the buffer.
     * Blocks if the buffer is empty until an item becomes available.
     *
     * @return the next item from the buffer
     */
    public synchronized T take() throws InterruptedException {
        // TODO:
        //  1. while buffer is empty, wait()
        //  2. remove and save head of queue
        //  3. notifyAll()
        //  4. return the removed item
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the number of items currently in the buffer. */
    public synchronized int size() {
        return queue.size();
    }

    /** Returns true if the buffer contains no items. */
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    /** Returns true if the buffer has reached its capacity. */
    public synchronized boolean isFull() {
        return queue.size() == capacity;
    }
}
