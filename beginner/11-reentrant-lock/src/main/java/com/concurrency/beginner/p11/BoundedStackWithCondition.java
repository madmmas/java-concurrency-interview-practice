package com.concurrency.beginner.p11;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Problem 11 – ReentrantLock: Bounded Stack with Two Conditions
 *
 * Uses ReentrantLock + two Condition objects (notFull / notEmpty) instead of
 * synchronized + notifyAll(), which is more efficient for producer-consumer patterns.
 */
public class BoundedStackWithCondition<T> {

    private final Deque<T> stack = new ArrayDeque<>();
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull  = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedStackWithCondition(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
    }

    /**
     * Pushes item onto the stack. Blocks if the stack is full.
     */
    public void push(T item) throws InterruptedException {
        // TODO:
        //  lock → while full: notFull.await() → push → notEmpty.signal() → unlock(finally)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Pops and returns the top item. Blocks if the stack is empty.
     */
    public T pop() throws InterruptedException {
        // TODO:
        //  lock → while empty: notEmpty.await() → pop → notFull.signal() → unlock(finally)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Returns (but does not remove) the top item. Blocks if the stack is empty.
     */
    public T peek() throws InterruptedException {
        // TODO: lock → while empty: notEmpty.await() → peek → unlock(finally)
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the current number of elements in the stack. */
    public int size() {
        lock.lock();
        try { return stack.size(); }
        finally { lock.unlock(); }
    }

    public boolean isEmpty() { return size() == 0; }
    public boolean isFull()  { return size() == capacity; }
}
