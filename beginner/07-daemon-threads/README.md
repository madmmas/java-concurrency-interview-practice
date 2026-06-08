# Problem 07 — Daemon Threads

## 🟢 Difficulty: Beginner

## 📖 Background

Java threads are either **user threads** (default) or **daemon threads**.

| | User Thread | Daemon Thread |
|---|---|---|
| JVM shutdown | JVM waits for all user threads | JVM does NOT wait |
| Typical use | Business logic, request handling | Background services (GC, logging, heartbeat) |
| Set via | `thread.setDaemon(false)` (default) | `thread.setDaemon(true)` |

**Key rules:**
- `setDaemon()` must be called **before** `start()`, otherwise `IllegalThreadStateException`
- A thread created by a daemon thread is also a daemon by default
- When only daemon threads remain, the JVM exits immediately — daemon threads may be killed mid-operation

**Typical daemon use cases:**
- Garbage collector
- Background cache eviction
- Periodic heartbeat / health-check emitters
- Log flusher threads

## 🎯 Task

Implement `HeartbeatService`:
- `start()` — starts a **daemon** thread that "beats" every `intervalMs` milliseconds by calling an injected `Runnable` beat action
- `stop()` — gracefully stops the heartbeat thread
- `getBeatCount()` — returns how many times the beat action has been invoked
- `isDaemon()` — returns whether the internal thread is a daemon thread (must be `true`)

Implement `BackgroundLogger`:
- `start()` — starts a **daemon** thread that drains a shared `BlockingQueue<String>` and records log lines
- `log(String message)` — enqueues a message for background logging (non-blocking for the caller)
- `getLoggedMessages()` — returns all messages that have been logged so far
- `stop()` — signals the logger to finish and stops it

## 💡 Hints
- Call `thread.setDaemon(true)` before `thread.start()`
- Use `Thread.sleep(intervalMs)` inside the heartbeat loop
- For `BackgroundLogger`, use `LinkedBlockingQueue` and `poll(timeout)` so the thread can check a stop flag

## 🧠 Interview Talking Points
- What happens to daemon threads when the JVM shuts down?
- Can daemon threads be used for database transactions? Why not?
- How does the JVM decide when to exit?
- What is the difference between `thread.interrupt()` and a volatile stop flag?
