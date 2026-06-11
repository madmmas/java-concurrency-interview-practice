package com.concurrency.advanced.p27;

import java.util.concurrent.CountDownLatch;

/**
 * Problem 27 – JMM: Happens-Before Chain
 *
 * Demonstrates that happens-before is transitive across a chain of N threads.
 *
 * Thread[0]:   writes values[0] to volatile handoff → countDown(latch[0])
 * Thread[1]:   await(latch[0]) → reads handoff → writes values[1] → countDown(latch[1])
 * ...
 * Thread[N-1]: await(latch[N-2]) → accumulates sum → countDown(latch[N-1])
 *
 * Because volatile-write HB volatile-read, and countDown HB await (CountDownLatch),
 * each thread sees all writes made by all prior threads in the chain.
 * By transitivity, Thread[N-1] sees every value written from Thread[0] onwards.
 */
public class HappensBeforeChain {

    private volatile long handoff = 0;   // carries values down the chain
    private volatile long result  = 0;   // final accumulated result

    /**
     * Runs a chain of values.length threads, each contributing one value.
     * Returns the sum of all values (which proves every write was visible).
     *
     * Implementation outline:
     *  1. N = values.length
     *  2. CountDownLatch[] latches = one per thread (each starts at 1)
     *  3. Thread[0]:
     *       handoff = values[0]
     *       latches[0].countDown()
     *  4. Thread[i] (1 ≤ i < N-1):
     *       latches[i-1].await()         // wait for predecessor
     *       handoff += values[i]         // volatile write — HB for Thread[i+1]
     *       latches[i].countDown()
     *  5. Thread[N-1]:
     *       latches[N-2].await()
     *       result = handoff + values[N-1]
     *       latches[N-1].countDown()
     *  6. Start all threads, latches[N-1].await() from caller, return result
     *
     * @param values  non-empty array; each value[i] is written by thread[i]
     * @return the sum of all values in the array
     */
    public long runChain(int[] values) throws InterruptedException {
        // TODO: implement the N-thread chain as described above
        throw new UnsupportedOperationException("Implement runChain()");
    }
}
