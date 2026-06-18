# Hints — Problem 07: Daemon Threads

## Level 1 — Nudge

A daemon thread is marked with one method call before `start()`. If you forget that method, the JVM will not exit until the thread finishes — the test harness will hang. Check the `Thread` API for a method that sets the daemon property.

---

## Level 2 — Direction

**`HeartbeatService`**:
- Create the thread, call `thread.setDaemon(true)`, *then* call `thread.start()` — order matters
- The loop: `while (!stopped) { beatAction.run(); Thread.sleep(intervalMs); }`
- Use `volatile boolean stopped` as the stop flag
- `stop()` sets `stopped = true` and interrupts the thread (to break out of `sleep`)

**`BackgroundLogger`**:
- Use `LinkedBlockingQueue<String>` for the message queue
- The daemon thread loop: `String msg = queue.poll(100, TimeUnit.MILLISECONDS)` — timed poll so the stop flag is checked regularly
- `log(message)` calls `queue.offer(message)` (non-blocking for the caller)
- `stop()` sets the flag; the poll timeout lets the thread notice within 100ms

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Test hangs after all assertions pass | Thread is not a daemon — JVM waits for it to finish |
| `IllegalThreadStateException` | You called `setDaemon()` after `start()` |
| Logger loses messages | You called `queue.take()` (blocks forever) — use `poll(timeout)` instead |

---

## Key rule

`setDaemon(true)` must be called *before* `start()`. After the thread is running, the daemon property cannot be changed.

