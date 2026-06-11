package com.concurrency.advanced.p39;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=10, unit=TimeUnit.SECONDS)
class ConcurrentLRUCacheTest {
    @Test void basicPutAndGet() {
        var c = new ConcurrentLRUCache<String,Integer>(3);
        c.put("a",1); c.put("b",2);
        assertEquals(1,c.get("a")); assertEquals(2,c.get("b")); assertNull(c.get("z"));
    }
    @Test void evictsLRUWhenFull() {
        var c = new ConcurrentLRUCache<String,Integer>(3);
        c.put("a",1); c.put("b",2); c.put("c",3);
        c.get("a"); // a=MRU, b=LRU
        c.put("d",4); // evicts b
        assertNull(c.get("b")); assertNotNull(c.get("a")); assertNotNull(c.get("d"));
    }
    @Test void sizeNeverExceedsCapacity() {
        var c = new ConcurrentLRUCache<Integer,Integer>(5);
        for (int i=0;i<20;i++) c.put(i,i);
        assertEquals(5, c.size());
    }
    @Test void manualEvict() {
        var c = new ConcurrentLRUCache<String,Integer>(5);
        c.put("x",1); c.evict("x"); assertFalse(c.containsKey("x"));
    }
    @Test void computeIfAbsentLoadOnce() {
        var c = new ConcurrentLRUCache<String,String>(5);
        var count = new AtomicInteger(0);
        var v1 = c.computeIfAbsent("k", k->{ count.incrementAndGet(); return "v"; });
        var v2 = c.computeIfAbsent("k", k->{ count.incrementAndGet(); return "v2"; });
        assertEquals("v",v1); assertEquals("v",v2); assertEquals(1,count.get());
    }
    @Test void lruOrderCorrect() {
        var c = new ConcurrentLRUCache<String,Integer>(3);
        c.put("a",1); c.put("b",2); c.put("c",3);
        c.get("a"); // a=MRU, b=LRU
        var order = c.getKeysLRUOrder();
        assertEquals("b", order.get(0)); assertEquals("a", order.get(2));
    }
    @Test void concurrentPutsSafe() throws Exception {
        var c = new ConcurrentLRUCache<Integer,Integer>(100);
        int threads=10, each=200;
        var done = new CountDownLatch(threads);
        var errors = new CopyOnWriteArrayList<Throwable>();
        for (int t=0;t<threads;t++) { final int base=t; new Thread(()->{ try{ for(int i=0;i<each;i++) c.put(base*1000+i,i); }catch(Throwable e){errors.add(e);}finally{done.countDown();} }).start(); }
        done.await(); assertTrue(errors.isEmpty()); assertTrue(c.size()<=100);
    }
}
