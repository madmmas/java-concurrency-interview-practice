package com.concurrency.advanced.p35;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 15, unit = TimeUnit.SECONDS)
class ConcurrentSkipListTest {

    // ── Parameterised: run every test against both implementations ────────────

    static Stream<SkipListSet<Integer>> implementations() {
        return Stream.of(
                new ConcurrentSkipListSet<>(),
                new LockFreeSkipListSet<>()
        );
    }

    static Stream<String> implNames() {
        return Stream.of("ConcurrentSkipListSet (lock-based)",
                         "LockFreeSkipListSet (lock-free)");
    }

    // ── Single-threaded correctness ───────────────────────────────────────────

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void addReturnsTrueForNewKey(SkipListSet<Integer> set) {
        assertTrue(set.add(42), "add() must return true for a new key");
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void addReturnsFalseForDuplicateKey(SkipListSet<Integer> set) {
        set.add(42);
        assertFalse(set.add(42), "add() must return false for a duplicate key");
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void containsReturnsTrueAfterAdd(SkipListSet<Integer> set) {
        set.add(10);
        assertTrue(set.contains(10));
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void containsReturnsFalseForAbsentKey(SkipListSet<Integer> set) {
        set.add(10);
        assertFalse(set.contains(99));
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void removeReturnsTrueForPresentKey(SkipListSet<Integer> set) {
        set.add(5);
        assertTrue(set.remove(5), "remove() must return true for an existing key");
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void removeReturnsFalseForAbsentKey(SkipListSet<Integer> set) {
        assertFalse(set.remove(99), "remove() must return false for a missing key");
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void containsReturnsFalseAfterRemove(SkipListSet<Integer> set) {
        set.add(7);
        set.remove(7);
        assertFalse(set.contains(7), "contains() must return false after remove()");
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void sizeTracksAddAndRemove(SkipListSet<Integer> set) {
        assertEquals(0, set.size());
        set.add(1); set.add(2); set.add(3);
        assertEquals(3, set.size());
        set.remove(2);
        assertEquals(2, set.size());
        set.add(2);   // re-insert
        assertEquals(3, set.size());
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void toSortedListReturnsSortedOrder(SkipListSet<Integer> set) {
        int[] vals = {50, 10, 90, 30, 70, 20};
        for (int v : vals) set.add(v);
        List<Integer> sorted = set.toSortedList();
        List<Integer> expected = List.of(10, 20, 30, 50, 70, 90);
        assertEquals(expected, sorted, "toSortedList() must return ascending order");
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void toSortedListExcludesRemovedKeys(SkipListSet<Integer> set) {
        set.add(1); set.add(2); set.add(3); set.add(4);
        set.remove(2); set.remove(4);
        assertEquals(List.of(1, 3), set.toSortedList());
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void firstAndLastReturnExtremes(SkipListSet<Integer> set) {
        set.add(40); set.add(10); set.add(70); set.add(25);
        assertEquals(10, set.first(), "first() must return the minimum");
        assertEquals(70, set.last(),  "last() must return the maximum");
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void firstAndLastThrowOnEmptySet(SkipListSet<Integer> set) {
        assertThrows(NoSuchElementException.class, set::first);
        assertThrows(NoSuchElementException.class, set::last);
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void addRemoveSequenceIsCorrect(SkipListSet<Integer> set) {
        for (int i = 1; i <= 100; i++) assertTrue(set.add(i));
        for (int i = 1; i <= 100; i += 2) assertTrue(set.remove(i)); // remove odds
        assertEquals(50, set.size());
        for (int i = 2; i <= 100; i += 2) assertTrue(set.contains(i),  "Even " + i + " must be present");
        for (int i = 1; i <= 100; i += 2) assertFalse(set.contains(i), "Odd "  + i + " must be absent");
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void addManyElementsAndVerifySorted(SkipListSet<Integer> set) {
        List<Integer> input = new ArrayList<>();
        for (int i = 0; i < 200; i++) input.add(i);
        Collections.shuffle(input, new Random(42));
        input.forEach(set::add);

        List<Integer> sorted = set.toSortedList();
        assertEquals(200, sorted.size());
        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i) < sorted.get(i + 1),
                    "toSortedList() must be strictly ascending at index " + i);
        }
    }

    // ── Concurrent correctness ────────────────────────────────────────────────

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void concurrentAddsAllSucceed(SkipListSet<Integer> set) throws InterruptedException {
        int threads = 8, keysPerThread = 500;
        CountDownLatch go   = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            final int base = t * keysPerThread;
            new Thread(() -> {
                try { go.await(); } catch (InterruptedException ignored) {}
                for (int i = 0; i < keysPerThread; i++) set.add(base + i);
                done.countDown();
            }).start();
        }
        go.countDown();
        done.await();

        int expected = threads * keysPerThread;
        assertEquals(expected, set.size(),
                "All " + expected + " concurrent inserts must be reflected in size()");
        assertEquals(expected, set.toSortedList().size(),
                "toSortedList() must contain all " + expected + " unique keys");
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void concurrentRemovesNeverRemoveSameElementTwice(SkipListSet<Integer> set)
            throws InterruptedException {
        int n = 1000;
        for (int i = 0; i < n; i++) set.add(i);

        AtomicInteger successfulRemovals = new AtomicInteger(0);
        int threads = 8;
        CountDownLatch go   = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            new Thread(() -> {
                try { go.await(); } catch (InterruptedException ignored) {}
                for (int i = 0; i < n; i++) {
                    if (set.remove(i)) successfulRemovals.incrementAndGet();
                }
                done.countDown();
            }).start();
        }
        go.countDown();
        done.await();

        assertEquals(n, successfulRemovals.get(),
                "Each of the " + n + " keys must be removed exactly once across all threads");
        assertEquals(0, set.size(), "All elements must be removed; size must be 0");
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void concurrentMixedOpsAreLinearisable(SkipListSet<Integer> set)
            throws InterruptedException {
        // Multiple threads add/remove disjoint key ranges — no key is shared.
        // After completion, each thread's adds must be present (removes are absent).
        int threads = 6, keysPerThread = 200;
        CountDownLatch go   = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            final int base = t * keysPerThread;
            new Thread(() -> {
                try { go.await(); } catch (InterruptedException ignored) {}
                for (int i = 0; i < keysPerThread; i++) set.add(base + i);
                for (int i = 0; i < keysPerThread / 2; i++) set.remove(base + i);
                done.countDown();
            }).start();
        }
        go.countDown();
        done.await();

        int expectedSize = threads * (keysPerThread - keysPerThread / 2);
        assertEquals(expectedSize, set.size(),
                "After disjoint add+remove, size must equal total remaining elements");
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource("implementations")
    void concurrentContainsNeverThrows(SkipListSet<Integer> set) throws InterruptedException {
        int n = 500;
        for (int i = 0; i < n; i++) set.add(i);

        int threads = 10;
        CountDownLatch done = new CountDownLatch(threads);
        List<Throwable> errors = new CopyOnWriteArrayList<>();

        for (int t = 0; t < threads; t++) {
            new Thread(() -> {
                try {
                    Random rng = new Random();
                    for (int i = 0; i < 1000; i++) set.contains(rng.nextInt(n * 2));
                } catch (Throwable e) {
                    errors.add(e);
                } finally {
                    done.countDown();
                }
            }).start();
        }
        done.await();
        assertTrue(errors.isEmpty(),
                "contains() must never throw under concurrent access; errors: " + errors);
    }

    // ── Benchmark harness (correctness only — not performance assertions) ─────

    @Test
    void benchmarkAddCompletesWithoutError() throws InterruptedException {
        SkipListBenchmark bench = new SkipListBenchmark();
        long ms = bench.benchmarkAdd(new ConcurrentSkipListSet<>(), 4, 500);
        assertTrue(ms >= 0, "benchmarkAdd must complete and return non-negative ms");
    }

    @Test
    void benchmarkMixedCompletesWithoutError() throws InterruptedException {
        SkipListBenchmark bench = new SkipListBenchmark();
        long ms = bench.benchmarkMixed(new ConcurrentSkipListSet<>(), 4, 500, 70);
        assertTrue(ms >= 0, "benchmarkMixed must complete and return non-negative ms");
    }

    @Test
    void benchmarkComparesLockBasedVsLockFree() throws InterruptedException {
        SkipListBenchmark bench = new SkipListBenchmark();
        long lockBased = bench.benchmarkAdd(new ConcurrentSkipListSet<>(), 4, 1000);
        long lockFree  = bench.benchmarkAdd(new LockFreeSkipListSet<>(),   4, 1000);
        // Both must complete without error or deadlock; no timing assertion needed
        assertTrue(lockBased >= 0 && lockFree >= 0,
                "Both implementations must complete the benchmark without deadlock");
    }

    // ── Java's built-in ConcurrentSkipListMap comparison ─────────────────────

    @Test
    void resultMatchesJavaBuiltInForLockBased() {
        ConcurrentSkipListSet<Integer> ourSet = new ConcurrentSkipListSet<>();
        java.util.concurrent.ConcurrentSkipListMap<Integer, Boolean> javaMap =
                new java.util.concurrent.ConcurrentSkipListMap<>();

        Random rng = new Random(123);
        List<Integer> ops = new ArrayList<>();
        for (int i = 0; i < 200; i++) ops.add(rng.nextInt(100));
        for (int k : ops) { ourSet.add(k); javaMap.put(k, true); }

        List<Integer> ourList  = ourSet.toSortedList();
        List<Integer> javaList = new ArrayList<>(javaMap.keySet());
        assertEquals(javaList, ourList,
                "Our skip list must produce the same sorted key set as Java's built-in");
    }

    @Test
    void resultMatchesJavaBuiltInForLockFree() {
        LockFreeSkipListSet<Integer> ourSet = new LockFreeSkipListSet<>();
        java.util.concurrent.ConcurrentSkipListMap<Integer, Boolean> javaMap =
                new java.util.concurrent.ConcurrentSkipListMap<>();

        Random rng = new Random(456);
        List<Integer> ops = new ArrayList<>();
        for (int i = 0; i < 200; i++) ops.add(rng.nextInt(100));
        for (int k : ops) { ourSet.add(k); javaMap.put(k, true); }

        List<Integer> ourList  = ourSet.toSortedList();
        List<Integer> javaList = new ArrayList<>(javaMap.keySet());
        assertEquals(javaList, ourList,
                "Our lock-free skip list must produce the same sorted key set as Java's built-in");
    }
}
