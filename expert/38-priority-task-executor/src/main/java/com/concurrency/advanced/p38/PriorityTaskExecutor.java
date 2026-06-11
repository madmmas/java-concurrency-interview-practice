package com.concurrency.advanced.p38;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
/**
 * Problem 38 – Priority Task Executor
 * Backed by PriorityBlockingQueue; higher int priority runs first; equal priority → FIFO by seq.
 *
 * TODO constructor:   new ThreadPoolExecutor(threads,threads,0,MILLISECONDS,new PriorityBlockingQueue<>())
 * TODO execute(t,p):  executor.execute(new PriorityTask<>(t,p,SEQ.getAndIncrement(),completedCount))
 * TODO submit(c,p):   wrap in FutureTask, create PriorityTask, execute, return future
 * TODO PriorityTask.run():        future.run(); completedCount.incrementAndGet()
 * TODO PriorityTask.compareTo():  Integer.compare(other.priority,this.priority) then Long.compare(seq)
 */
public class PriorityTaskExecutor {
    private static final AtomicLong SEQ = new AtomicLong(0);
    private final AtomicLong completedCount = new AtomicLong(0);
    private final ThreadPoolExecutor executor;
    public PriorityTaskExecutor(int threads) { throw new UnsupportedOperationException("Implement constructor"); }
    public void execute(Runnable task, int priority) { throw new UnsupportedOperationException("Implement execute()"); }
    public <T> Future<T> submit(Callable<T> task, int priority) { throw new UnsupportedOperationException("Implement submit()"); }
    public void shutdown()                                     { executor.shutdown(); }
    public boolean awaitTermination(long ms) throws InterruptedException { return executor.awaitTermination(ms,TimeUnit.MILLISECONDS); }
    public long getCompletedCount() { return completedCount.get(); }
    public long getQueueSize()      { return executor.getQueue().size(); }

    static class PriorityTask<T> implements Runnable, Comparable<PriorityTask<?>> {
        private final FutureTask<T> future;
        private final int priority;
        private final long seq;
        private final AtomicLong completedCount;
        PriorityTask(Runnable r, int p, long s, AtomicLong c) { throw new UnsupportedOperationException("Implement PriorityTask(Runnable)"); }
        PriorityTask(Callable<T> c, int p, long s, AtomicLong cnt) { throw new UnsupportedOperationException("Implement PriorityTask(Callable)"); }
        @Override public void run() { throw new UnsupportedOperationException("Implement run()"); }
        FutureTask<T> getFuture() { return future; }
        @Override public int compareTo(PriorityTask<?> o) { throw new UnsupportedOperationException("Implement compareTo()"); }
    }
}
