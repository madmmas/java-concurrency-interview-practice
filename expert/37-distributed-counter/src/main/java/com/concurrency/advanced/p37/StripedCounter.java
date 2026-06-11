package com.concurrency.advanced.p37;
import java.util.concurrent.atomic.AtomicLong;
/**
 * Problem 37 – Distributed Counter: Striped Counter
 * Reduces CAS contention by distributing increments across AtomicLong[] stripes.
 * Stripe selection: Thread.currentThread().getId() % stripes.
 *
 * TODO increment(): cells[stripeIndex()].incrementAndGet()
 * TODO add(delta):  cells[stripeIndex()].addAndGet(delta)
 * TODO sum():       sum all cells
 * TODO reset():     set all cells to 0
 */
public class StripedCounter {
    private final AtomicLong[] cells;
    private final int stripes;
    public StripedCounter(int stripes) {
        if (stripes <= 0) throw new IllegalArgumentException("stripes must be > 0");
        this.stripes = stripes;
        this.cells   = new AtomicLong[stripes];
        for (int i = 0; i < stripes; i++) cells[i] = new AtomicLong(0);
    }
    public void increment()      { throw new UnsupportedOperationException("Implement increment()"); }
    public void add(long delta)  { throw new UnsupportedOperationException("Implement add()"); }
    public long sum()            { throw new UnsupportedOperationException("Implement sum()"); }
    public void reset()          { throw new UnsupportedOperationException("Implement reset()"); }
    public int getStripeCount()  { return stripes; }
    private int stripeIndex()    { return (int)(Thread.currentThread().getId() % stripes); }
}
