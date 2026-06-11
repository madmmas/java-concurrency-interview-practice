package com.concurrency.advanced.p36;
import java.util.concurrent.Flow;
import java.util.function.Function;
/**
 * Problem 36 – Reactive Streams: TransformProcessor
 * Mid-stream transformer applying Function<T,R> to each item.
 *
 * TODO subscribe(downstream): store downstream; give it a forwarding Subscription.
 * TODO onSubscribe(upSub):    store upstream subscription.
 * TODO onNext(item):          downstream.onNext(transform.apply(item)).
 * TODO onComplete/onError:    forward to downstream.
 */
public class TransformProcessor<T, R> implements Flow.Processor<T, R> {
    private final Function<T, R>        transform;
    private Flow.Subscription            upSub;
    private Flow.Subscriber<? super R>  downstream;
    public TransformProcessor(Function<T, R> transform) { this.transform = transform; }
    @Override public void subscribe(Flow.Subscriber<? super R> s) { throw new UnsupportedOperationException("Implement subscribe()"); }
    @Override public void onSubscribe(Flow.Subscription s)        { throw new UnsupportedOperationException("Implement onSubscribe()"); }
    @Override public void onNext(T item)                          { throw new UnsupportedOperationException("Implement onNext()"); }
    @Override public void onComplete()                            { throw new UnsupportedOperationException("Implement onComplete()"); }
    @Override public void onError(Throwable t)                    { throw new UnsupportedOperationException("Implement onError()"); }
}
