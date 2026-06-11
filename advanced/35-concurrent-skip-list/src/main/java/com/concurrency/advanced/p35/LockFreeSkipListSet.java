package com.concurrency.advanced.p35;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * Problem 35 – Concurrent Skip List: Lock-Free Implementation
 *
 * Uses AtomicMarkableReference<Node<T>> for each forward pointer.
 * The mark bit (boolean) represents logical deletion:
 *   false = node is live
 *   true  = node is logically deleted (being removed)
 *
 * Key invariants:
 *   - A node is logically present iff its level-0 next pointer is NOT marked.
 *   - Logical deletion (marking next[0]) is the linearisation point of remove().
 *   - Insertion's linearisation point is the CAS that sets next[0] from null to newNode.
 *   - Physically unlinked nodes are cleaned up lazily during find().
 *
 * @param <T> element type; must implement Comparable
 */
public class LockFreeSkipListSet<T extends Comparable<T>> implements SkipListSet<T> {

    static final int    MAX_LEVEL   = 16;
    static final double PROBABILITY = 0.5;

    // ── Node ──────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    static final class Node<T> {
        final T key;                                       // null for sentinels
        final AtomicMarkableReference<Node<T>>[] next;    // next[i] with mark bit

        /** Regular node. */
        Node(T key, int height) {
            this.key  = key;
            this.next = new AtomicMarkableReference[height];
            for (int i = 0; i < height; i++) {
                this.next[i] = new AtomicMarkableReference<>(null, false);
            }
        }

        /** Sentinel (head / tail). */
        Node(int height) {
            this(null, height);
        }

        int height() { return next.length; }
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private final Node<T> head = new Node<>(MAX_LEVEL);
    private final Node<T> tail = new Node<>(MAX_LEVEL);
    private final AtomicInteger size = new AtomicInteger(0);

