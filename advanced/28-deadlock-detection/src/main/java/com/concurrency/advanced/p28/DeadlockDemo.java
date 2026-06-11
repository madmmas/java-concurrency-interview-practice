package com.concurrency.advanced.p28;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Problem 28 – Deadlock Detection: Deadlock Demo & JVM-Level Detection
 *
 * Deliberately creates a deadlock (educational) and uses ThreadMXBean to detect it.
 */
public class DeadlockDemo {

    /**
     * Creates a classic two-thread, two-monitor deadlock:
     *
     *   Thread 1: synchronized(monitorA) → sleep 100ms → synchronized(monitorB)
     *   Thread 2: synchronized(monitorB) → sleep 100ms → synchronized(monitorA)
     *
     * Both threads enter their outer lock before either reaches the inner lock,
     * then each waits for the lock the other holds → deadlock.
     *
     * @return Thread[]{thread1, thread2}  (both alive and stuck after this call)
     */
    public Thread[] createDeadlock() throws InterruptedException {
        // TODO:
        //   Object monitorA = new Object();
        //   Object monitorB = new Object();
        //
        //   Thread t1 = new Thread(() -> {
        //       synchronized (monitorA) {
        //           try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        //           synchronized (monitorB) { /* never reached */ }
        //       }
        //   }, "deadlock-t1");
        //
        //   Thread t2 = new Thread(() -> {
        //       synchronized (monitorB) {
        //           try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        //           synchronized (monitorA) { /* never reached */ }
        //       }
        //   }, "deadlock-t2");
        //
        //   t1.start(); t2.start();
        //   Thread.sleep(200);   // let both threads enter their outer monitors
        //   return new Thread[]{t1, t2};
        throw new UnsupportedOperationException("Implement createDeadlock()");
    }

    /**
     * Uses ThreadMXBean.findDeadlockedThreads() to check whether t1 or t2
     * (or both) are part of a JVM-detected deadlock.
     *
     * @return true if at least one of t1/t2 appears in the deadlocked-thread list
     */
    public boolean detectDeadlock(Thread t1, Thread t2) {
        // TODO:
        //   ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        //   long[] deadlocked = bean.findDeadlockedThreads();
        //   if (deadlocked == null) return false;
        //   Set<Long> ids = Set.of(t1.getId(), t2.getId());
        //   return Arrays.stream(deadlocked).anyMatch(ids::contains);
        throw new UnsupportedOperationException("Implement detectDeadlock()");
    }
}
