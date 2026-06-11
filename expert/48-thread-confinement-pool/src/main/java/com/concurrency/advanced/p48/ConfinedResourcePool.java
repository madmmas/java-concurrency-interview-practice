package com.concurrency.advanced.p48;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
/**
 * Problem 48 – Thread Confinement: ConfinedResourcePool
 * Each thread gets its own private resource via ThreadLocal.
 *
 * TODO constructor: ThreadLocal.withInitial(()->{ instanceCount.incrementAndGet(); return factory.get(); })
 * TODO get():       return threadLocal.get()
 * TODO remove():    threadLocal.remove()
 */
public class ConfinedResourcePool<R> {
    private final AtomicInteger instanceCount = new AtomicInteger(0);
    private final ThreadLocal<R> threadLocal;
    public ConfinedResourcePool(Supplier<R> factory) { throw new UnsupportedOperationException("Implement constructor"); }
    public R get()        { throw new UnsupportedOperationException("Implement get()"); }
    public void remove()  { throw new UnsupportedOperationException("Implement remove()"); }
    public int getInstanceCount() { return instanceCount.get(); }
}
