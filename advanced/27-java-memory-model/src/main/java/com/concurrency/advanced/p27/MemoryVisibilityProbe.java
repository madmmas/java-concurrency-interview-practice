package com.concurrency.advanced.p27;

/**
 * Problem 27 – JMM: Memory Visibility Probe
 *
 * Two inner classes that contrast the visibility bug (plain boolean) and
 * its fix (volatile boolean).
 *
 * BuggyVisibility:  worker may spin forever because the JVM can cache `running`
 *                   in a register and never re-read it from memory.
 * FixedVisibility:  volatile write forces a memory flush; volatile read always
 *                   fetches the freshest value → worker terminates promptly.
 */
public class MemoryVisibilityProbe {

    // ── Buggy version (educational — demonstrates the bug) ───────────────────

    public static class BuggyVisibility {

        // TODO: declare as plain boolean (NOT volatile)
        private boolean running = false;
        private Thread worker;

        /**
         * Sets running = true, then starts a worker thread that busy-loops
         * while (running) — the thread may never see running become false.
         */
        public void start() {
            // TODO:
            //   running = true;
            //   worker = new Thread(() -> { while (running) { /* spin */ } });
            //   worker.setDaemon(true);
            //   worker.start();
            throw new UnsupportedOperationException("Implement start()");
        }

        /**
         * Sets running = false.  No memory barrier — worker may not observe it.
         */
        public void stop() {
            // TODO: running = false;
            throw new UnsupportedOperationException("Implement stop()");
        }

        public boolean isRunning()     { return running; }
        public boolean isThreadAlive() { return worker != null && worker.isAlive(); }
    }

    // ── Fixed version ─────────────────────────────────────────────────────────

    public static class FixedVisibility {

        // TODO: declare as volatile boolean
        private volatile boolean running = false;
        private Thread worker;

        /**
         * Sets running = true, then starts a worker thread that loops while
         * (running), sleeping 1 ms per iteration to avoid 100% CPU usage.
         */
        public void start() {
            // TODO:
            //   running = true;
            //   worker = new Thread(() -> {
            //       while (running) {
            //           try { Thread.sleep(1); } catch (InterruptedException ignored) {}
            //       }
            //   });
            //   worker.setDaemon(true);
            //   worker.start();
            throw new UnsupportedOperationException("Implement start()");
        }

        /**
         * Sets running = false (volatile write → memory barrier) then joins
         * the worker thread to confirm it has exited.
         */
        public void stop() throws InterruptedException {
            // TODO: running = false; worker.join();
            throw new UnsupportedOperationException("Implement stop()");
        }

        public boolean isRunning()     { return running; }
        public boolean isThreadAlive() { return worker != null && worker.isAlive(); }
    }
}
