package com.concurrency.advanced.p43;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
/**
 * Problem 43 – Concurrent Graph Traversal (Parallel BFS & DFS)
 *
 * Graph: adjacency map using ConcurrentHashMap.
 * ParallelBFS: level-by-level; each level's nodes submitted to executor concurrently.
 * ParallelDFS: recursive RecursiveAction; fork one task per unvisited neighbor.
 * Visited set: ConcurrentHashMap.newKeySet() — add() returns true only for the first visitor.
 *
 * TODO ParallelBFS.traverse(): frontier = [start]; while frontier not empty:
 *   submit each node to executor; visitor.accept(node); add unvisited neighbors to next frontier.
 *   Wait (invokeAll/futures) then repeat with next frontier.
 * TODO ParallelDFS.traverse(): pool.invoke(new DFSTask(start,...))
 * TODO DFSTask.compute(): visitor.accept(node); fork DFSTask for each unvisited neighbor; invokeAll.
 */
public class ConcurrentGraphTraversal {
    public static class Graph<N> {
        private final ConcurrentHashMap<N,Set<N>> adj = new ConcurrentHashMap<>();
        public void addNode(N n) { adj.putIfAbsent(n, ConcurrentHashMap.newKeySet()); }
        public void addEdge(N f, N t) { addNode(f); addNode(t); adj.get(f).add(t); }
        public Set<N> neighbors(N n) { return adj.getOrDefault(n,Collections.emptySet()); }
        public Set<N> nodes() { return adj.keySet(); }
    }
    public static class ParallelBFS<N> {
        public void traverse(Graph<N> g, N start, Consumer<N> visitor, ExecutorService exec)
                throws InterruptedException, ExecutionException {
            throw new UnsupportedOperationException("Implement ParallelBFS.traverse()");
        }
    }
    public static class ParallelDFS<N> {
        public void traverse(Graph<N> g, N start, Consumer<N> visitor, ForkJoinPool pool)
                throws InterruptedException {
            throw new UnsupportedOperationException("Implement ParallelDFS.traverse()");
        }
        private class DFSTask extends RecursiveAction {
            private final N node; private final Graph<N> g; private final Consumer<N> v; private final Set<N> visited;
            DFSTask(N n, Graph<N> g, Consumer<N> v, Set<N> vs) { node=n; this.g=g; this.v=v; visited=vs; }
            @Override protected void compute() { throw new UnsupportedOperationException("Implement DFSTask.compute()"); }
        }
    }
}
