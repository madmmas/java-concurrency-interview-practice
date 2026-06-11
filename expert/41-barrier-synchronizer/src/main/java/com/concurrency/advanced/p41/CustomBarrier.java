package com.concurrency.advanced.p41;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
/**
 * Problem 41 – Custom Barrier Synchronizer
 * Reusable CyclicBarrier built from ReentrantLock + Condition.
 *
 * State: count (arrived), generation (increments per trip), broken flag.
 * TODO await():       lock; ++count; if count==parties → run action, signalAll, reset count, generation++
 *                     else loop: while gen unchanged && !broken && count<parties → condition.await()
 *                     if broken → throw BrokenBarrierException
 * TODO await(ms):     same but use condition.awaitNanos(ns); throw TimeoutException
 * TODO reset():       lock; broken=true; signalAll; broken=false; count=0; generation++
 */
public class CustomBarrier {
    private final int parties;
    private final Runnable barrierAction;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition trip = lock.newCondition();
    private int count=0, generation=0;
    private boolean broken=false;
    public CustomBarrier(int parties) { this(parties,null); }
    public CustomBarrier(int parties, Runnable action) {
        if (parties<=0) throw new IllegalArgumentException(); this.parties=parties; this.barrierAction=action;
    }
    public int await() throws InterruptedException, BrokenBarrierException { throw new UnsupportedOperationException("Implement await()"); }
    public int await(long ms) throws InterruptedException, BrokenBarrierException, TimeoutException { throw new UnsupportedOperationException("Implement await(long)"); }
    public void reset() { throw new UnsupportedOperationException("Implement reset()"); }
    public boolean isBroken() { return broken; }
    public int getNumberWaiting() { lock.lock(); try{ return count; }finally{ lock.unlock(); } }
    public int getParties() { return parties; }
}
