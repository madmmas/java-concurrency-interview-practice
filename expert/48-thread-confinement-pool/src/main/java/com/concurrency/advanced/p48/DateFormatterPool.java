package com.concurrency.advanced.p48;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
/**
 * Problem 48 – Thread Confinement: DateFormatterPool + ConfinedWorkerPool
 *
 * DateFormatterPool: ConfinedResourcePool<SimpleDateFormat>
 *   TODO constructor: pool = new ConfinedResourcePool<>(()->new SimpleDateFormat(pattern))
 *   TODO format(date): pool.get().format(date)
 *   TODO parse(text):  pool.get().parse(text)
 *
 * ConfinedWorkerPool: each thread owns a private R initialized in ThreadFactory.
 *   TODO constructor: ThreadFactory sets threadLocal.set(factory.get()) in each new thread
 *   TODO submit(fn):  executor.submit(()->fn.apply(threadLocal.get()))
 */
public class DateFormatterPool {
    private final ConfinedResourcePool<SimpleDateFormat> pool;
    public DateFormatterPool(String pattern) { throw new UnsupportedOperationException("Implement constructor"); }
    public String format(Date d) { throw new UnsupportedOperationException("Implement format()"); }
    public Date parse(String s) throws ParseException { throw new UnsupportedOperationException("Implement parse()"); }
    public int getInstanceCount() { return pool.getInstanceCount(); }
}
class ConfinedWorkerPool<R> {
    private final ExecutorService executor;
    private final ThreadLocal<R> threadLocal = new ThreadLocal<>();
    public ConfinedWorkerPool(int threads, Supplier<R> factory) { throw new UnsupportedOperationException("Implement constructor"); }
    public <T> Future<T> submit(Function<R,T> fn) { throw new UnsupportedOperationException("Implement submit()"); }
    public void shutdown() throws InterruptedException { throw new UnsupportedOperationException("Implement shutdown()"); }
}
