package com.concurrency.advanced.p40;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=10, unit=TimeUnit.SECONDS)
class ObjectPoolTest {
    @Test void borrowAndReturn() throws Exception {
        var pool = new ObjectPool<>(3, ()->"obj");
        var obj = pool.borrow(); assertNotNull(obj); assertEquals(2, pool.availableCount());
        pool.returnObject(obj); assertEquals(3, pool.availableCount());
    }
    @Test void borrowBlocksWhenEmpty() throws Exception {
        var pool = new ObjectPool<>(1, ()->"x");
        var obj = pool.borrow();
        var borrowed = new AtomicInteger(0);
        var t = new Thread(()->{ try{ pool.borrow(); borrowed.incrementAndGet(); }catch(Exception e){} });
        t.start(); Thread.sleep(100);
        assertEquals(0, borrowed.get());
        pool.returnObject(obj); t.join(2000); assertEquals(1, borrowed.get());
    }
    @Test void timedBorrowReturnsNullOnTimeout() throws Exception {
        var pool = new ObjectPool<>(1, ()->"x"); pool.borrow();
        assertNull(pool.borrow(100));
    }
    @Test void pooledResourceAutoReturns() throws Exception {
        var pool = new ObjectPool<>(2, ()->"r");
        try (var r = pool.borrowResource()) { assertEquals("r", r.get()); assertEquals(1, pool.availableCount()); }
        assertEquals(2, pool.availableCount());
    }
    @Test void shutdownPreventsNewBorrows() throws Exception {
        var pool = new ObjectPool<>(2, ()->"x"); pool.shutdown();
        assertThrows(IllegalStateException.class, pool::borrow);
    }
    @Test void concurrentBorrowReturn() throws Exception {
        var pool = new ObjectPool<>(5, AtomicInteger::new);
        int threads=20; var done = new CountDownLatch(threads); var errors = new CopyOnWriteArrayList<Throwable>();
        for (int i=0;i<threads;i++) new Thread(()->{ try{ for(int j=0;j<50;j++){ var o=pool.borrow(); o.incrementAndGet(); Thread.sleep(1); pool.returnObject(o); } }catch(Throwable e){errors.add(e);}finally{done.countDown();} }).start();
        done.await(); assertTrue(errors.isEmpty()); assertEquals(5, pool.availableCount());
    }
}
