package com.concurrency.advanced.p33;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Problem 33 – STM Simulation: Transactional Variable
 *
 * A versioned shared variable. Reads and writes go through a Transaction
 * to give the illusion of atomicity across multiple TVars.
 *
 * Internally:
 *   - value:   the currently committed value (volatile for direct reads)
 *   - version: monotonically increasing; incremented at every commit
 *   - lock:    acquired by the committing transaction during the write phase
 *
 * @param <T> the type of value stored
 */
public class TVar<T> {

    volatile T    value;
    volatile long version = 0;
    final ReentrantLock lock = new ReentrantLock();

    public TVar(T initialValue) {
        this.value = initialValue;
    }

    /**
     * Reads the value through the given transaction.
     *
     * Delegates to tx.read(this): the transaction returns the locally-buffered
     * write if one exists, otherwise reads and records the current value + version
     * in its read-set.
     */
    public T read(Transaction tx) {
        // TODO: return tx.read(this);
        throw new UnsupportedOperationException("Implement read(Transaction)");
    }

    /**
     * Writes a new value through the given transaction.
     * The write is buffered locally — not committed until tx.commit().
     */
    public void write(Transaction tx, T newValue) {
        // TODO: tx.write(this, newValue);
        throw new UnsupportedOperationException("Implement write(Transaction)");
    }

    /**
     * Reads the current committed value directly (bypasses any transaction).
     * Used by tests to inspect state after commit.
     */
    public T readDirect() {
        return value;
    }

    /** Returns the current committed version number. */
    public long getVersion() {
        return version;
    }
}
