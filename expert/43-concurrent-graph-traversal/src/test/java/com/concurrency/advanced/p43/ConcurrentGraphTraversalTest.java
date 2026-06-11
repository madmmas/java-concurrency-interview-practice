package com.concurrency.advanced.p43;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=10, unit=TimeUnit.SECONDS)
class ConcurrentGraphTraversalTest {
    private ConcurrentGraphTraversal.Graph<Integer> g5() {
        var g = new ConcurrentGraphTraversal.Graph<Integer>();
        g.addEdge(0,1); g.addEdge(0,2); g.addEdge(1,3); g.addEdge(2,4); return g;
    }
    private ConcurrentGraphTraversal.Graph<Integer> cyclic() {
        var g = new ConcurrentGraphTraversal.Graph<Integer>();
        g.addEdge(0,1); g.addEdge(1,2); g.addEdge(2,0); g.addEdge(1,3); return g;
    }
    @Test void bfsVisitsAllNodes() throws Exception {
        var exec = Executors.newFixedThreadPool(4);
        var visited = ConcurrentHashMap.<Integer>newKeySet();
        new ConcurrentGraphTraversal.ParallelBFS<Integer>().traverse(g5(),0,visited::add,exec);
        exec.shutdown(); assertEquals(Set.of(0,1,2,3,4),visited);
    }
    @Test void bfsEachNodeOnce() throws Exception {
        var exec = Executors.newFixedThreadPool(4);
        var counts = new ConcurrentHashMap<Integer,Integer>();
        new ConcurrentGraphTraversal.ParallelBFS<Integer>().traverse(g5(),0,n->counts.merge(n,1,Integer::sum),exec);
        exec.shutdown(); counts.values().forEach(c->assertEquals(1,c));
    }
    @Test void bfsHandlesCycles() throws Exception {
        var exec = Executors.newFixedThreadPool(4);
        var visited = ConcurrentHashMap.<Integer>newKeySet();
        new ConcurrentGraphTraversal.ParallelBFS<Integer>().traverse(cyclic(),0,visited::add,exec);
        exec.shutdown(); assertEquals(Set.of(0,1,2,3),visited);
    }
    @Test void dfsVisitsAllNodes() throws Exception {
        var pool = new ForkJoinPool(4);
        var visited = ConcurrentHashMap.<Integer>newKeySet();
        new ConcurrentGraphTraversal.ParallelDFS<Integer>().traverse(g5(),0,visited::add,pool);
        pool.shutdown(); assertEquals(Set.of(0,1,2,3,4),visited);
    }
    @Test void dfsHandlesCycles() throws Exception {
        var pool = new ForkJoinPool(4);
        var visited = ConcurrentHashMap.<Integer>newKeySet();
        new ConcurrentGraphTraversal.ParallelDFS<Integer>().traverse(cyclic(),0,visited::add,pool);
        pool.shutdown(); assertEquals(Set.of(0,1,2,3),visited);
    }
}
