package com.concurrency.advanced.p35;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Problem 35 – Concurrent Skip List: Common interface for both implementations.
 *
 * Both {@link ConcurrentSkipListSet} (lock-based) and
 * {@link LockFreeSkipListSet} (lock-free) implement this interface,
 * so {@link SkipListBenchmark} can test either interchangeably.
 *
 * @param <T> element type; must be Comparable
 */
public interface SkipListSet<T extends Comparable<T>> {

    /**
     * Inserts key into the set.
     *
     * @return {@code true} if key was not already present; {@code false} if it was
     */
    boolean add(T key);

    /**
     * Removes key from the set.
     *
     * @return {@code true} if key was present and removed; {@code false} if absent
     */
    boolean remove(T key);

    /**
     * Returns {@code true} if key is currently in the set.
     */
    boolean contains(T key);

    /**
     * Returns the number of elements currently in the set.
     * May be approximate under heavy concurrent modification.
     */
    int size();

    /**
     * Returns a sorted snapshot of all elements in ascending order.
     * The snapshot reflects the state at the moment of the call;
     * concurrent modifications after this point are not reflected.
     */
    List<T> toSortedList();

    /**
     * Returns the smallest element in the set.
     *
     * @throws NoSuchElementException if the set is empty
     */
    T first();

    /**
     * Returns the largest element in the set.
     *
     * @throws NoSuchElementException if the set is empty
     */
    T last();
}
