package com.concurrency.advanced.p50;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=15, unit=TimeUnit.SECONDS)
class MiniJobSchedulerTest {
    private MiniJobScheduler sched;
    @BeforeEach void setUp() {
        sched=new MiniJobScheduler(new MiniJobScheduler.SchedulerConfig().coreThreads(2).maxThreads(4).maxJobsPerSecond(200).burstCapacity(20).queueCapacity(500).shutdownTimeoutMs(5000));
    }
    @AfterEach void tearDown() throws Exception { sched.shutdown(); }

    @Test void oneShotReturnsResult() throws Exception {
        var h=sched.submit(MiniJobScheduler.Job.oneShot("j1","compute",5,()->"hello"));
        assertEquals("hello",(String)h.getFuture().get(5,TimeUnit.SECONDS));
        assertEquals(MiniJobScheduler.JobStatus.DONE,h.getStatus());
    }
    @Test void multipleJobsAllComplete() throws Exception {
        var handles=new ArrayList<MiniJobScheduler.JobHandle>();
        for(int i=0;i<20;i++){final int id=i; handles.add(sched.submit(MiniJobScheduler.Job.oneShot("j"+id,"batch",5,()->"r"+id)));}
        for(var h:handles){h.getFuture().get(8,TimeUnit.SECONDS); assertEquals(MiniJobScheduler.JobStatus.DONE,h.getStatus());}
    }
    @Test void delayedJobNotImmediate() throws Exception {
        var ran=new AtomicInteger(0);
        sched.submit(new MiniJobScheduler.Job("d1","d",5,()->{ran.incrementAndGet();return"ok";},300,0));
        Thread.sleep(100); assertEquals(0,ran.get());
        Thread.sleep(500); assertEquals(1,ran.get());
    }
    @Test void recurringJobRunsMultipleTimes() throws Exception {
        var latch=new CountDownLatch(3);
        sched.submit(MiniJobScheduler.Job.recurring("r1","hb",5,()->{latch.countDown();return"beat";},100));
        assertTrue(latch.await(5,TimeUnit.SECONDS));
    }
    @Test void cancelPendingJobPreventsExecution() throws Exception {
        var blocker=new CountDownLatch(1);
        for(int i=0;i<4;i++) sched.submit(MiniJobScheduler.Job.oneShot("b"+i,"bl",0,()->{blocker.await();return"x";}));
        Thread.sleep(50);
        var ran=new AtomicInteger(0);
        var h=sched.submit(MiniJobScheduler.Job.oneShot("c","t",1,()->{ran.incrementAndGet();return"ran";}));
        assertTrue(h.cancel()); assertEquals(MiniJobScheduler.JobStatus.CANCELLED,h.getStatus());
        blocker.countDown(); Thread.sleep(300); assertEquals(0,ran.get());
    }
    @Test void cancelDoneReturnsFalse() throws Exception {
        var h=sched.submit(MiniJobScheduler.Job.oneShot("d","t",5,()->"ok"));
        h.getFuture().get(5,TimeUnit.SECONDS); assertFalse(h.cancel());
    }
    @Test void metricsTrackSuccessAndFailure() throws Exception {
        var handles=new ArrayList<MiniJobScheduler.JobHandle>();
        for(int i=0;i<3;i++) handles.add(sched.submit(MiniJobScheduler.Job.oneShot("s"+i,"mt",5,()->"ok")));
        for(int i=0;i<2;i++) handles.add(sched.submit(MiniJobScheduler.Job.oneShot("f"+i,"mt",5,()->{throw new RuntimeException();})));
        for(var h:handles){try{h.getFuture().get(5,TimeUnit.SECONDS);}catch(ExecutionException e){}}
        Thread.sleep(100);
        var m=sched.getMetrics(); assertTrue(m.containsKey("mt"));
        assertEquals(3,m.get("mt").successCount); assertEquals(2,m.get("mt").failureCount);
    }
    @Test void shutdownRejectsNew() throws Exception {
        sched.shutdown(); assertThrows(IllegalStateException.class,()->sched.submit(MiniJobScheduler.Job.oneShot("x","t",5,()->"x")));
    }
    @Test void statusIsRunningInitially() { assertEquals(MiniJobScheduler.SchedulerStatus.RUNNING,sched.getSchedulerStatus()); }
    @Test void statusTerminatedAfterShutdown() throws Exception { sched.shutdown(); assertEquals(MiniJobScheduler.SchedulerStatus.TERMINATED,sched.getSchedulerStatus()); }
    @Test void concurrentSubmissionsAllComplete() throws Exception {
        int threads=10,each=20; var ran=new AtomicInteger(0);
        var handles=new CopyOnWriteArrayList<MiniJobScheduler.JobHandle>();
        var go=new CountDownLatch(1); var done=new CountDownLatch(threads);
        for(int t=0;t<threads;t++){final int tid=t; new Thread(()->{ try{go.await(); for(int i=0;i<each;i++) handles.add(sched.submit(MiniJobScheduler.Job.oneShot("t"+tid+"j"+i,"stress",tid%5,()->{ran.incrementAndGet();return"ok";}))); }catch(Exception e){}finally{done.countDown();} }).start();}
        go.countDown(); done.await();
        for(var h:handles){try{h.getFuture().get(10,TimeUnit.SECONDS);}catch(ExecutionException e){}}
        assertEquals(threads*each,ran.get());
    }
}
