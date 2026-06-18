# Hints — Problem 01: Thread Basics

## Level 1 — Nudge (read this first, wait 10 min before looking further)

You need a class that *is* a thread, not one that merely uses a thread.
Think about which class in `java.lang` you extend, and what method you override.
When you call `start()`, that override is what runs — not on *your* thread, on the new one.

---

## Level 2 — Direction (still stuck after Level 1?)

- The constructor should accept `message` and `repeatCount` and also name the thread something meaningful — there is a `Thread` constructor that takes a name string.
- Inside `run()`, a simple `for` loop handles the printing. You also need to capture which thread name executed it — `Thread.currentThread().getName()` is available inside `run()`.
- Store the executing thread name in a field so callers can inspect it after the thread finishes.

---

## Level 3 — Almost there (if tests are still red)

| Symptom | Likely cause |
|---|---|
| Output appears on the *main* thread | You called `run()` instead of `start()` |
| `getExecutingThreadName()` returns `null` | You stored the name before the thread ran (field never set inside `run()`) |
| Tests complain the thread is not named | Pass the thread name to `super(name)` in your constructor |

---

## Common mistake to avoid

Calling `run()` directly executes the method on the calling thread — no new thread is created. Always call `start()`.

