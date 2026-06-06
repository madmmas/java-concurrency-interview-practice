/**
 * Problem 01 — Thread Basics
 * Implement the methods below. Do not modify method signatures.
 */
public class ThreadBasics {

    /**
     * Creates a Thread from the given Runnable, starts it, and returns it.
     */
    public Thread createAndStartThread(Runnable task) {
        Thread thread = new Thread(task);
        thread.start();
        return thread;
    }

    /**
     * Creates (but does NOT start) a Thread by extending Thread anonymously.
     * The thread should print "Hello from <name>" when run.
     */
    public Thread extendThread(String name) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello from " + Thread.currentThread().getName());
            }
        }, name);
        return thread;
    }

    /**
     * Returns thread info as "name=<name>,state=<state>,daemon=<isDaemon>"
     */
    public String getThreadInfo(Thread t) {
        return String.format("name=%s,state=%s,daemon=%s",t.getName(), t.getState(), t.isDaemon());
    }

    /**
     * Returns the number of active threads in the current thread group.
     */
    public int countActiveThreads() {
        return Thread.currentThread().getThreadGroup().activeCount();
    }
}
