package com.concurrency.advanced.p26;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Problem 26 – Lock-Free Data Structures: ABA Demonstrator
 *
 * Shows that a naive CAS can succeed incorrectly (ABA problem), and that
 * AtomicStampedReference prevents it by pairing the value with a version stamp.
 */
public class ABADemonstrator {

    /**
     * Demonstrates the ABA problem using a plain AtomicReference<String>.
     *
     * Scenario (single-threaded simulation of a two-thread race):
     *  1. ref starts at "A"
     *  2. Thread 1 reads ref → expected = "A"
     *  3. Thread 2 changes ref: "A" → "B" → "A"  (ABA sequence)
     *  4. Thread 1 CAS(expected="A", update="C") — succeeds because ref is
     *     back to "A", even though it was secretly modified in between
     *
     * @return true if the CAS succeeded (demonstrating the ABA problem)
     */
    public boolean demonstrateABA() {
        // TODO:
        //   AtomicReference<String> ref = new AtomicReference<>("A");
        //   String expected = ref.get();               // Thread 1 snapshots "A"
        //   ref.set("B");                              // Thread 2: A → B
        //   ref.set("A");                              // Thread 2: B → A  (ABA!)
        //   return ref.compareAndSet(expected, "C");   // Thread 1 CAS — should it succeed?
        throw new UnsupportedOperationException("Implement demonstrateABA()");
    }

    /**
     * Shows how AtomicStampedReference defeats ABA.
     *
     * Scenario:
     *  1. stampedRef = ("A", stamp=0)
     *  2. Thread 1 reads value="A", stamp=0
     *  3. Thread 2 does ABA with stamp increments:
     *       ("A",0) → ("B",1) → ("A",2)
     *  4. Thread 1 CAS(expectedVal="A", expectedStamp=0, newVal="C", newStamp=1)
     *     → FAILS because stamp is now 2, not 0
     *
     * @return false — the stamped CAS correctly rejected the spurious update
     */
    public boolean demonstrateABAFix() {
        // TODO:
        //   AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);
        //   int[] stampHolder = new int[1];
        //   String expected      = ref.get(stampHolder);
        //   int    expectedStamp = stampHolder[0];            // Thread 1 snapshots stamp=0
        //
        //   // Thread 2 ABA sequence (stamp increments each time)
        //   ref.compareAndSet("A", "B", 0, 1);
        //   ref.compareAndSet("B", "A", 1, 2);
        //
        //   // Thread 1 CAS — stamp 0 ≠ current stamp 2 → must FAIL
        //   return ref.compareAndSet(expected, "C", expectedStamp, expectedStamp + 1);
        throw new UnsupportedOperationException("Implement demonstrateABAFix()");
    }
}
