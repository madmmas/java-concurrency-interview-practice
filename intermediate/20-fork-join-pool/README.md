# Problem 20 — ForkJoinPool & RecursiveTask

## 🟡 Difficulty: Intermediate

## 📖 Background

`ForkJoinPool` is designed for **divide-and-conquer** algorithms. It uses
**work-stealing**: idle threads steal tasks from the back of other threads'
queues, keeping all CPU cores busy.

### Core classes
```
ForkJoinTask<V>
├── RecursiveTask<V>   // returns a value
└── RecursiveAction    // void (no return value)
```

### The pattern
```java
class SumTask extends RecursiveTask<Long> {
    static final int THRESHOLD = 1000;

    @Override
    protected Long compute() {
        if (size <= THRESHOLD) {
            return computeDirectly();   // base case
        }
        SumTask left  = new SumTask(leftHalf);
        SumTask right = new SumTask(rightHalf);
        left.fork();                    // async: push to queue
        long rightResult = right.compute(); // sync: run right on current thread
        long leftResult  = left.join(); // block until left is done
        return leftResult + rightResult;
    }
}

ForkJoinPool pool = new ForkJoinPool();
long result = pool.invoke(new SumTask(data));
```

**Key insight — fork/join vs invoke:**
- `fork()` + `join()`: asynchronous; use when you have two sub-tasks to run in parallel
- `invokeAll(left, right)`: convenience for forking both and joining both
- Always compute **one** sub-task directly (not forked) to avoid unnecessary overhead

**Work-stealing:** Each worker thread has a deque. It pushes new tasks to the
front and takes tasks from the front (LIFO). Idle threads steal from the **back**
(FIFO) of another thread's deque — this ensures locality for the thief.

## 🎯 Task

### `ParallelSum` — `RecursiveTask<Long>`
Sums an `int[]` array in parallel using divide-and-conquer:
- Split threshold: 2000 elements
- `compute(int[] array, int from, int to)` recursively — if range ≤ threshold,
  sum sequentially; otherwise fork two halves
- `sum(int[] array)` — entry point: creates a `ForkJoinPool` and invokes the task

### `ParallelMergeSort` — `RecursiveAction`
Sorts an `int[]` in-place using parallel merge sort:
- Split threshold: 2000 elements
- Below threshold: use `Arrays.sort()` on the subrange (sequential base case)
- Above threshold: fork left half, compute right half, join left, then merge
- `sort(int[] array)` — entry point

### `ParallelSearch` — `RecursiveTask<Integer>`
Finds the **index** of a target value in an unsorted `int[]` using parallel search:
- Split threshold: 1000 elements
- Returns the index if found, or `-1` if not present
- On fork: if left half finds it first, return immediately (cancel right task)
- `search(int[] array, int target)` — entry point

## 💡 Hints
- For `ParallelSum`: `right.compute()` on current thread, `left.fork()` first,
  then `left.join()` — this is the standard fork-one-compute-one pattern
- For `ParallelMergeSort`: merge into a temp array then copy back
- For `ParallelSearch`: after left and right tasks, check if left returned ≥ 0 first;
  only inspect right result if left found nothing

## 🧠 Interview Talking Points
- What is work-stealing and why does it improve CPU utilization?
- Why should you compute one sub-task directly (not fork both)?
- What is the common pool (`ForkJoinPool.commonPool()`) and when should you use it?
- How does `RecursiveTask` differ from `RecursiveAction`?
- What threshold should you use in practice?
