package com.concurrency.advanced.p41;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=10, unit=TimeUnit.SECONDS)
class CustomBarrierTest {
    @Test void allPartiesRelease() throws Exception {
        var b = new CustomBarrier(4); var latch = new CountDownLatch(4);
        for (int i=0;i<4;i++) new Thread(()->{ try{b.await();latch.countDown();}catch(Exception e){} }).start();
        assertTrue(latch.await(5,TimeUnit.SECONDS));
    }
    @Test void barrierActionRunsOnce() throws Exception {
        var count = new AtomicInteger(0);
        var b = new CustomBarrier(3, count::incrementAndGet);
        var done = new CountDownLatch(3);
        for (int i=0;i<3;i++) new Thread(()->{ try{b.await();done.countDown();}catch(Exception e){} }).start();
        done.await(); assertEquals(1,count.get());
    }
    @Test void reusableAcrossGenerations() throws Exception {
        var b = new CustomBarrier(2); var done = new CountDownLatch(2);
        for (int t=0;t<2;t++) new Thread(()->{ try{ for(int g=0;g<3;g++) b.await(); done.countDown(); }catch(Exception e){} }).start();
        assertTrue(done.await(8,TimeUnit.SECONDS));
    }
    @Test void resetBreaksWaiters() throws Exception {
        var b = new CustomBarrier(3); var broken = new CountDownLatch(1);
        new Thread(()->{ try{b.await();}catch(BrokenBarrierException e){broken.countDown();}catch(Exception e){} }).start();
        Thread.sleep(100); b.reset();
        assertTrue(broken.await(2,TimeUnit.SECONDS));
    }
    @Test void timedAwaitThrows() {
        var b = new CustomBarrier(3);
        assertThrows(TimeoutException.class, ()->b.await(150));
    }
}
