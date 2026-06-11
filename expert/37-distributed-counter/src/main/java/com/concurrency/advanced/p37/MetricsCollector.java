package com.concurrency.advanced.p37;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
/**
 * Problem 37 – Distributed Counter: Metrics Collector
 * Per-endpoint request counts and latency totals using LongAdder.
 *
 * TODO recordRequest(endpoint):      computeIfAbsent(endpoint, k->new LongAdder()).increment()
 * TODO recordLatency(endpoint, ms):  latencyAdder.add(ms)
 * TODO getRequestCount(endpoint):    adder.sum() or 0
 * TODO getTotalLatency(endpoint):    adder.sum() or 0
 * TODO getAverageLatency(endpoint):  total/count or 0.0
 * TODO getTopEndpoints(n):           sort by count desc, limit n, return keys
 */
public class MetricsCollector {
    private final ConcurrentHashMap<String,LongAdder> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String,LongAdder> latencyTotals = new ConcurrentHashMap<>();
    public void   recordRequest(String ep)          { throw new UnsupportedOperationException("Implement recordRequest()"); }
    public void   recordLatency(String ep, long ms) { throw new UnsupportedOperationException("Implement recordLatency()"); }
    public long   getRequestCount(String ep)        { throw new UnsupportedOperationException("Implement getRequestCount()"); }
    public long   getTotalLatency(String ep)        { throw new UnsupportedOperationException("Implement getTotalLatency()"); }
    public double getAverageLatency(String ep)      { throw new UnsupportedOperationException("Implement getAverageLatency()"); }
    public List<String> getTopEndpoints(int n)      { throw new UnsupportedOperationException("Implement getTopEndpoints()"); }
}
