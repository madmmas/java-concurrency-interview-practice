package com.concurrency.advanced.p49;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=15, unit=TimeUnit.SECONDS)
class ParallelMergeSortTest {
    private ForkJoinPool pool; private ParallelMergeSortAdvanced<Integer> sorter;
    @BeforeEach void setUp()    { pool=new ForkJoinPool(); sorter=new ParallelMergeSortAdvanced<>(); }
    @AfterEach  void tearDown() { pool.shutdown(); }
    @Test void seqMergeCorrect() {
        Integer[] src={1,3,5,2,4,6}; Integer[] dest=new Integer[6];
        ParallelMergeSortAdvanced.sequentialMerge(src,0,3,6,dest,Integer::compareTo);
        assertArrayEquals(new Integer[]{1,2,3,4,5,6},dest);
    }
    @Test void sortEmpty()     { assertEquals(0, sorter.sort(new Integer[]{},Integer::compareTo,pool).length); }
    @Test void sortSingle()    { assertArrayEquals(new Integer[]{42}, sorter.sort(new Integer[]{42},Integer::compareTo,pool)); }
    @Test void sortReversed()  { assertArrayEquals(new Integer[]{1,2,3,4,5}, sorter.sort(new Integer[]{5,4,3,2,1},Integer::compareTo,pool)); }
    @Test void sortDuplicates() {
        Integer[] input={3,1,4,1,5,9,2,6,5,3}; Integer[] exp=input.clone(); Arrays.sort(exp);
        assertArrayEquals(exp, sorter.sort(input,Integer::compareTo,pool));
    }
    @Test void doesNotMutateInput() {
        Integer[] input={5,3,1}; Integer[] orig=input.clone();
        sorter.sort(input,Integer::compareTo,pool);
        assertArrayEquals(orig,input);
    }
    @Test void sortLargeRandom() {
        var rng=new Random(42); int n=100_000; Integer[] arr=new Integer[n];
        for(int i=0;i<n;i++) arr[i]=rng.nextInt(); Integer[] exp=arr.clone(); Arrays.sort(exp,Integer::compareTo);
        assertArrayEquals(exp, sorter.sort(arr,Integer::compareTo,pool));
    }
    @Test void sortDescending() {
        Integer[] input={3,1,4,1,5}; Integer[] exp=input.clone(); Arrays.sort(exp,Comparator.reverseOrder());
        assertArrayEquals(exp, sorter.sort(input,Comparator.reverseOrder(),pool));
    }
    @Test void binarySearchFindsFirstGe() {
        Integer[] arr={1,3,5,7,9};
        assertEquals(2, ParallelMergeSortAdvanced.binarySearch(arr,0,5,5,Integer::compareTo));
        assertEquals(5, ParallelMergeSortAdvanced.binarySearch(arr,0,5,10,Integer::compareTo));
    }
    @Test void adaptiveThresholdAtLeast1024() {
        assertTrue(ParallelMergeSortAdvanced.adaptiveThreshold(100)>=1024);
    }
}
