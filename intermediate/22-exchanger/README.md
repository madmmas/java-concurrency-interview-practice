# Problem 22 — Exchanger

## 🟡 Difficulty: Intermediate

## 📖 Background

`Exchanger<V>` is a synchronisation point where exactly **two threads** meet and
swap objects. Thread A calls `exchange(objA)` and blocks; when Thread B calls
`exchange(objB)`, both unblock — A receives `objB`, B receives `objA`.

```java
Exchanger<List<Integer>> exchanger = new Exchanger<>();

// Thread A — producer
List<Integer> full = fillBuffer();
List<Integer> empty = exchanger.exchange(full);   // hand off full, get empty

// Thread B — consumer
List<Integer> empty = new ArrayList<>();
List<Integer> full = exchanger.exchange(empty);   // hand off empty, get full
process(full);
```

Key characteristics:
- Exactly **two** threads participate — `exchange()` blocks until the partner arrives
- Bidirectional: both threads give and receive simultaneously
- `exchange(V, long, TimeUnit)` — timed variant; throws `TimeoutException` if partner
  doesn't arrive in time
- Classic use case: **double-buffering** (producer fills one buffer while consumer
  drains another; they swap at each cycle)

## 🎯 Task

### `DoubleBufferedLogger`
A high-throughput logger that uses `Exchanger` to implement double-buffering:

- Internally maintains two `List<String>` buffers — one being filled by callers,
  one being flushed by a background thread
- `log(String message)` — appends to the current fill buffer (thread-safe); when
  the fill buffer reaches `bufferSize`, exchanges it with the flush buffer
- `start()` — starts the background flush thread (daemon); it receives full buffers
  via the `Exchanger` and "flushes" them (appends to `flushedMessages`)
- `stop()` — signals stop, performs a final exchange to flush any partial buffer,
  joins the background thread
- `getFlushedMessages()` — returns all messages that have been flushed so far

### `GeneticCrossover`
Simulates a genetic crossover where two "chromosomes" (int arrays) swap their
second halves using an `Exchanger`:

- `crossover(int[] chromosomeA, int[] chromosomeB)` — starts two threads, each
  presenting the second half of its chromosome to the `Exchanger`; after the
  swap, each thread replaces its own second half with what it received
- The method blocks until both threads complete and returns `int[][] { resultA, resultB }`

## 💡 Hints
- `DoubleBufferedLogger`: use `synchronized` for appending to the fill buffer; the
  exchange itself only happens when the buffer is full — check size inside the lock
- The flush thread loop: `List<String> received = exchanger.exchange(emptyList)` →
  add all `received` to `flushedMessages` → repeat with a fresh empty list
- `GeneticCrossover`: each thread calls `exchanger.exchange(myHalf)`, then writes
  the received half back into its chromosome — use `System.arraycopy`

## 🧠 Interview Talking Points
- What happens if only one thread calls `exchange()` and the partner never arrives?
- How does `Exchanger` compare to using a `SynchronousQueue` for handoffs?
- Why is `Exchanger` limited to exactly two threads?
- Describe a double-buffering scenario where `Exchanger` is the cleanest solution.
