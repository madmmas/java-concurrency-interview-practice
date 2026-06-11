package com.concurrency.advanced.p34;

import java.util.concurrent.Executor;

/**
 * Problem 34 – Actor Model: Bank Account Actor
 *
 * Manages a bank account balance as private actor state.
 * External threads interact only via tell() / ask() — never by touching
 * the balance field directly.
 *
 * State type:  Integer  (the current balance in cents or whole units)
 * Message type: Command (sealed hierarchy below)
 */
public class BankAccountActor extends Actor<int[], BankAccountActor.Command> {

    // Using int[] so the balance is mutable inside the actor's state object.
    // Alternatively state could be a simple integer updated via reassignment.
    // We use int[] here as a simple mutable container compatible with Actor<S,M>.

    // ── Command hierarchy ─────────────────────────────────────────────────────

    /** Base type for all commands this actor understands. */
    public sealed interface Command
            permits BankAccountActor.Deposit,
                    BankAccountActor.Withdraw,
                    BankAccountActor.GetBalance {}

    /** Adds {@code amount} to the balance. Returns the new balance. */
    public record Deposit(int amount) implements Command {}

    /**
     * Subtracts {@code amount} from the balance.
     * Returns the new balance, or completes exceptionally with
     * {@link IllegalStateException} if balance would go negative.
     */
    public record Withdraw(int amount) implements Command {}

    /** Returns the current balance without modifying it. */
    public record GetBalance() implements Command {}

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * @param initialBalance starting balance
     * @param executor       executor used by the actor system for the processing loop
     */
    public BankAccountActor(int initialBalance, Executor executor) {
        super(new int[]{initialBalance});
        startLoop(executor);
    }

    // ── Message handler ───────────────────────────────────────────────────────

    /**
     * Handles each command on the actor's private thread.
     * {@code state[0]} is the current balance.
     *
     * @param state   one-element array holding the balance (mutable)
     * @param message the incoming Command
     * @return the new balance (int) after the command is applied
     * @throws IllegalStateException if a Withdraw would make the balance negative
     */
    @Override
    protected Object receive(int[] state, Command message) {
        // TODO:
        //   if (message instanceof Deposit d) {
        //       state[0] += d.amount();
        //       return state[0];
        //   } else if (message instanceof Withdraw w) {
        //       if (state[0] < w.amount())
        //           throw new IllegalStateException("Insufficient funds: balance=" + state[0]);
        //       state[0] -= w.amount();
        //       return state[0];
        //   } else if (message instanceof GetBalance) {
        //       return state[0];
        //   }
        //   throw new IllegalArgumentException("Unknown command: " + message);
        throw new UnsupportedOperationException("Implement receive()");
    }
}
