package com.concurrency.advanced.p28;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Problem 28 – Deadlock Detection: Deadlock Preventer
 *
 * Two helpers that prevent deadlock by design — no graph tracking needed.
 */
public class DeadlockPreventer {

    /**
     * Static tie-breaker lock used when two locks have the same identityHashCode.
     * Acquiring it before both target locks ensures a global ordering even in the
     * hash-collision edge case.
     */
    private static final ReentrantLock TIE_BREAKER = new ReentrantLock();

    /**
     * Acquires both lockA and lockB in a consistent global order derived from
     * System.identityHashCode(). Always acquires the lower-hash lock first.
     *
     * This breaks Coffman's "circular wait" condition: two threads calling
     * acquireInOrder(A,B) and acquireInOrder(B,A) will acquire in the same order
     * and therefore never deadlock.
     *
     * @return a Runnable that releases both locks when invoked (unlock in reverse order)
     */
    public Runnable acquireInOrder(ReentrantLock lockA, ReentrantLock lockB) {
        // TODO:
        //   int hashA = System.identityHashCode(lockA);
        //   int hashB = System.identityHashCode(lockB);
        //   ReentrantLock first, second;
        //   if (hashA < hashB)      { first = lockA; second = lockB; }
        //   else if (hashA > hashB) { first = lockB; second = lockA; }
        //   else {
        //       TIE_BREAKER.lock();      // prevent two threads from racing on equal hashes
        //       try { lockA.lock(); lockB.lock(); }
        //       finally { TIE_BREAKER.unlock(); }
        //       return () -> { lockB.unlock(); lockA.unlock(); };
        //   }
        //   first.lock(); second.lock();
        //   return () -> { second.unlock(); first.unlock(); };
        throw new UnsupportedOperationException("Implement acquireInOrder()");
    }

    /**
     * Attempts to acquire lockA then lockB, both within a total deadline.
     *
     * If lockB cannot be acquired before the deadline, lockA is released
     * and the method returns false. Uses tryLock() — breaks "hold and wait".
     *
     * @param timeoutMs total budget in milliseconds
     * @return true if both locks were acquired; false if deadline expired
     */
    public boolean tryAcquireBothWithTimeout(ReentrantLock lockA,
                                             ReentrantLock lockB,
                                             long timeoutMs) throws InterruptedException {
        // TODO:
        //   long deadline = System.currentTimeMillis() + timeoutMs;
        //   while (System.currentTimeMillis() < deadline) {
        //       long remaining = deadline - System.currentTimeMillis();
        //       if (remaining <= 0) break;
        //       if (lockA.tryLock(remaining, TimeUnit.MILLISECONDS)) {
        //           remaining = deadline - System.currentTimeMillis();
        //           if (remaining > 0 && lockB.tryLock(remaining, TimeUnit.MILLISECONDS)) {
        //               return true;   // both acquired
        //           }
        //           lockA.unlock();   // couldn't get B; release A and retry
        //       }
        //   }
        //   return false;
        throw new UnsupportedOperationException("Implement tryAcquireBothWithTimeout()");
    }
}
