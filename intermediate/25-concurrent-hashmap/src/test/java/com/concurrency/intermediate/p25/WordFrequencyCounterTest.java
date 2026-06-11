package com.concurrency.intermediate.p25;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class WordFrequencyCounterTest {

    private WordFrequencyCounter counter;

    @BeforeEach void setUp()                     { counter = new WordFrequencyCounter(4); }
    @AfterEach  void tearDown() throws Exception { counter.shutdown(); }

    @Test
    void singleDocumentCounts() {
        counter.addDocument("the cat sat on the mat");
        assertEquals(2, counter.getCount("the"));
        assertEquals(1, counter.getCount("cat"));
        assertEquals(1, counter.getCount("sat"));
    }

    @Test
    void multipleDocumentsAccumulate() {
        counter.addDocument("hello world");
        counter.addDocument("hello java");
        assertEquals(2, counter.getCount("hello"));
        assertEquals(1, counter.getCount("world"));
        assertEquals(1, counter.getCount("java"));
    }

    @Test
    void caseInsensitive() {
        counter.addDocument("Hello HELLO hello");
        assertEquals(3, counter.getCount("hello"));
    }

    @Test
    void getTopNReturnsCorrectOrder() {
        counter.addDocument("a a a b b c");
        List<String> top = counter.getTopN(2);
        assertEquals(List.of("a", "b"), top, "Top 2 words must be 'a' (3) and 'b' (2)");
    }

    @Test
    void getTopNTiesAreAlphabetical() {
        counter.addDocument("apple banana cherry");
        List<String> top = counter.getTopN(3);
        assertEquals(List.of("apple", "banana", "cherry"), top,
                "All tied at 1 — must be sorted alphabetically");
    }

    @Test
    void parallelDocumentsProduceCorrectCounts() throws InterruptedException {
        List<String> docs = List.of(
                "a b c", "a b", "a",
                "d d d d", "d d"
        );
        counter.processDocuments(docs);
        assertEquals(3, counter.getCount("a"), "a appears 3 times across docs");
        assertEquals(2, counter.getCount("b"), "b appears 2 times");
        assertEquals(1, counter.getCount("c"), "c appears 1 time");
        assertEquals(6, counter.getCount("d"), "d appears 6 times");
    }

    @Test
    void missingWordReturnsZero() {
        assertEquals(0, counter.getCount("nothere"));
    }
}
