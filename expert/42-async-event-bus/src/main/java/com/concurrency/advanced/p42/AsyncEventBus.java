package com.concurrency.advanced.p42;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
/**
 * Problem 42 – Async Event Bus (Publish-Subscribe)
 * ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Subscription>> registry.
 *
 * TODO subscribe(type,handler):  create Subscription; registry.computeIfAbsent; return sub
 * TODO post(event):              get subs for event.getClass(); executor.submit(()->sub.invoke(event))
 * TODO postToAll(event):         walk class hierarchy; dispatch to all matching subscribers
 * TODO unsubscribe(sub):         subs.removeIf(s->s.id==sub.id)
 * TODO shutdown():               executor.shutdown(); awaitTermination(5s)
 * TODO awaitQuiescence(ms):      executor.submit(()->{}).get(ms,MILLISECONDS)
 */
public class AsyncEventBus {
    @FunctionalInterface public interface ExceptionHandler { void handle(Object event, Consumer<?> sub, Throwable err); }
    public static class Subscription {
        private static final AtomicLong SEQ = new AtomicLong(0);
        final long id; final Class<?> eventType; final Consumer<?> handler;
        @SuppressWarnings("unchecked")
        Subscription(Class<?> t, Consumer<?> h) { id=SEQ.getAndIncrement(); eventType=t; handler=h; }
        @SuppressWarnings("unchecked") <T> void invoke(T e) { ((Consumer<T>)handler).accept(e); }
    }
    private final ExecutorService exec;
    private final ConcurrentHashMap<Class<?>,CopyOnWriteArrayList<Subscription>> registry = new ConcurrentHashMap<>();
    private volatile ExceptionHandler exceptionHandler = (e,s,t)->System.err.println("EventBus error: "+t);
    public AsyncEventBus(int threads) { this.exec = Executors.newFixedThreadPool(threads); }
    public <T> Subscription subscribe(Class<T> type, Consumer<T> h) { throw new UnsupportedOperationException("Implement subscribe()"); }
    public void post(Object event)                                   { throw new UnsupportedOperationException("Implement post()"); }
    public void postToAll(Object event)                              { throw new UnsupportedOperationException("Implement postToAll()"); }
    public void unsubscribe(Subscription s)                          { throw new UnsupportedOperationException("Implement unsubscribe()"); }
    public int subscriberCount(Class<?> t) { var l=registry.get(t); return l==null?0:l.size(); }
    public void setExceptionHandler(ExceptionHandler h) { this.exceptionHandler=h; }
    public void shutdown() throws InterruptedException               { throw new UnsupportedOperationException("Implement shutdown()"); }
    public void awaitQuiescence(long ms) throws InterruptedException { throw new UnsupportedOperationException("Implement awaitQuiescence()"); }
    private Set<Class<?>> hierarchy(Class<?> t) {
        Set<Class<?>> r=new LinkedHashSet<>(); Queue<Class<?>> q=new ArrayDeque<>(); q.add(t);
        while(!q.isEmpty()){Class<?> c=q.poll(); if(c==null||!r.add(c)) continue; if(c.getSuperclass()!=null) q.add(c.getSuperclass()); Collections.addAll(q,c.getInterfaces());} return r;
    }
}
