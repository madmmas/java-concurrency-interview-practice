package com.concurrency.advanced.p33;

import java.util.*;

/**
 * Problem 33 – STM Simulation: Transaction Context
 *
 * Holds a private read-set and write-set for one transactional attempt.
 *
 *   read-set:  TVar → version-at-read-time   (for conflict detection at commit)
 *   write-set: TVar → new-value              (buffered local writes)
 *
 * Commit protocol:
 *   1. Sort write-set TVars by System.identityHashCode (consistent lock order → no deadlock)
 *   2. Lock each write-set TVar
 *   3. Validate: every read-set TVar's current version == recorded version
 *   4. If valid: apply writes (value + version++) then unlock
 *   5. If invalid: unlock then throw RetryException
 */
public class Transaction {

    /** Maps each read TVar to the version we observed when we read it. */
    private final Map<TVar<?>, Long> readSet  = new IdentityHashMap<>();

    /** Maps each written TVar to the new (uncommitted) value. */
    private final Map<TVar<?>, Object> writeSet = new IdentityHashMap<>();

    private boolean aborted = false;

    // ── Transaction operations ────────────────────────────────────────────────

    /**
     * Reads a value from the transaction's perspective:
     *  - If the TVar is in the write-set, return the buffered value.
     *  - Otherwise, read the TVar's current committed value AND version,
     *    record (tvar → version) in the read-set, and return the value.
     *
     * @param tvar the variable to read
     * @return the value visible within this transaction
     */
    @SuppressWarnings("unchecked")
    public <T> T read(TVar<T> tvar) {
        // TODO:
        //   if (writeSet.containsKey(tvar)) return (T) writeSet.get(tvar);
        //   T val = tvar.value;
        //   readSet.put(tvar, tvar.version);
        //   return val;
        throw new UnsupportedOperationException("Implement read()");
    }

    /**
     * Buffers a write locally — does NOT modify the TVar yet.
     * Overwrites any previous buffered write for the same TVar.
     *
     * @param tvar     the variable to write
     * @param newValue the value to commit later
     */
    public <T> void write(TVar<T> tvar, T newValue) {
        // TODO: writeSet.put(tvar, newValue);
        throw new UnsupportedOperationException("Implement write()");
    }

    /**
     * Attempts to commit the transaction.
     *
     * Steps:
     *  1. Sort write-set keys by System.identityHashCode (avoid deadlock on lock acquisition)
     *  2. Lock each write-set TVar in sorted order
     *  3. Validate: for each (tvar, recordedVersion) in read-set,
     *               if tvar.version != recordedVersion → conflict detected
     *  4a. On success: for each (tvar, newValue) in write-set:
     *                    tvar.value = newValue
     *                    tvar.version++
     *  4b. On conflict: aborted = true; throw new RetryException()
     *  5. Unlock all write-set TVars in finally
     *
     * @throws RetryException if a conflict is detected (caller should retry)
     */
    @SuppressWarnings("unchecked")
    public void commit() {
        // TODO: implement the 5-step commit protocol
        throw new UnsupportedOperationException("Implement commit()");
    }

    /** Returns true if this transaction was aborted due to a conflict. */
    public boolean isAborted() {
        return aborted;
    }

    // ── Retry signal ──────────────────────────────────────────────────────────

    /** Thrown by commit() when a conflict is detected — not a fatal error. */
    public static class RetryException extends RuntimeException {
        public RetryException() { super("Transaction conflict — retry required"); }
    }
}
