package com.concurrency.intermediate.p25;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Problem 25 – ConcurrentHashMap: Word Frequency Counter
 *
 * Counts word frequencies across many documents processed in parallel.
 * Uses ConcurrentHashMap.merge() for lock-free atomic increments.
 */
public class WordFrequencyCounter {

    private final ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
    private final ExecutorService executor;

    public WordFrequencyCounter(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
    }

    /**
     * Splits text by whitespace and increments the count for each word.
     * Must be thread-safe. Use map.merge(word, 1, Integer::sum).
     */
    public void addDocument(String text) {
        // TODO:
        //   String[] words = text.split("\\s+");
        //   for each non-blank word: counts.merge(word.toLowerCase(), 1, Integer::sum)
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Submits each document in the list to the executor in parallel.
     * Blocks until all documents have been processed.
     */
    public void processDocuments(List<String> documents) throws InterruptedException {
        // TODO: submit each document's addDocument call to executor,
        //       collect futures, then wait for all with future.get()
        throw new UnsupportedOperationException("Implement this method");
    }

    /** Returns the frequency of the given word (0 if not seen). */
    public int getCount(String word) {
        return counts.getOrDefault(word.toLowerCase(), 0);
    }

    /**
     * Returns the N most frequent words in descending order.
     * Ties broken alphabetically (ascending).
     */
    public List<String> getTopN(int n) {
        // TODO: stream entrySet, sort by value DESC then key ASC, limit n, map to keys
        throw new UnsupportedOperationException("Implement this method");
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}
