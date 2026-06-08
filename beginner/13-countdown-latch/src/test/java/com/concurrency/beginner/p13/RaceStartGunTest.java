package com.concurrency.beginner.p13;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class RaceStartGunTest {

    @Test
    void racersBlockBeforeFire() throws InterruptedException {
        RaceStartGun gun = new RaceStartGun();
        int racers = 5;
        CountDownLatch registered = new CountDownLatch(racers);

        for (int i = 0; i < racers; i++) {
            new Thread(() -> {
                try {
                    registered.countDown();
                    gun.register();
                } catch (InterruptedException ignored) {}
            }).start();
        }

        registered.await();
        Thread.sleep(50); // let threads reach await()
        assertEquals(racers, gun.getWaitingCount(),
                "All " + racers + " racers should be waiting before fire()");
    }

    @Test
    void fireReleasesAllRacersSimultaneously() throws InterruptedException {
        RaceStartGun gun = new RaceStartGun();
        int racers = 10;
        CopyOnWriteArrayList<Long> releaseTimes = new CopyOnWriteArrayList<>();
        CountDownLatch allReleased = new CountDownLatch(racers);

        for (int i = 0; i < racers; i++) {
            new Thread(() -> {
                try {
                    gun.register();
                    releaseTimes.add(System.currentTimeMillis());
                } catch (InterruptedException ignored) {}
                finally { allReleased.countDown(); }
            }).start();
        }

        Thread.sleep(100); // all racers reach await()
        gun.fire();
        assertTrue(allReleased.await(3, TimeUnit.SECONDS),
                "All racers should be released after fire()");

        long spread = releaseTimes.stream().mapToLong(Long::longValue).max().orElse(0)
                - releaseTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        assertTrue(spread < 200,
                "All racers should be released nearly simultaneously (spread: " + spread + " ms)");
    }

    @Test
    void waitingCountDropsToZeroAfterFire() throws InterruptedException {
        RaceStartGun gun = new RaceStartGun();
        CountDownLatch allDone = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try { gun.register(); } catch (InterruptedException ignored) {}
                finally { allDone.countDown(); }
            }).start();
        }
        Thread.sleep(80);
        gun.fire();
        allDone.await();
        assertEquals(0, gun.getWaitingCount(), "Waiting count should be 0 after all racers released");
    }
}