    public LockFreeSkipListSet() {
        // Connect head → tail at every level (unmarked)
        for (int i = 0; i < MAX_LEVEL; i++) {
            head.next[i].set(tail, false);
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Inserts key into the set using lock-free CAS.
     *
     * Algorithm:
     *  retry:
     *    Node<T>[] preds, succs = find(key)     // also cleans up deleted nodes
     *    if succs[0].key == key && !isMarked(succs[0]) → return false (already present)
     *    newNode = new Node(key, randomLevel())
     *    link newNode.next[i] = succs[i] for all levels
     *    CAS preds[0].next[0] from succs[0] → newNode (unmarked)
     *    if CAS fails → retry (another thread modified preds[0])
     *    for levels 1..height-1: CAS preds[i].next[i] from succs[i] → newNode
     *    size++; return true
     *
     * @return true if newly inserted; false if already present
     */
    @Override
    public boolean add(T key) {
        // TODO: implement lock-free insertion with CAS retry loop
        throw new UnsupportedOperationException("Implement add()");
    }

    /**
     * Removes key from the set using lock-free two-phase deletion.
     *
     * Algorithm:
     *  find victim = succs[0] from find(key)
     *  if victim.key != key → return false (not found)
     *  Phase 1 – logical deletion:
     *    for level = victim.height-1 down to 1:
     *        CAS next[level] mark: false → true  (retry on failure)
     *    CAS next[0] mark: false → true   ← linearisation point
     *    if this CAS fails → return false (another thread deleted it first)
     *  Phase 2 – physical deletion:
     *    call find(key) which will clean up the now-marked node
     *  size--; return true
     *
     * @return true if found and removed; false if not found
     */
    @Override
    public boolean remove(T key) {
        // TODO: implement lock-free two-phase deletion
        throw new UnsupportedOperationException("Implement remove()");
    }

    /**
     * Returns true if key is present.
     * Wait-free: no CAS, no marks set — pure traversal.
     *
     * Algorithm:
     *  curr = head
     *  for level = MAX_LEVEL-1 down to 0:
     *    while true:
     *      succ = curr.next[level].get(marked)
     *      while succ.key != null && compare(succ.key, key) < 0:
     *          curr = succ; succ = curr.next[level].get(marked)
     *      break
     *  // curr.next[0] is the candidate; check key and not marked
     *  boolean[] marked = {false};
     *  Node<T> succ = curr.next[0].get(marked);
     *  return !marked[0] && succ.key != null && succ.key.compareTo(key) == 0
     */
    @Override
    public boolean contains(T key) {
        // TODO: implement wait-free read-only traversal
        throw new UnsupportedOperationException("Implement contains()");
    }

    /** Returns approximate size (AtomicInteger; may lag slightly). */
    @Override
    public int size() {
        return size.get();
    }

    /**
     * Level-0 traversal collecting all live (unmarked) nodes.
     * Skips logically deleted nodes.
     */
    @Override
    public List<T> toSortedList() {
        // TODO:
        //   List<T> result = new ArrayList<>();
        //   boolean[] marked = {false};
        //   Node<T> curr = head.next[0].get(marked);
        //   while (curr != tail) {
        //       if (!marked[0]) result.add(curr.key);
        //       curr = curr.next[0].get(marked);
        //   }
        //   return result;
        throw new UnsupportedOperationException("Implement toSortedList()");
    }

    /** Returns the smallest live element, or throws NoSuchElementException. */
    @Override
    public T first() {
        // TODO:
        //   boolean[] marked = {false};
        //   Node<T> candidate = head.next[0].get(marked);
        //   while (candidate != tail && marked[0])
        //       candidate = candidate.next[0].get(marked);
        //   if (candidate == tail) throw new NoSuchElementException();
        //   return candidate.key;
        throw new UnsupportedOperationException("Implement first()");
    }

    /** Returns the largest live element, or throws NoSuchElementException. */
    @Override
    public T last() {
        // TODO: traverse level-0 to find the last non-tail, non-marked node
        throw new UnsupportedOperationException("Implement last()");
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    /**
     * The core find() helper: traverses the skip list searching for key,
     * physically removing any logically deleted (marked) nodes encountered.
     *
     * Returns a {@link FindResult} containing:
     *   preds[i] = predecessor node at level i (last node whose key < key)
     *   succs[i] = successor  node at level i (first node whose key >= key)
     *
     * After find() returns, preds[0].next[0] == succs[0] (unmarked CAS invariant).
     *
     * This method is called by add(), remove(), and implicitly by contains().
     */
    FindResult<T> find(T key) {
        // TODO: implement the full find() traversal with physical deletion of marked nodes
        //
        // Outer retry loop (retries from head if a CAS during cleanup fails):
        //   retry:
        //   pred = head
        //   for level = MAX_LEVEL-1 down to 0:
        //     curr = pred.next[level].getReference()
        //     while true:
        //       boolean[] marked = {false}
        //       succ = curr.next[level].get(marked)
        //       while marked[0]:                 // curr is deleted — physically remove it
        //         boolean snip = pred.next[level].CAS(curr, succ, false, false)
        //         if !snip: goto retry           // someone else changed pred; restart
        //         curr = succ
        //         succ = curr.next[level].get(marked)
        //       if curr.key != null && curr.key.compareTo(key) < 0:
        //         pred = curr; curr = succ
        //       else: break
        //     preds[level] = pred
        //     succs[level] = curr
        //   return new FindResult<>(preds, succs)
        throw new UnsupportedOperationException("Implement find()");
    }

    /** Holds the predecessor and successor arrays returned by find(). */
    @SuppressWarnings("unchecked")
    static final class FindResult<T> {
        final Node<T>[] preds = new Node[MAX_LEVEL];
        final Node<T>[] succs = new Node[MAX_LEVEL];

        FindResult(Node<T>[] preds, Node<T>[] succs) {
            System.arraycopy(preds, 0, this.preds, 0, preds.length);
            System.arraycopy(succs, 0, this.succs, 0, succs.length);
        }
    }

    /** Generates a random node height capped at MAX_LEVEL. */
    private int randomLevel() {
        int level = 1;
        while (level < MAX_LEVEL && ThreadLocalRandom.current().nextDouble() < PROBABILITY) {
            level++;
        }
        return level;
    }
}
