package com.concurrency.advanced.p48;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=10, unit=TimeUnit.SECONDS)
class ThreadConfinementTest {
    @Test void sameThreadGetsSameInstance() {
        var pool=new ConfinedResourcePool<>(ArrayList::new);
        assertSame(pool.get(), pool.get());
    }
    @Test void differentThreadsDifferentInstances() throws Exception {
        var pool=new ConfinedResourcePool<>(ArrayList::new);
        var main=pool.get(); var other=new AtomicReference<Object>();
        var t=new Thread(()->other.set(pool.get())); t.start(); t.join();
        assertNotSame(main, other.get());
    }
    @Test void instanceCountPerThread() throws Exception {
        var pool=new ConfinedResourcePool<>(StringBuilder::new);
        var done=new CountDownLatch(5);
        for(int i=0;i<5;i++) new Thread(()->{ pool.get(); done.countDown(); }).start();
        done.await(); assertEquals(5, pool.getInstanceCount());
    }
    @Test void removeCreatesNewOnNextGet() {
        var pool=new ConfinedResourcePool<>(ArrayList::new);
        var a=pool.get(); pool.remove(); var b=pool.get();
        assertNotSame(a,b); assertEquals(2,pool.getInstanceCount());
    }
    @Test void dateFormatterRoundTrip() throws Exception {
        var fmt=new DateFormatterPool("yyyy-MM-dd");
        var d=new SimpleDateFormat("yyyy-MM-dd").parse("2024-06-15");
        assertEquals(d, fmt.parse(fmt.format(d)));
    }
    @Test void dateFormatterConcurrentNeverCorrupts() throws Exception {
        var fmt=new DateFormatterPool("yyyy-MM-dd");
        var errors=new CopyOnWriteArrayList<Throwable>(); var done=new CountDownLatch(20);
        for(int t=0;t<20;t++){final int d=t+1; new Thread(()->{ try{ var date=new SimpleDateFormat("yyyy-MM-dd").parse("2024-01-"+String.format("%02d",d)); assertEquals(date,fmt.parse(fmt.format(date))); }catch(Throwable e){errors.add(e);}finally{done.countDown();} }).start();}
        done.await(); assertTrue(errors.isEmpty());
    }
}
