package com.concurrency.advanced.p36;
import java.util.*;
import java.util.concurrent.*;
/**
 * Problem 36 – Reactive Streams: BufferingSubscriber
 * Collects all items; requests Long.MAX_VALUE upfront.
 *
 * TODO onSubscribe: store subscription; call request(Long.MAX_VALUE).
 * TODO onNext:      add item to list.
 * TODO onComplete:  mark done; count down latch.
 * TODO onError:     store error; count down latch.
 * TODO awaitCompletion(ms): latch.await(ms, MILLISECONDS).
 */
public class BufferingSubscriber<T> implements Flow.Subscriber<T> {
    private final List<T>      items = new ArrayList<>();
    private final CountDownLatch done  = new CountDownLatch(1);
    private Flow.Subscription  sub;
    private Throwable          error;
    private volatile boolean   complete;
    @Override public void onSubscribe(Flow.Subscription s)  { throw new UnsupportedOperationException("Implement onSubscribe()"); }
    @Override public void onNext(T item)                    { throw new UnsupportedOperationException("Implement onNext()"); }
    @Override public void onComplete()                      { throw new UnsupportedOperationException("Implement onComplete()"); }
    @Override public void onError(Throwable t)              { throw new UnsupportedOperationException("Implement onError()"); }
    public boolean awaitCompletion(long ms) throws InterruptedException { throw new UnsupportedOperationException("Implement awaitCompletion()"); }
    public List<T> getItems()   { return List.copyOf(items); }
    public boolean isComplete() { return complete; }
    public boolean isError()    { return error != null; }
    public Throwable getError() { return error; }
    public void cancel()        { if (sub != null) sub.cancel(); }
}
