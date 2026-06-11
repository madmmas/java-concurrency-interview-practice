package com.concurrency.advanced.p26;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Problem 26 – Lock-Free Data Structures: Treiber Stack
 *
 * A lock-free LIFO stack using AtomicReference and CAS retry loops.
 * No locks, no synchronized — system-wide progress is guaranteed.
 *
 * @param <T> element type
 */
public class TreiberStack<T> {

    static final class Node<T> {
        final T value;
        volatile Node<T> next;   // volatile so readers see fully-initialised next
        Node(T value) { this.value = value; }
    }

    private final AtomicReference<Node<T>> top  = new AtomicReference<>(null);
    private final AtomicInteger            count = new AtomicInteger(0);

    /**
     * Pushes item onto the top of the stack.
     *
     * CAS retry loop:
     *   1. Create newNode
     *   2. Read oldTop = top.get()
     *   3. Set newNode.next = oldTop   (link before CAS)
     *   4. CAS(oldTop, newNode) — if it fails, another thread raced us; retry
     */
    public void push(T item) {
        // TODO:
        //   Node<T> newNode = new Node<>(item);
        //   Node<T> oldTop;
        //   do {
        //       oldTop      = top.get();
        //       newNode.next = oldTop;
        //   } while (!top.compareAndSet(oldTop, newNode));
        //   count.incrementAndGet();
        throw new UnsupportedOperationException("Implement push()");
    }

    /**
     * Removes and returns the top element, or null if empty.
     *
     * CAS retry loop:
     *   1. Read oldTop = top.get()
     *   2. If null → return null (empty)
     *   3. Read newTop = oldTop.next
     *   4. CAS(oldTop, newTop) — if it fails, retry from step 1
     *   5. Return oldTop.value
     */
    public T pop() {
        // TODO:
        //   Node<T> oldTop, newTop;
        //   do {
        //       oldTop = top.get();
        //       if (oldTop == null) return null;
        //       newTop = oldTop.next;
        //   } while (!top.compareAndSet(oldTop, newTop));
        //   count.decrementAndGet();
        //   return oldTop.value;
        throw new UnsupportedOperationException("Implement pop()");
    }

    /**
     * Returns the top element without removing it, or null if empty.
     * Simple volatile read — no CAS needed.
     */
    public T peek() {
        // TODO: Node<T> t = top.get(); return t == null ? null : t.value;
        throw new UnsupportedOperationException("Implement peek()");
    }

    /** True if the stack contains no elements. */
    public boolean isEmpty() {
        return top.get() == null;
    }

    /**
     * Approximate size. Not linearisable under concurrent modification —
     * safe to call but may be momentarily stale.
     */
    public int size() {
        return Math.max(0, count.get());
    }
}
