package com.concurrency.advanced.p36;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.*;
/**
 * Problem 36 – Reactive Streams: SimplePublisher
 * Publishes a fixed List of items honouring back-pressure via request(n).
 *
 * TODO subscribe(): create a SimpleSubscription and call subscriber.onSubscribe(it).
 * TODO request(n): saturating-add n to demand AtomicLong, then drain().
 * TODO drain():    while demand>0 && index<size && !cancelled → onNext; when exhausted → onComplete.
 * TODO cancel():   set cancelled flag; no further subscriber calls.
 */
public class SimplePublisher<T> implements Flow.Publisher<T> {
    private final List<T> items;
    public SimplePublisher(List<T> items) { this.items = List.copyOf(items); }
    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {
        throw new UnsupportedOperationException("Implement subscribe()");
    }
    private class SimpleSubscription implements Flow.Subscription {
        private final Flow.Subscriber<? super T> sub;
        private final AtomicInteger idx    = new AtomicInteger(0);
        private final AtomicLong    demand = new AtomicLong(0);
        private final AtomicBoolean done   = new AtomicBoolean(false);
        SimpleSubscription(Flow.Subscriber<? super T> s) { sub = s; }
        @Override public void request(long n) { throw new UnsupportedOperationException("Implement request()"); }
        @Override public void cancel()        { throw new UnsupportedOperationException("Implement cancel()"); }
        private void drain()                  { throw new UnsupportedOperationException("Implement drain()"); }
    }
}
