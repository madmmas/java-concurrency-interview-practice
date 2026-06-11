package com.concurrency.advanced.p34;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 34 – Actor Model: Ping-Pong Actors
 *
 * Two actors exchange messages back and forth for a fixed number of rounds.
 * Demonstrates: actor-to-actor messaging, shared completion future, and
 * clean termination once a round counter reaches zero.
 *
 * Message flow (rounds = 3):
 *   Initiator ──ping──► PongActor ──pong──► PingActor
 *   PingActor  ──ping──► PongActor ──pong──► PingActor
 *   PingActor  ──ping──► PongActor ──pong──► PingActor  → done (rounds = 0)
 */
public class PingPongActors {

    // ── Message types ─────────────────────────────────────────────────────────

    sealed interface PingPongMessage
            permits PingPongActors.Ping, PingPongActors.Pong, PingPongActors.Stop {}

    /** Sent by Ping to Pong. */
    record Ping(Actor<?, PingPongMessage> replyTo) implements PingPongMessage {}

    /** Sent by Pong back to Ping. */
    record Pong(Actor<?, PingPongMessage> replyTo) implements PingPongMessage {}

    /** Signals an actor to stop its loop. */
    record Stop() implements PingPongMessage {}

    // ── Actor implementations ─────────────────────────────────────────────────

    /**
     * PingActor state: remaining rounds.
     * On Pong: if rounds > 0, send Ping back to pong; else complete the result future.
     * On Stop: call stop() on self.
     */
    static class PingActor extends Actor<int[], PingPongMessage> {

        private final CompletableFuture<Integer> resultFuture;
        private final AtomicInteger totalMessages;

        PingActor(int rounds,
                  CompletableFuture<Integer> resultFuture,
                  AtomicInteger totalMessages,
                  Executor executor) {
            super(new int[]{rounds}); // state[0] = remaining rounds
            this.resultFuture  = resultFuture;
            this.totalMessages = totalMessages;
            startLoop(executor);
        }

        @Override
        protected Object receive(int[] state, PingPongMessage message) {
            // TODO:
            //   if (message instanceof Pong pong) {
            //       totalMessages.incrementAndGet();
            //       state[0]--;                         // decrement rounds
            //       if (state[0] > 0) {
            //           pong.replyTo().tell(new Ping(this));  // keep going
            //       } else {
            //           resultFuture.complete(totalMessages.get());
            //           stop();                              // done
            //           pong.replyTo().tell(new Stop());     // stop pong too
            //       }
            //   } else if (message instanceof Stop) {
            //       stop();
            //   }
            //   return null;
            throw new UnsupportedOperationException("Implement PingActor.receive()");
        }
    }

    /**
     * PongActor state: total messages processed (not used for logic, just tracking).
     * On Ping: send Pong back to the replyTo actor; increment totalMessages.
     * On Stop: call stop() on self.
     */
    static class PongActor extends Actor<int[], PingPongMessage> {

        private final AtomicInteger totalMessages;

        PongActor(AtomicInteger totalMessages, Executor executor) {
            super(new int[]{0});
            this.totalMessages = totalMessages;
            startLoop(executor);
        }

        @Override
        protected Object receive(int[] state, PingPongMessage message) {
            // TODO:
            //   if (message instanceof Ping ping) {
            //       totalMessages.incrementAndGet();
            //       ping.replyTo().tell(new Pong(this));
            //   } else if (message instanceof Stop) {
            //       stop();
            //   }
            //   return null;
            throw new UnsupportedOperationException("Implement PongActor.receive()");
        }
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    /**
     * Creates Ping and Pong actors, fires the first Ping, and returns a future
     * that completes with the total number of messages exchanged.
     *
     * @param rounds number of full ping-pong exchanges (each round = 1 ping + 1 pong)
     * @return CompletableFuture<Integer> completed with total message count
     */
    public static CompletableFuture<Integer> run(int rounds, Executor executor) {
        // TODO:
        //   CompletableFuture<Integer> result = new CompletableFuture<>();
        //   AtomicInteger totalMessages = new AtomicInteger(0);
        //
        //   PongActor pong = new PongActor(totalMessages, executor);
        //   PingActor ping = new PingActor(rounds, result, totalMessages, executor);
        //
        //   // Kick off the exchange
        //   pong.tell(new Ping(ping));
        //
        //   return result;
        throw new UnsupportedOperationException("Implement run()");
    }
}
