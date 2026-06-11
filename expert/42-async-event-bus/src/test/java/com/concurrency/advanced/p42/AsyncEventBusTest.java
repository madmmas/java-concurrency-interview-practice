package com.concurrency.advanced.p42;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=10, unit=TimeUnit.SECONDS)
class AsyncEventBusTest {
    record UserCreated(String name){}
    record OrderPlaced(int id){}
    interface Auditable{}
    record AuditedOrder(int id) implements Auditable{}
    private AsyncEventBus bus;
    @BeforeEach void setUp()                    { bus = new AsyncEventBus(4); }
    @AfterEach  void tearDown() throws Exception{ bus.shutdown(); }
    @Test void subscriberReceivesEvent() throws Exception {
        var latch = new CountDownLatch(1);
        bus.subscribe(UserCreated.class, e->latch.countDown());
        bus.post(new UserCreated("Alice"));
        assertTrue(latch.await(3,TimeUnit.SECONDS));
    }
    @Test void multipleSubscribersAllReceive() throws Exception {
        var latch = new CountDownLatch(3);
        for(int i=0;i<3;i++) bus.subscribe(OrderPlaced.class, e->latch.countDown());
        bus.post(new OrderPlaced(1));
        assertTrue(latch.await(3,TimeUnit.SECONDS));
    }
    @Test void unsubscribeStopsDelivery() throws Exception {
        var count = new AtomicInteger(0);
        var sub = bus.subscribe(UserCreated.class, e->count.incrementAndGet());
        bus.post(new UserCreated("A")); bus.awaitQuiescence(2000);
        bus.unsubscribe(sub);
        bus.post(new UserCreated("B")); bus.awaitQuiescence(2000);
        assertEquals(1, count.get());
    }
    @Test void postToAllDeliveresToInterface() throws Exception {
        var latch = new CountDownLatch(2);
        bus.subscribe(AuditedOrder.class, e->latch.countDown());
        bus.subscribe(Auditable.class, e->latch.countDown());
        bus.postToAll(new AuditedOrder(1));
        assertTrue(latch.await(3,TimeUnit.SECONDS));
    }
    @Test void exceptionHandlerCalled() throws Exception {
        var latch = new CountDownLatch(1);
        bus.setExceptionHandler((e,s,t)->latch.countDown());
        bus.subscribe(UserCreated.class, e->{ throw new RuntimeException("oops"); });
        bus.post(new UserCreated("X"));
        assertTrue(latch.await(3,TimeUnit.SECONDS));
    }
    @Test void postIsNonBlocking() throws Exception {
        bus.subscribe(UserCreated.class, e->{ try{Thread.sleep(2000);}catch(Exception ex){} });
        long t=System.currentTimeMillis(); bus.post(new UserCreated("Z"));
        assertTrue(System.currentTimeMillis()-t < 500);
    }
}
