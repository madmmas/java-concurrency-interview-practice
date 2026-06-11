# Problem 34 — Actor Model & Message-Passing Concurrency

## 🔴 Difficulty: Advanced

## 📖 Background

The **Actor Model** is a concurrency paradigm where computation is organised
around independent entities called *actors*. Each actor:

- Has a **mailbox** (a queue of incoming messages)
- Processes messages **one at a time** — no shared mutable state between actors
- Can, in response to a message: send messages to other actors, create new actors,
  and update its own private state

Because actors never share state, there are **no data races** and **no locks**
by design. The only synchronisation primitive is message passing.

### Java implementation strategy

Java has no built-in actor runtime (unlike Erlang or Akka/Scala), but we can
build a minimal one using:

```
Actor
 ├── LinkedBlockingQueue<Message>  ← mailbox
 ├── Thread (or Executor task)     ← processes messages sequentially
 └── private state                 ← only touched by this actor's thread
```

### Key patterns

**Tell (fire-and-forget)**
```java
actor.tell(new Message("ping"));    // non-blocking
```

**Ask (request-reply with Future)**
```java
CompletableFuture<Object> reply = actor.ask(new Message("get-balance"));
int balance = (int) reply.get();
```

**Supervision** — actors can supervise child actors; if a child crashes,
the supervisor decides whether to restart, stop, or escalate.

### Actor vs thread pool
| | Thread Pool | Actor |
|---|---|---|
| Shared state | Yes — needs locks | No — private per actor |
| Message ordering | No guarantee | Per-actor FIFO guarantee |
| Supervision | Manual | Built-in hierarchy |
| Scalability | Limited by thread count | Thousands of actors per thread |

## 🎯 Task

### `Actor<S, M>`
A generic actor base class:

- **Type params**: `S` = state type, `M` = message type
- `Actor(S initialState, Executor executor)` — creates actor with initial state;
  uses the executor to process messages
- `tell(M message)` — enqueues `message` in the mailbox; returns immediately
- `CompletableFuture<Object> ask(M message)` — enqueues message paired with a
  `CompletableFuture`; the actor resolves the future when it processes the message
- `protected abstract Object receive(S state, M message)` — **implement this in subclasses**;
  called for each message; return value resolves pending `ask()` futures; may mutate `state`
- `stop()` — sends a poison-pill to drain and terminate the actor's processing loop
- `isAlive()` — true if the actor's processing loop is still running

### `BankAccountActor` (extends `Actor<Integer, BankAccountActor.Command>`)
An actor that manages a bank account balance:

- Commands: `Deposit(amount)`, `Withdraw(amount)`, `GetBalance`
- `receive()` handles each command:
  - `Deposit` → add to balance; return new balance
  - `Withdraw` → subtract (throw `IllegalStateException` if insufficient); return new balance
  - `GetBalance` → return current balance without changing it
- The balance is the actor's **private state** — never touched by external threads

### `ActorSystem`
A lightweight runtime that manages a pool of actors:

- `ActorSystem(int threads)` — creates a fixed thread pool shared by all actors
- `<S, M> Actor<S, M> actorOf(S initialState, ActorFactory<S, M> factory)` — creates
  and registers a new actor; starts its processing loop
- `shutdown()` — stops all registered actors and shuts down the thread pool
- `getActorCount()` — number of currently registered actors

### `PingPongActors`
Two actors that bounce messages between each other a fixed number of times:

- `run(int rounds)` — creates two actors (Ping and Pong), starts the exchange
  with a `ping` message, and returns a `CompletableFuture<Integer>` that completes
  with the total number of messages exchanged once all rounds finish

## 💡 Hints

- `Actor` mailbox: `LinkedBlockingQueue<Envelope>` where `Envelope` holds both
  the message and an optional `CompletableFuture<Object>` (null for `tell`)
- Processing loop: submit a recursive `Runnable` to the executor that calls
  `mailbox.poll(100, MILLISECONDS)` and loops until a poison-pill is seen
- `ask()`: create a `CompletableFuture<Object>`, wrap message + future in an
  `Envelope`, enqueue it, return the future
- `receive()` return value: after calling `receive(state, msg)`, if the envelope
  has a pending future, complete it with the return value
- `BankAccountActor`: state is `Integer` (the balance); the `receive` method
  switches on command type
- `PingPongActors`: Ping's `receive` sends a `pong` back to Pong and decrements
  a counter; when counter reaches 0, complete the result future

## 🧠 Interview Talking Points

- How does the Actor Model eliminate data races without locks?
- What is the difference between `tell` (fire-and-forget) and `ask` (request-reply)?
- What is the "let it crash" philosophy in actor-based systems?
- How does Akka's actor model differ from this minimal Java implementation?
- What are the trade-offs of actors vs `synchronized` shared state?
- How do you handle back-pressure in an actor system when a mailbox grows unbounded?
