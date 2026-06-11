package com.concurrency.advanced.p38;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=10, unit=TimeUnit.SECONDS)
class PriorityTaskExecutorTest {
    private PriorityTaskExecutor exec;
    @BeforeEach void setUp()    { exec = new PriorityTaskExecutor(1); }
    @AfterEach  void tearDown() throws Exception { exec.shutdown(); exec.awaitTermination(3000); }
    @Test void highPriorityRunsFirst() throws Exception {
        var blocker = new CountDownLatch(1);
        exec.execute(()->{ try{blocker.await();}catch(Exception e){} }, 0);
        Thread.sleep(50);
        var order = new CopyOnWriteArrayList<String>();
        exec.execute(()->order.add("LOW"),    1);
        exec.execute(()->order.add("HIGH"),  10);
        exec.execute(()->order.add("MEDIUM"), 5);
        blocker.countDown(); exec.shutdown(); exec.awaitTermination(5000);
        assertEquals(List.of("HIGH","MEDIUM","LOW"), order);
    }
    @Test void equalPriorityFIFO() throws Exception {
        var blocker = new CountDownLatch(1);
        exec.execute(()->{ try{blocker.await();}catch(Exception e){} }, 0);
        Thread.sleep(50);
        var order = new CopyOnWriteArrayList<Integer>();
        for (int i=0;i<5;i++) { final int id=i; exec.execute(()->order.add(id), 5); }
        blocker.countDown(); exec.shutdown(); exec.awaitTermination(5000);
        assertEquals(List.of(0,1,2,3,4), order);
    }
    @Test void submitReturnsResult() throws Exception {
        assertEquals(42, exec.submit(()->42, 5).get(3,TimeUnit.SECONDS));
    }
    @Test void completedCountTracksAll() throws Exception {
        for (int i=0;i<10;i++) exec.execute(()->{}, i);
        exec.shutdown(); exec.awaitTermination(5000);
        assertEquals(10, exec.getCompletedCount());
    }
}
