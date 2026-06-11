package com.concurrency.advanced.p34;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Problem 34 – Actor Model: Actor System
 *
 * Lightweight runtime that:
 *  - Owns a shared Executor (fixed thread pool)
 *  - Creates and tracks registered actors
 *  - Provides a clean shutdown path
 */
public class ActorSystem {

    private final ExecutorService executor;
    private final List<Actor<?, ?>> actors = new CopyOnWriteArrayList<>();

    /**
     * @param threads number of threads in the shared executor pool
     */
    public ActorSystem(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
    }

    /**
     * Creates a new actor, starts its processing loop on this system's executor,
     * registers it, and returns it.
     *
     * @param initialState  the actor's starting state
     * @param factory       a factory that constructs the actor given the state and executor
     * @param <S>           actor state type
     * @param <M>           actor message type
     * @return              the running actor
     */
    public <S, M> Actor<S, M> actorOf(S initialState, ActorFactory<S, M> factory) {
        // TODO:
        //   Actor<S, M> actor = factory.create(initialState, executor);
        //   actors.add(actor);
        //   return actor;
        throw new UnsupportedOperationException("Implement actorOf()");
    }

    /**
     * Stops all registered actors (sends stop signal) then shuts down the executor.
     * Waits up to 5 seconds for graceful termination.
     */
    public void shutdown() throws InterruptedException {
        // TODO:
        //   actors.forEach(Actor::stop);
        //   executor.shutdown();
        //   executor.awaitTermination(5, TimeUnit.SECONDS);
        throw new UnsupportedOperationException("Implement shutdown()");
    }

    /** Returns the number of actors currently registered in this system. */
    public int getActorCount() {
        return actors.size();
    }

    // ── Factory functional interface ──────────────────────────────────────────

    /**
     * A factory that constructs an Actor given initial state and an executor.
     * Typically implemented as a constructor reference or lambda.
     *
     * Example:
     *   system.actorOf(0, (state, exec) -> new MyActor(state, exec));
     */
    @FunctionalInterface
    public interface ActorFactory<S, M> {
        Actor<S, M> create(S initialState, Executor executor);
    }
}
