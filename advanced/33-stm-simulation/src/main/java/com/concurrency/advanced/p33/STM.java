package com.concurrency.advanced.p33;

import java.util.function.Supplier;

/**
 * Problem 33 – STM Simulation: STM Utility
 *
 * Static helpers that run a transactional body with automatic retry on conflict.
 *
 * The caller writes business logic against TVars without worrying about
 * locking or conflict detection — the STM runtime handles retries transparently.
 *
 * Usage:
 *   TVar<Integer> balance = new TVar<>(1000);
 *   STM.atomically(() -> {
 *       int b = balance.read(tx);           // tx is the current Transaction
 *       balance.write(tx, b - 100);
 *   });
 *
 * Because Transaction is created and passed implicitly by atomically(), the
 * lambda receives a Transaction as a parameter. Use the overloads below.
 */
public class STM {

    private static final int MAX_RETRIES = 100;

    /**
     * Executes txBody in a fresh Transaction. If the commit detects a conflict
     * (RetryException), creates a new Transaction and retries up to MAX_RETRIES times.
     *
     * The Supplier receives the current Transaction via a thread-local so that
     * TVar.read() and TVar.write() can delegate to it transparently.
     *
     * @param txBody a lambda that reads/writes TVars; must be safe to re-run
     * @return the value returned by txBody after a successful commit
     * @throws IllegalStateException if MAX_RETRIES is exceeded
     */
    public static <T> T atomically(java.util.function.Function<Transaction, T> txBody) {
        // TODO:
        //   for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
        //       Transaction tx = new Transaction();
        //       try {
        //           T result = txBody.apply(tx);
        //           tx.commit();
        //           return result;
        //       } catch (Transaction.RetryException e) {
        //           // conflict — retry with a fresh transaction
        //       }
        //   }
        //   throw new IllegalStateException("STM: exceeded " + MAX_RETRIES + " retries");
        throw new UnsupportedOperationException("Implement atomically(Function)");
    }

    /**
     * Void variant of atomically().
     *
     * @param txBody a lambda that reads/writes TVars but returns nothing
     */
    public static void atomically(java.util.function.Consumer<Transaction> txBody) {
        // TODO: atomically(tx -> { txBody.accept(tx); return null; });
        throw new UnsupportedOperationException("Implement atomically(Consumer)");
    }

    // ── Pre-built transactional operations ───────────────────────────────────

    /**
     * Atomically transfers `amount` from TVar `from` to TVar `to`.
     *
     * Both the deduction and the addition are part of the same transaction —
     * they either both happen or neither does.
     *
     * @throws IllegalStateException if `from` has insufficient balance
     */
    public static void transfer(TVar<Integer> from, TVar<Integer> to, int amount) {
        // TODO: STM.atomically(tx -> {
        //   int fromVal = from.read(tx);
        //   if (fromVal < amount) throw new IllegalStateException("Insufficient balance");
        //   from.write(tx, fromVal - amount);
        //   to.write(tx, to.read(tx) + amount);
        // });
        throw new UnsupportedOperationException("Implement transfer()");
    }

    /**
     * Atomically swaps the values of two TVars.
     */
    public static <T> void swap(TVar<T> a, TVar<T> b) {
        // TODO: STM.atomically(tx -> {
        //   T va = a.read(tx);
        //   T vb = b.read(tx);
        //   a.write(tx, vb);
        //   b.write(tx, va);
        // });
        throw new UnsupportedOperationException("Implement swap()");
    }
}
