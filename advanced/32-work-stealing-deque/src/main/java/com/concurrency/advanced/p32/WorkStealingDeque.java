package com.concurrency.advanced.p32;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Problem 32 – Work-Stealing Deque
 *
 * A double-ended queue where:
 *  - The OWNER thread pushes and pops from the BOTTOM (LIFO — promotes cache locality)
 *  - THIEF threads steal from the TOP (FIFO — grab older, larger tasks first)
 *
 * Simplified design (not fully lock-free — uses a lock for steal() only):
 *  - bottom is a plain int field (only the owner writes it)
 *  - top is an AtomicInteger (thieves CAS it; owner reads it)
 *  - steal() acquires a lock so thieves don't race each other on top
 *
 * Internal storage: circular array that grows when full (doubles capacity).
 *
 * @param <T> task type
 */
public class WorkStealingDeque<T> {

    private static final int INITIAL_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    private volatile T[] array = (T[]) new Object[INITIAL_CAPACITY];

    /** Index of the next empty slot at the bottom (owner writes here). */
    private int bottom = 0;

    /** Index of the topmost element (thieves steal from here). */
    private final AtomicInteger top = new AtomicInteger(0);

    /** Protects steal() so concurrent thieves don't both increment top. */
    private final ReentrantLock stealLock = new ReentrantLock();

    // ── Owner operations (called only by the owning thread) ───────────────────

    /**
     * Pushes a task onto the bottom of the deque.
     * Called ONLY by the owner thread.
     *
     * Algorithm:
     *  1. If full (bottom - top.get() >= array.length), grow (double capacity)
     *  2. array[bottom % array.length] = task
     *  3. bottom++
     */
    public void push(T task) {
        // TODO: check capacity, write to array[bottom % length], increment bottom
        throw new UnsupportedOperationException("Implement push()");
    }

    /**
     * Pops a task from the bottom of the deque.
     * Called ONLY by the owner thread.
     * Returns null if the deque is empty.
     *
     * Algorithm:
     *  1. int b = --bottom
     *  2. T task = array[b % array.length]
     *  3. if (b < top.get()) { bottom = top.get(); return null; }  // lost race with thief
     *  4. if (b == top.get()) { if (!top.CAS(b, b+1)) task = null; bottom = b+1; }
     *  5. return task
     */
    public T pop() {
        // TODO: implement the owner-pop protocol with last-element race handling
        throw new UnsupportedOperationException("Implement pop()");
    }

    // ── Thief operation (may be called by any thread) ─────────────────────────

    /**
     * Steals a task from the top of the deque.
     * May be called by any thread (including non-owner threads).
     * Returns null if the deque is empty or another thief won the race.
     *
     * Algorithm (under stealLock):
     *  1. int t = top.get()
     *  2. if (t >= bottom) return null  (empty)
     *  3. T task = array[t % array.length]
     *  4. top.incrementAndGet()   (we hold the lock, so this is safe)
     *  5. return task
     */
    public T steal() {
        // TODO: acquire stealLock, check emptiness, read task, advance top, unlock
        throw new UnsupportedOperationException("Implement steal()");
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    /**
     * Returns the approximate number of elements.
     * May be momentarily inaccurate due to concurrent operations.
     */
    public int size() {
        return Math.max(0, bottom - top.get());
    }

    /** Returns true if the deque appears empty. */
    public boolean isEmpty() {
        return size() == 0;
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    /**
     * Doubles the capacity of the internal array.
     * Called only by the owner inside push() when the array is full.
     */
    @SuppressWarnings("unchecked")
    private void grow() {
        // TODO:
        //   T[] newArray = (T[]) new Object[array.length * 2];
        //   int t = top.get();
        //   for (int i = t; i < bottom; i++)
        //       newArray[i % newArray.length] = array[i % array.length];
        //   array = newArray;  // volatile write — visible to thieves
        throw new UnsupportedOperationException("Implement grow()");
    }
}
