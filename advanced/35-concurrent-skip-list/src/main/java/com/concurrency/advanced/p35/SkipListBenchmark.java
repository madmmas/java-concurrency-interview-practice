package com.concurrency.advanced.p35;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Problem 35 – Concurrent Skip List: Benchmark Harness
 *
 * Provides add-only and mixed read/write benchmarks so you can compare
 * the lock-based and lock-free implementations under concurrent load.
 *
 * Results are returned as elapsed milliseconds so tests can make
 * loose assertions (e.g., completes without deadlock/timeout).
 */
public class SkipListBenchmark {

    /**
     * Benchmarks concurrent add() operations.
     *
     * Spawns {@code threads} worker threads, each inserting {@code keysPerThread}
     * unique integer keys. All threads start simultaneously via a CountDownLatch.
     * Returns elapsed wall-clock time in milliseconds.
     *
     * @param set           the skip list to benchmark (must be empty at entry)
     * @param threads       number of concurrent inserter threads
     * @param keysPerThread number of keys each thread inserts
     * @return elapsed time in milliseconds
     */
    public long benchmarkAdd(SkipListSet<Integer> set, int threads, int keysPerThread)
            throws InterruptedException {
        // TODO:
        //   CountDownLatch start = new CountDownLatch(1);
        //   CountDownLatch done  = new CountDownLatch(threads);
        //   for (int t = 0; t < threads; t++) {
        //       final int base = t * keysPerThread;
        //       new Thread(() -> {
        //           try { start.await(); } catch (InterruptedException ignored) {}
        //           for (int i = 0; i < keysPerThread; i++) set.add(base + i);
        //           done.countDown();
        //       }).start();
        //   }
        //   long t0 = System.currentTimeMillis();
        //   start.countDown();
        //   done.await();
        //   return System.currentTimeMillis() - t0;
        throw new UnsupportedOperationException("Implement benchmarkAdd()");
    }

    /**
     * Benchmarks a mixed workload of reads and writes.
     *
     * Each thread performs {@code ops} operations. For each operation, a random
     * number decides: if {@code rand < readPct/100.0} → contains(); else alternate
     * between add() and remove().
     *
     * Pre-populates the set with {@code threads * ops / 2} keys so that
     * removes have something to remove.
     *
     * @param set     the skip list to benchmark
     * @param threads number of concurrent threads
     * @param ops     operations per thread
     * @param readPct percentage of operations that are reads (0–100)
     * @return elapsed time in milliseconds
     */
    public long benchmarkMixed(SkipListSet<Integer> set, int threads, int ops, int readPct)
            throws InterruptedException {
        // TODO:
        //   // Pre-populate
        //   int prepopulate = threads * ops / 2;
        //   for (int i = 0; i < prepopulate; i++) set.add(i);
        //
        //   CountDownLatch start = new CountDownLatch(1);
        //   CountDownLatch done  = new CountDownLatch(threads);
        //   for (int t = 0; t < threads; t++) {
        //       final int tid = t;
        //       new Thread(() -> {
        //           try { start.await(); } catch (InterruptedException ignored) {}
        //           Random rng = new Random(tid);
        //           for (int i = 0; i < ops; i++) {
        //               int key = rng.nextInt(prepopulate * 2);
        //               if (rng.nextInt(100) < readPct) {
        //                   set.contains(key);
        //               } else if (rng.nextBoolean()) {
        //                   set.add(key);
        //               } else {
        //                   set.remove(key);
        //               }
        //           }
        //           done.countDown();
        //       }).start();
        //   }
        //   long t0 = System.currentTimeMillis();
        //   start.countDown();
        //   done.await();
        //   return System.currentTimeMillis() - t0;
        throw new UnsupportedOperationException("Implement benchmarkMixed()");
    }
}
