package com.concurrency.advanced.p46;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=10, unit=TimeUnit.SECONDS)
class ConcurrentTrieTest {
    private ConcurrentTrie trie;
    @BeforeEach void setUp() { trie=new ConcurrentTrie(); }
    @Test void insertAndSearch() {
        trie.insert("apple"); assertTrue(trie.search("apple")); assertFalse(trie.search("app")); assertFalse(trie.search("apples"));
    }
    @Test void startsWith() {
        trie.insert("apple"); assertTrue(trie.startsWith("app")); assertFalse(trie.startsWith("xyz"));
    }
    @Test void wordCountNoDuplicates() {
        trie.insert("a"); trie.insert("b"); trie.insert("a"); assertEquals(2,trie.wordCount());
    }
    @Test void delete() {
        trie.insert("cat"); trie.insert("car"); assertTrue(trie.delete("cat")); assertFalse(trie.search("cat")); assertTrue(trie.search("car"));
    }
    @Test void deleteReturnsFalseIfAbsent() {
        assertFalse(trie.delete("xyz"));
    }
    @Test void wordsWithPrefix() {
        trie.insert("apple"); trie.insert("app"); trie.insert("application"); trie.insert("banana");
        var words=trie.wordsWithPrefix("app");
        assertEquals(3,words.size()); assertTrue(words.containsAll(List.of("app","apple","application")));
    }
    @Test void clearRemovesAll() {
        trie.insert("x"); trie.insert("y"); trie.clear(); assertEquals(0,trie.wordCount()); assertFalse(trie.search("x"));
    }
    @Test void concurrentInserts() throws Exception {
        int threads=10, each=100; var done=new CountDownLatch(threads);
        for(int t=0;t<threads;t++){final int b=t*each; new Thread(()->{ for(int i=0;i<each;i++) trie.insert("w"+(b+i)); done.countDown(); }).start();}
        done.await(); assertEquals(threads*each,trie.wordCount());
    }
    @Test void concurrentSearchesNeverThrow() throws Exception {
        for(int i=0;i<100;i++) trie.insert("k"+i);
        var errors=new CopyOnWriteArrayList<Throwable>(); var done=new CountDownLatch(20);
        for(int t=0;t<20;t++) new Thread(()->{ try{ var rng=new Random(); for(int i=0;i<500;i++) trie.search("k"+rng.nextInt(200)); }catch(Throwable e){errors.add(e);}finally{done.countDown();} }).start();
        done.await(); assertTrue(errors.isEmpty());
    }
}
