package com.concurrency.advanced.p34;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Problem 34 – Actor Model: Generic Actor Base Class
 *
 * An actor owns:
 *  - A private mailbox (LinkedBlockingQueue of Envelopes)
 *  - Private mutable state of type S (only ever touched on the actor's own thread)
 *  - A processing loop running on the provided Executor
 *
 * External threads interact ONLY via tell() and ask() — never by touching state directly.
 *
 * @param <S> the type of the actor's private state
 * @param <M> the type of messages this actor accepts
 */
public abstract class Actor<S, M> {

    // ── Envelope: message + optional reply future ─────────────────────────────

    /**
     * Wraps a message together with an optional CompletableFuture.
     *  - tell()  → replyFuture is null
     *  - ask()   → replyFuture is non-null; completed after receive() returns
     */
    static final class Envelope<M> {
        final M message;
        final CompletableFuture<Object> replyFuture; // null for tell()

        Envelope(M message, CompletableFuture<Object> replyFuture) {
            this.message     = message;
            this.replyFuture = replyFuture;
        }
    }

    // ── Actor internals ───────────────────────────────────────────────────────

    /** Poison-pill sentinel — signals the processing loop to stop. */
    private static final Object STOP = new Object();

    private final LinkedBlockingQueue<Object> mailbox = new LinkedBlockingQueue<>();
    // Uses Object so the poison-pill can be enqueued without generic-type issues.

    protected S state;               // private to the actor's processing thread
    private final AtomicBoolean alive = new AtomicBoolean(false);

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates an actor with the given initial state.
     * Does NOT start the processing loop — call startLoop(executor) to begin.
     * (ActorSystem calls startLoop; direct subclass tests can call it too.)
     */
    protected Actor(S initialState) {
        this.state = initialState;
    }

    /**
     * Starts the actor's message-processing loop on the given executor.
     * Called by ActorSystem.actorOf() after the actor is created.
     *
     * The loop:
     *   1. alive = true
     *   2. poll mailbox with 100 ms timeout
     *   3. if object == STOP: alive = false; return (exit loop)
     *   4. if object is an Envelope<M>: call dispatchEnvelope(envelope)
     *   5. repeat while alive
     *
     * The loop must handle InterruptedException by setting alive=false and exiting.
     */
    @SuppressWarnings("unchecked")
    void startLoop(Executor executor) {
        // TODO:
        //   alive.set(true);
        //   executor.execute(() -> {
        //       while (alive.get()) {
        //           try {
        //               Object obj = mailbox.poll(100, TimeUnit.MILLISECONDS);
        //               if (obj == STOP) { alive.set(false); break; }
        //               if (obj != null) dispatchEnvelope((Envelope<M>) obj);
        //           } catch (InterruptedException e) {
        //               alive.set(false);
        //               Thread.currentThread().interrupt();
        //               break;
        //           }
        //       }
        //   });
        throw new UnsupportedOperationException("Implement startLoop()");
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Enqueues a message in the mailbox (fire-and-forget).
     * Returns immediately — never blocks the caller.
     *
     * @param message the message to send
     */
    public void tell(M message) {
        // TODO: mailbox.offer(new Envelope<>(message, null));
        throw new UnsupportedOperationException("Implement tell()");
    }

    /**
     * Enqueues a message and returns a CompletableFuture that will be
     * completed with the return value of receive() when the actor processes it.
     *
     * @param message the message to send
     * @return a future completed with the actor's reply
     */
    public CompletableFuture<Object> ask(M message) {
        // TODO:
        //   CompletableFuture<Object> future = new CompletableFuture<>();
        //   mailbox.offer(new Envelope<>(message, future));
        //   return future;
        throw new UnsupportedOperationException("Implement ask()");
    }

    /**
     * Sends a poison-pill to the actor's mailbox.
     * The processing loop exits cleanly after processing all pending messages
     * that arrived before the stop signal.
     */
    public void stop() {
        // TODO: mailbox.offer(STOP);
        throw new UnsupportedOperationException("Implement stop()");
    }

    /** Returns true if the actor's processing loop is still running. */
    public boolean isAlive() {
        return alive.get();
    }

    // ── Dispatch helper ───────────────────────────────────────────────────────

    /**
     * Called by the processing loop for each non-poison-pill envelope.
     * Invokes receive(state, message); if the envelope has a replyFuture,
     * completes it with the return value (or completes exceptionally on error).
     */
    private void dispatchEnvelope(Envelope<M> envelope) {
        // TODO:
        //   try {
        //       Object result = receive(state, envelope.message);
        //       if (envelope.replyFuture != null) envelope.replyFuture.complete(result);
        //   } catch (Exception e) {
        //       if (envelope.replyFuture != null) envelope.replyFuture.completeExceptionally(e);
        //       else throw e;  // unhandled tell() exception — let the loop log/handle it
        //   }
        throw new UnsupportedOperationException("Implement dispatchEnvelope()");
    }

    // ── Abstract message handler ──────────────────────────────────────────────

    /**
     * Called by the processing loop — always on the actor's own thread.
     * Subclasses implement all business logic here.
     *
     * May mutate {@code state} freely (no other thread ever touches it).
     *
     * @param state   the actor's current private state (may be mutated)
     * @param message the incoming message
     * @return reply value (used to complete ask() futures; ignored for tell())
     */
    protected abstract Object receive(S state, M message);
}
