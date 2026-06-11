package com.concurrency.advanced.p28;

import java.util.*;

/**
 * Problem 28 – Deadlock Detection: Resource Allocation Graph
 *
 * Tracks resource ownership and thread wait-edges; detects cycles via DFS.
 * Not thread-safe itself — the caller must synchronize if needed.
 *
 * Graph model:
 *   Assignment edge: resource R is held by thread T  → heldBy.put(R, T)
 *   Request   edge:  thread T is waiting for resource R → waitingFor[T].add(R)
 *
 * Cycle detection: build a "wait-for" graph where T → U means "T is waiting for
 * a resource currently held by U"; a cycle in this graph = deadlock.
 */
public class ResourceAllocationGraph {

    /** resource → thread currently holding it */
    private final Map<String, String>      heldBy     = new HashMap<>();

    /** thread → set of resources the thread is waiting to acquire */
    private final Map<String, Set<String>> waitingFor = new HashMap<>();

    /**
     * Records that threadName wants to acquire resource.
     * (Adds a request edge: thread → resource)
     */
    public void requestResource(String threadName, String resource) {
        // TODO: waitingFor.computeIfAbsent(threadName, k -> new HashSet<>()).add(resource);
        throw new UnsupportedOperationException("Implement requestResource()");
    }

    /**
     * Records that threadName now holds resource.
     * (Adds assignment edge resource → threadName; removes the request edge)
     */
    public void assignResource(String threadName, String resource) {
        // TODO:
        //   heldBy.put(resource, threadName);
        //   Set<String> wants = waitingFor.get(threadName);
        //   if (wants != null) wants.remove(resource);
        throw new UnsupportedOperationException("Implement assignResource()");
    }

    /**
     * Removes all edges associated with resource (thread releases it).
     */
    public void releaseResource(String threadName, String resource) {
        // TODO:
        //   heldBy.remove(resource);
        //   Set<String> wants = waitingFor.get(threadName);
        //   if (wants != null) wants.remove(resource);
        throw new UnsupportedOperationException("Implement releaseResource()");
    }

    /**
     * Returns true if a cycle exists in the current wait-for graph (= deadlock).
     *
     * Algorithm:
     *  1. Build wait-for graph: for each (thread T, resources W) in waitingFor:
     *       for each resource R in W: if heldBy.get(R) = U → add edge T → U
     *  2. DFS with visited + onStack sets; if you reach a node already onStack → cycle
     */
    public boolean hasDeadlock() {
        // TODO: implement DFS cycle detection on the wait-for graph
        throw new UnsupportedOperationException("Implement hasDeadlock()");
    }

    /**
     * Returns the set of thread names participating in any deadlock cycle.
     * Returns an empty set if there is no deadlock.
     */
    public Set<String> getDeadlockedThreads() {
        // TODO: if no deadlock return Collections.emptySet()
        //       else collect all threads involved in cycles
        throw new UnsupportedOperationException("Implement getDeadlockedThreads()");
    }

    // ── Helper: DFS ───────────────────────────────────────────────────────────

    /**
     * Builds the simplified wait-for graph: thread T → thread U means
     * T is waiting for a resource currently held by U.
     */
    private Map<String, Set<String>> buildWaitForGraph() {
        Map<String, Set<String>> graph = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : waitingFor.entrySet()) {
            String thread = entry.getKey();
            for (String resource : entry.getValue()) {
                String holder = heldBy.get(resource);
                if (holder != null && !holder.equals(thread)) {
                    graph.computeIfAbsent(thread, k -> new HashSet<>()).add(holder);
                }
            }
        }
        return graph;
    }

    /**
     * DFS cycle detection.
     * @param node     current node
     * @param graph    adjacency map
     * @param visited  globally visited nodes
     * @param onStack  nodes in the current DFS path (recursion stack)
     * @param cycleSet nodes confirmed to be in a cycle (populated on cycle found)
     * @return true if a cycle is reachable from node
     */
    private boolean dfs(String node,
                        Map<String, Set<String>> graph,
                        Set<String> visited,
                        Set<String> onStack,
                        Set<String> cycleSet) {
        // TODO: standard iterative or recursive DFS with onStack tracking
        //   visited.add(node); onStack.add(node);
        //   for each neighbour of node in graph:
        //       if !visited → recurse; if cycle found → cycleSet.add(node); return true
        //       if onStack  → cycleSet.add(node); cycleSet.add(neighbour); return true
        //   onStack.remove(node);
        //   return false;
        throw new UnsupportedOperationException("Implement dfs()");
    }
}
