package com.concurrency.advanced.p35;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Problem 35 – Concurrent Skip List: Lock-Based Implementation
 *
 * A thread-safe sorted set using a skip list with per-node ReentrantLocks.
 * Uses hand-over-hand (lock-coupling) locking for add() and remove().
 * contains() is optimistic (no lock).
 *
 * Structure:
 *   head ──[level MAX_LEVEL-1]──► ... ──► tail
 *   head ──[level 1          ]──► ... ──► tail
 *   head ──[level 0          ]──► n1 ──► n2 ──► ... ──► tail
 *
 * Sentinel nodes:
 *   head.key = null (conceptually -∞)
 *   tail.key = null (conceptually +∞)
 *
 * @param <T> element type; must implement Comparable
 */
public class ConcurrentSkipListSet<T extends Comparable<T>> implements SkipListSet<T> {

    static final int MAX_LEVEL = 16;
    static final double PROBABILITY = 0.5;

    // ── Node ─────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    static final class Node<T> {
        final T key;                       // null for head and tail sentinels
        final Node<T>[] next;              // next[i] = forward pointer at level i
        final ReentrantLock lock = new ReentrantLock();
        volatile boolean deleted = false;

        /** Regular node constructor. */
        Node(T key, int height) {
            this.key  = key;
            this.next = new Node[height];
        }

        /** Sentinel constructor (head / tail) — full height. */
        Node(int height) {
            this.key  = null;
            this.next = new Node[height];
        }

        void lock()   { lock.lock(); }
        void unlock() { lock.unlock(); }
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private final Node<T> head = new Node<>(MAX_LEVEL);
    private final Node<T> tail = new Node<>(MAX_LEVEL);
    private final AtomicInteger size = new AtomicInteger(0);

    @SuppressWarnings("unchecked")
    public ConcurrentSkipListSet() {
        // Connect head → tail at every level
        Arrays.fill(head.next, tail);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Inserts key into the skip list.
     *
     * Algorithm (lock-based, hand-over-hand at level 0):
     *  1. Traverse to find predecessors at each level: update[0..MAX_LEVEL-1]
     *  2. Lock update[0] and update[0].next[0]
     *  3. Validate: update[0] is not deleted and update[0].next[0] is not deleted
     *     and update[0].next[0].key == key → already present (unlock, return false)
     *  4. Create newNode with randomLevel() height
     *  5. Link at level 0 under the lock; unlock
     *  6. For levels 1..newNode.height-1: re-find predecessor, lock it, link, unlock
     *  7. size++; return true
     *
     * @return true if newly inserted; false if key was already present
     */
    @Override
    public boolean add(T key) {
        // TODO: implement hand-over-hand lock-based insertion
        throw new UnsupportedOperationException("Implement add()");
    }

    /**
     * Removes key from the skip list.
     *
     * Algorithm:
     *  1. Find predecessors at each level: update[]
     *  2. Lock the target node (update[0].next[0]) and verify it has the right key
     *  3. Mark node as deleted
     *  4. For each level: lock update[i], unlink node, unlock update[i]
     *  5. size--; return true
     *
     * @return true if removed; false if key was not found
     */
    @Override
    public boolean remove(T key) {
        // TODO: implement lock-based removal with logical + physical deletion
        throw new UnsupportedOperationException("Implement remove()");
    }

    /**
     * Returns true if key is present.
     * Optimistic: no locks acquired (reads volatile fields only).
     *
     * Algorithm:
     *  curr = head
     *  for level = MAX_LEVEL-1 down to 0:
     *    while curr.next[level].key != null && curr.next[level].key < key:
     *        curr = curr.next[level]
     *  curr = curr.next[0]
     *  return !curr.deleted && curr.key != null && curr.key.equals(key)
     */
    @Override
    public boolean contains(T key) {
        // TODO: implement optimistic lock-free traversal
        throw new UnsupportedOperationException("Implement contains()");
    }

    /** Returns the current number of elements. */
    @Override
    public int size() {
        return size.get();
    }

    /**
     * Returns all elements in ascending sorted order (level-0 traversal).
     * Skips logically deleted nodes.
     */
    @Override
    public List<T> toSortedList() {
        // TODO:
        //   List<T> result = new ArrayList<>();
        //   Node<T> curr = head.next[0];
        //   while (curr != tail) {
        //       if (!curr.deleted) result.add(curr.key);
        //       curr = curr.next[0];
        //   }
        //   return result;
        throw new UnsupportedOperationException("Implement toSortedList()");
    }

    /**
     * Returns the smallest element (first non-sentinel node at level 0).
     *
     * @throws NoSuchElementException if the set is empty
     */
    @Override
    public T first() {
        // TODO:
        //   Node<T> first = head.next[0];
        //   if (first == tail) throw new NoSuchElementException("Set is empty");
        //   return first.key;
        throw new UnsupportedOperationException("Implement first()");
    }

    /**
     * Returns the largest element (last non-sentinel node at level 0).
     *
     * @throws NoSuchElementException if the set is empty
     */
    @Override
    public T last() {
        // TODO: traverse level-0 list to find the last node before tail
        //   Node<T> curr = head;
        //   while (curr.next[0] != tail) curr = curr.next[0];
        //   if (curr == head) throw new NoSuchElementException("Set is empty");
        //   return curr.key;
        throw new UnsupportedOperationException("Implement last()");
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    /**
     * Finds the predecessor node at every level for the given key.
     * Returns an array update[] where update[i] is the rightmost node at level i
     * whose key is strictly less than the target key.
     *
     * Called without any locks held — pure traversal.
     */
    @SuppressWarnings("unchecked")
    private Node<T>[] findPredecessors(T key) {
        // TODO:
        //   Node<T>[] update = new Node[MAX_LEVEL];
        //   Node<T> curr = head;
        //   for (int level = MAX_LEVEL - 1; level >= 0; level--) {
        //       while (curr.next[level] != tail &&
        //              (curr.next[level].key == null || curr.next[level].key.compareTo(key) < 0)) {
        //           curr = curr.next[level];
        //       }
        //       update[level] = curr;
        //   }
        //   return update;
        throw new UnsupportedOperationException("Implement findPredecessors()");
    }

    /**
     * Generates a random height for a new node.
     * height = 1 always; each additional level added with probability PROBABILITY.
     * Capped at MAX_LEVEL.
     */
    private int randomLevel() {
        int level = 1;
        while (level < MAX_LEVEL && ThreadLocalRandom.current().nextDouble() < PROBABILITY) {
            level++;
        }
        return level;
    }

    /**
     * Returns true if a is ordered strictly before b in the skip list.
     * null key means sentinel (head = -∞, tail = +∞).
     */
    private boolean lessThan(T a, T b) {
        if (a == null) return true;   // head is always less than everything
        if (b == null) return false;  // tail is never less than anything
        return a.compareTo(b) < 0;
    }
}
