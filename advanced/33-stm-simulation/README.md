# Problem 33 — Software Transactional Memory (STM) Simulation

## 🔴 Difficulty: Advanced

## 📖 Background

**Software Transactional Memory (STM)** is a concurrency control mechanism that
allows groups of memory operations to be executed **atomically**, similar to
database transactions. Instead of acquiring locks before accessing shared data,
threads execute optimistically in a **private sandbox**; changes are committed
only if no conflict is detected.

### Core concepts

**Versioned reference (`TVar<T>`)**  
Each shared variable has a value and a monotonically increasing **version number**.
Reads snapshot the value and version; writes are buffered locally.

**Transaction**  
- **Read-set**: variables read during the transaction + their versions at read time
- **Write-set**: local uncommitted writes (shadows the real value)

**Commit protocol (optimistic concurrency)**
```
1. Lock all variables in the write-set
2. Validate: for each variable in the read-set,
             check that its current version == the version when we read it
3. If valid:  apply writes, increment each variable's version, unlock
   If invalid: abort (throw RetryException), caller retries the whole transaction
```

### Advantages over lock-based concurrency
- No deadlock (no locks held while waiting)
- Composable: two atomic operations compose into one without changing their code
- Optimistic: no contention cost when conflicts are rare

### Real-world STM implementations
- Haskell's STM (gold standard — native support)
- Clojure's `atom`, `ref`, `dosync`
- Multiverse (Java library)

## 🎯 Task

### `TVar<T>` — Transactional Variable
- `TVar(T initialValue)` — creates a variable with version 0
- `T read(Transaction tx)` — reads value via the transaction (records to read-set)
- `void write(Transaction tx, T value)` — writes via the transaction (buffers in write-set)
- `T readDirect()` — reads current committed value (no transaction, for testing)
- `long getVersion()` — current committed version number

### `Transaction`
- `Transaction()` — creates a new transaction context
- `<T> T read(TVar<T> var)` — reads from write-set if present, else reads and records current version
- `<T> void write(TVar<T> var, T value)` — buffers write locally (does not commit)
- `void commit()` — locks all write-set vars, validates read-set, applies writes, increments versions, unlocks; throws `RetryException` on conflict
- `boolean isAborted()` — true if the transaction was aborted

### `STM`
Static utility class:
- `<T> T atomically(Supplier<T> txBody)` — runs `txBody` in a new `Transaction`; if `RetryException` is thrown, starts a fresh `Transaction` and retries (max 100 retries); returns the final committed result
- `void atomically(Runnable txBody)` — void variant

### Use `STM.atomically()` to implement:
- `transfer(TVar<Integer> from, TVar<Integer> to, int amount)` — deducts `amount` from `from`, adds to `to`; both operations in one atomic transaction
- `swap(TVar<T> a, TVar<T> b)` — atomically swaps values of two `TVar`s

## 💡 Hints

- `TVar`: use a `ReentrantLock` per variable (for commit-time locking); store
  `volatile T value` and `volatile long version`
- `Transaction`: use `Map<TVar<?>, Object> writeSet` and `Map<TVar<?>, Long> readSet`
- Commit:
  1. Sort write-set vars by identity hash to acquire locks in consistent order (prevents deadlock)
  2. Lock all write-set vars
  3. For each read-set entry: if `var.getVersion() != readSet.get(var)` → abort
  4. Apply writes: `var.value = newValue; var.version++`
  5. Unlock all in finally
- `RetryException` is not really an error — it's a signal to retry

## 🧠 Interview Talking Points

- What is the commit protocol for optimistic STM?
- How does STM avoid deadlock compared to lock-based approaches?
- What is the ABA problem in STM and how do version numbers prevent it?
- Why is composability an advantage of STM? Give a concrete example.
- What are the performance trade-offs of STM vs fine-grained locking?
- Why does Haskell's STM work better than Java's due to the type system?
