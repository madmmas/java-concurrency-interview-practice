package com.concurrency.advanced.p26;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Problem 26 – Lock-Free Data Structures: Michael-Scott Queue
 *
 * A lock-free FIFO queue using a dummy sentinel head node.
 * head always points to the sentinel; the first real element is head.next.
 *
 * Enqueue: CAS tail.next null→newNode, then swing tail forward.
 * Dequeue: CAS head sentinel→head.next (the new sentinel).
 *
 * @param <T> element type
 */
public class LockFreeQueue<T> {

    static final class Node<T> {
        final T value;
        final AtomicReference<Node<T>> next = new AtomicReference<>(null);
        Node(T value) { this.value = value; }
    }

    private final AtomicReference<Node<T>> head;   // sentinel
    private final AtomicReference<Node<T>> tail;   // last real node (or sentinel if empty)
    private final AtomicInteger            count = new AtomicInteger(0);

    public LockFreeQueue() {
        Node<T> sentinel = new Node<>(null);
        head = new AtomicReference<>(sentinel);
        tail = new AtomicReference<>(sentinel);
    }

    /**
     * Enqueues item at the tail.
     *
     * Algorithm:
     *   loop:
     *     t    = tail.get()
     *     next = t.next.get()
     *     if next == null:
     *         if t.next.CAS(null, newNode):   // successfully linked
     *             tail.CAS(t, newNode)         // swing tail (best-effort)
     *             return
     *     else:
     *         tail.CAS(t, next)               // help another thread advance tail
     */
    public void enqueue(T item) {
        // TODO: implement CAS-based enqueue
        throw new UnsupportedOperationException("Implement enqueue()");
    }

    /**
     * Dequeues and returns the front element, or null if empty.
     *
     * Algorithm:
     *   loop:
     *     h    = head.get()          // current sentinel
     *     t    = tail.get()
     *     next = h.next.get()        // first real node
     *     if h == head.get():        // snapshot still consistent
     *         if h == t:             // queue empty or tail lagging
     *             if next == null: return null
     *             tail.CAS(t, next)  // help advance tail
     *         else:
     *             val = next.value
     *             if head.CAS(h, next): return val   // next becomes new sentinel
     */
    public T dequeue() {
        // TODO: implement CAS-based dequeue
        throw new UnsupportedOperationException("Implement dequeue()");
    }

    /** True if only the sentinel remains (no real elements). */
    public boolean isEmpty() {
        return head.get().next.get() == null;
    }

    /** Approximate size — may be stale under concurrent modification. */
    public int size() {
        return Math.max(0, count.get());
    }
}
