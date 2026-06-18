# Hints — Problem 43: Concurrent Graph Traversal

## Level 1 — Nudge

For parallel BFS, each level'\''s frontier can be processed in parallel. Use a `ConcurrentHashMap` (or `ConcurrentHashMap.newKeySet()`) as the visited set — `add` returns false if already present, letting each thread atomically claim a node. For parallel DFS, use `ForkJoinPool` with a `RecursiveAction` per node.

---

## Level 2 — Direction

**Parallel BFS**:
```java
Set<T> visited = ConcurrentHashMap.newKeySet();
Queue<T> frontier = new ConcurrentLinkedQueue<>();
frontier.add(start); visited.add(start);
while (!frontier.isEmpty()) {
    List<Runnable> tasks = new ArrayList<>();
    for (T node : frontier) {
        frontier.remove(node);  // drain current frontier
        tasks.add(() -> {
            visitor.accept(node);
            graph.neighbors(node).forEach(n -> {
                if (visited.add(n)) frontier.add(n);  // atomic claim
            });
        });
    }
    // submit all tasks, wait for all to complete before next level
    List<Future<?>> futures = tasks.stream().map(executor::submit).collect(toList());
    futures.forEach(f -> f.get());
}
```

**Parallel DFS using ForkJoinPool**:
- `RecursiveAction` per node: call `visitor`, then fork a child action for each unvisited neighbour (claim with `visited.add()`)

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Nodes visited multiple times | Not using atomic `visited.add()` to claim — using `contains` then `add` (TOCTOU race) |
| BFS visits nodes out of level order | Not waiting for all tasks in the current level to complete before processing the next level |
| Cycles cause infinite loop | Same as above — `visited.add()` returning false must prevent re-queuing |
