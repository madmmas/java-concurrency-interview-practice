package com.concurrency.advanced.p37;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=10, unit=TimeUnit.SECONDS)
class DistributedCounterTest {
    @Test void stripedCounterSingleThread() {
        var c = new StripedCounter(4);
        c.increment(); c.increment(); c.add(10);
        assertEquals(12, c.sum());
    }
    @Test void stripedCounterReset() {
        var c = new StripedCounter(4); c.add(100); c.reset(); assertEquals(0, c.sum());
    }
    @Test void stripedCounterConcurrent() throws Exception {
        var c = new StripedCounter(8);
        int threads=10, each=1000;
        var workers = new ArrayList<Thread>();
        for (int i=0;i<threads;i++) workers.add(new Thread(()->{ for(int j=0;j<each;j++) c.increment(); }));
        workers.forEach(Thread::start); for (var t:workers) t.join();
        assertEquals((long)threads*each, c.sum());
    }
    @Test void metricsRecordAndQuery() {
        var m = new MetricsCollector();
        m.recordRequest("/a"); m.recordRequest("/a"); m.recordRequest("/b");
        m.recordLatency("/a",100); m.recordLatency("/a",200);
        assertEquals(2, m.getRequestCount("/a"));
        assertEquals(300, m.getTotalLatency("/a"));
        assertEquals(150.0, m.getAverageLatency("/a"), 1e-9);
        assertEquals(0, m.getRequestCount("/z"));
    }
    @Test void metricsTopEndpoints() {
        var m = new MetricsCollector();
        m.recordRequest("/c"); m.recordRequest("/c"); m.recordRequest("/c");
        m.recordRequest("/b"); m.recordRequest("/b");
        m.recordRequest("/a");
        assertEquals(List.of("/c","/b"), m.getTopEndpoints(2));
    }
    @Test void metricsConcurrent() throws Exception {
        var m = new MetricsCollector();
        int threads=10, each=100;
        var workers = new ArrayList<Thread>();
        for (int i=0;i<threads;i++) workers.add(new Thread(()->{ for(int j=0;j<each;j++){ m.recordRequest("/ep"); m.recordLatency("/ep",10); } }));
        workers.forEach(Thread::start); for(var t:workers) t.join();
        assertEquals((long)threads*each, m.getRequestCount("/ep"));
    }
}
