package com.concurrency.advanced.p47;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Problem 47 – Two-Phase Commit (2PC) Transaction Manager
 *
 * Phase 1 (parallel prepares): submit p.prepare(txId) for all; if any→ABORT or throws→decision=ABORT
 * Phase 2 (parallel finalize): if COMMIT→submit p.commit for all; else submit p.rollback for all; wait all
 *
 * TODO execute(txId):
 *   Phase1: List<Future<Vote>> votes = participants.stream().map(p->executor.submit(()->p.prepare(txId))).collect(...)
 *           boolean allCommit=true; for each future: try{if(f.get(1,SECONDS)==ABORT) allCommit=false}catch{allCommit=false}
 *   Phase2: List<Future<?>> fin=new ArrayList<>();
 *           if(allCommit): participants.forEach(p->fin.add(executor.submit(()->p.commit(txId)))); for(f:fin)f.get(); committedCount++; return COMMITTED
 *           else:          participants.forEach(p->fin.add(executor.submit(()->p.rollback(txId)))); for(f:fin)f.get(); abortedCount++; return ABORTED
 * TODO shutdown(): executor.shutdown(); executor.awaitTermination(5,SECONDS)
 */
public class TransactionCoordinator {
    public enum Vote              { COMMIT, ABORT }
    public enum TransactionResult { COMMITTED, ABORTED }

    public interface Participant {
        Vote   prepare(String txId);
        void   commit(String txId);
        void   rollback(String txId);
        String getName();
    }

    public static class ReliableParticipant implements Participant {
        private final String name;
        private final List<String> committed  = Collections.synchronizedList(new ArrayList<>());
        private final List<String> rolledBack = Collections.synchronizedList(new ArrayList<>());
        public ReliableParticipant(String name) { this.name=name; }
        @Override public Vote   prepare(String tx)  { return Vote.COMMIT; }
        @Override public void   commit(String tx)   { committed.add(tx); }
        @Override public void   rollback(String tx) { rolledBack.add(tx); }
        @Override public String getName()           { return name; }
        public List<String> getCommitted()  { return List.copyOf(committed); }
        public List<String> getRolledBack() { return List.copyOf(rolledBack); }
    }

    public static class UnreliableParticipant implements Participant {
        private final String name; private final double failProb;
        public UnreliableParticipant(String name, double p) { this.name=name; this.failProb=p; }
        @Override public Vote   prepare(String tx)  { return Math.random()<failProb?Vote.ABORT:Vote.COMMIT; }
        @Override public void   commit(String tx)   {}
        @Override public void   rollback(String tx) {}
        @Override public String getName()           { return name; }
    }

    private final List<Participant>   participants  = new CopyOnWriteArrayList<>();
    private final ExecutorService     executor      = Executors.newCachedThreadPool();
    private final AtomicInteger       committedCount = new AtomicInteger(0);
    private final AtomicInteger       abortedCount   = new AtomicInteger(0);

    public void registerParticipant(Participant p) { participants.add(p); }

    public TransactionResult execute(String txId) throws InterruptedException {
        // TODO: Phase 1 parallel prepares, Phase 2 parallel commit/rollback
        throw new UnsupportedOperationException("Implement execute()");
    }

    public int getCommittedCount() { return committedCount.get(); }
    public int getAbortedCount()   { return abortedCount.get(); }

    public void shutdown() throws InterruptedException {
        // TODO: executor.shutdown(); executor.awaitTermination(5, TimeUnit.SECONDS);
        throw new UnsupportedOperationException("Implement shutdown()");
    }
}
