package com.concurrency.beginner.p10;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class RequestContextTest {

    @AfterEach
    void cleanUp() {
        RequestContext.clear(); // ensure no state leaks between tests
    }

    @Test
    void setAndGetOnSameThread() {
        RequestContext.setUserId("user-42");
        RequestContext.setRequestId("req-99");
        assertEquals("user-42", RequestContext.getUserId());
        assertEquals("req-99", RequestContext.getRequestId());
    }

    @Test
    void nullBeforeSet() {
        assertNull(RequestContext.getUserId());
        assertNull(RequestContext.getRequestId());
    }

    @Test
    void clearRemovesBothValues() {
        RequestContext.setUserId("alice");
        RequestContext.setRequestId("r1");
        RequestContext.clear();
        assertNull(RequestContext.getUserId(), "userId should be null after clear()");
        assertNull(RequestContext.getRequestId(), "requestId should be null after clear()");
    }

    @Test
    void eachThreadHasOwnContext() throws InterruptedException {
        CountDownLatch bothSet  = new CountDownLatch(2);
        CountDownLatch proceed  = new CountDownLatch(1);
        String[] resultA = new String[1];
        String[] resultB = new String[1];

        Thread tA = new Thread(() -> {
            RequestContext.setUserId("userA");
            bothSet.countDown();
            try { proceed.await(); } catch (InterruptedException ignored) {}
            resultA[0] = RequestContext.getUserId();
            RequestContext.clear();
        });

        Thread tB = new Thread(() -> {
            RequestContext.setUserId("userB");
            bothSet.countDown();
            try { proceed.await(); } catch (InterruptedException ignored) {}
            resultB[0] = RequestContext.getUserId();
            RequestContext.clear();
        });

        tA.start(); tB.start();
        bothSet.await();
        proceed.countDown();
        tA.join(); tB.join();

        assertEquals("userA", resultA[0], "Thread A must see its own userId");
        assertEquals("userB", resultB[0], "Thread B must see its own userId");
    }

    @Test
    void settingOnOneThreadDoesNotAffectAnother() throws InterruptedException {
        RequestContext.setUserId("main-user");

        String[] childSaw = {null};
        Thread child = new Thread(() -> childSaw[0] = RequestContext.getUserId());
        child.start();
        child.join();

        assertNull(childSaw[0], "Child thread should not inherit parent's ThreadLocal value");
    }
}
