package com.concurrency.advanced.p45;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=10, unit=TimeUnit.SECONDS)
class LockFreeRingBufferTest {
    @Test void offerAndPollFIFO() {
        var b=new LockFreeRingBuffer<Integer>(4);
        b.offer(1); b.offer(2); b.offer(3);
        assertEquals(1,b.poll()); assertEquals(2,b.poll()); assertEquals(3,b.poll()); assertNull(b.poll());
    }
    @Test void fullReturnsFalse() {
        var b=new LockFreeRingBuffer<Integer>(2);
        assertTrue(b.offer(1)); assertTrue(b.offer(2)); assertFalse(b.offer(3));
    }
    @Test void peekDoesNotRemove() {
        var b=new LockFreeRingBuffer<String>(4); b.offer("x");
        assertEquals("x",b.peek()); assertEquals("x",b.peek()); assertEquals(1,b.size());
    }
    @Test void wrapsAround() {
        var b=new LockFreeRingBuffer<Integer>(4);
        for(int i=0;i<4;i++) b.offer(i); for(int i=0;i<4;i++) b.poll();
        for(int i=10;i<14;i++) b.offer(i);
        assertEquals(10,b.poll()); assertEquals(11,b.poll());
    }
    @Test void mustBePowerOfTwo() {
        assertThrows(IllegalArgumentException.class, ()->new LockFreeRingBuffer<>(3));
    }
    @Test void spscConcurrent() throws Exception {
        var buf=new LockFreeRingBuffer<Integer>(1024);
        int total=50_000; var received=new ArrayList<Integer>();
        var done=new CountDownLatch(1);
        new Thread(()->{ while(received.size()<total){Integer v=buf.poll(); if(v!=null) received.add(v); else Thread.yield();} done.countDown(); }).start();
        for(int i=0;i<total;i++) { while(!buf.offer(i)) Thread.yield(); }
        assertTrue(done.await(8,TimeUnit.SECONDS));
        assertEquals(total,received.size());
        for(int i=0;i<total;i++) assertEquals(i,received.get(i));
    }
    @Test void mpscBasic() {
        var b=new MultiProducerRingBuffer<Integer>(8);
        b.offer(1); b.offer(2);
        assertNotNull(b.poll()); assertNotNull(b.poll()); assertNull(b.poll());
    }
    @Test void mpscConcurrent() throws Exception {
        var buf=new MultiProducerRingBuffer<Integer>(1024);
        int producers=4,each=2000,total=producers*each;
        var received=new HashSet<Integer>(); var done=new CountDownLatch(1);
        new Thread(()->{ while(received.size()<total){Integer v=buf.poll(); if(v!=null) received.add(v); else Thread.yield();} done.countDown(); }).start();
        var threads=new ArrayList<Thread>();
        for(int p=0;p<producers;p++){final int base=p*each; threads.add(new Thread(()->{ for(int i=0;i<each;i++) { while(!buf.offer(base+i)) Thread.yield(); } }));}
        threads.forEach(Thread::start); for(var t:threads) t.join();
        assertTrue(done.await(8,TimeUnit.SECONDS));
        assertEquals(total,received.size());
    }
}
