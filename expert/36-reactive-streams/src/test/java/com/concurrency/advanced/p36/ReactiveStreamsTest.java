package com.concurrency.advanced.p36;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=10, unit=TimeUnit.SECONDS)
class ReactiveStreamsTest {
    @Test void publisherDeliversAllItems() throws Exception {
        var pub = new SimplePublisher<>(List.of(1,2,3,4,5));
        var sub = new BufferingSubscriber<Integer>();
        pub.subscribe(sub);
        assertTrue(sub.awaitCompletion(3000));
        assertEquals(List.of(1,2,3,4,5), sub.getItems());
    }
    @Test void emptyPublisherCompletesImmediately() throws Exception {
        var pub = new SimplePublisher<>(List.of());
        var sub = new BufferingSubscriber<Integer>();
        pub.subscribe(sub);
        assertTrue(sub.awaitCompletion(3000));
        assertTrue(sub.isComplete() && sub.getItems().isEmpty());
    }
    @Test void backPressureIsHonoured() throws Exception {
        var pub = new SimplePublisher<>(List.of(1,2,3,4,5));
        var received = new AtomicInteger(0);
        pub.subscribe(new Flow.Subscriber<>() {
            public void onSubscribe(Flow.Subscription s) { s.request(2); }
            public void onNext(Integer i) { received.incrementAndGet(); }
            public void onComplete() {}
            public void onError(Throwable t) {}
        });
        Thread.sleep(200);
        assertEquals(2, received.get(), "Must not deliver more than requested");
    }
    @Test void processorTransformsItems() throws Exception {
        var pub  = new SimplePublisher<>(List.of(1,2,3));
        var proc = new TransformProcessor<Integer,String>(i -> "item-"+i);
        var sub  = new BufferingSubscriber<String>();
        pub.subscribe(proc);
        proc.subscribe(sub);
        assertTrue(sub.awaitCompletion(3000));
        assertEquals(List.of("item-1","item-2","item-3"), sub.getItems());
    }
    @Test void processorChaining() throws Exception {
        var pub     = new SimplePublisher<>(List.of(1,2,3));
        var double_ = new TransformProcessor<Integer,Integer>(x -> x*2);
        var toStr   = new TransformProcessor<Integer,String>(x -> "v"+x);
        var sub     = new BufferingSubscriber<String>();
        pub.subscribe(double_); double_.subscribe(toStr); toStr.subscribe(sub);
        assertTrue(sub.awaitCompletion(3000));
        assertEquals(List.of("v2","v4","v6"), sub.getItems());
    }
    @Test void onCompleteCalledExactlyOnce() throws Exception {
        var count = new AtomicInteger(0);
        new SimplePublisher<>(List.of(1,2)).subscribe(new Flow.Subscriber<>() {
            public void onSubscribe(Flow.Subscription s) { s.request(Long.MAX_VALUE); }
            public void onNext(Integer i) {}
            public void onComplete() { count.incrementAndGet(); }
            public void onError(Throwable t) {}
        });
        Thread.sleep(200);
        assertEquals(1, count.get());
    }
}
