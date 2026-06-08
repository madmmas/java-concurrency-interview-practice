package com.concurrency.beginner.p11;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class BankAccountTest {

    private BankAccount account;

    @BeforeEach
    void setUp() { account = new BankAccount(1000.0); }

    @Test
    void depositIncreasesBalance() {
        account.deposit(500);
        assertEquals(1500.0, account.getBalance(), 0.001);
    }

    @Test
    void withdrawDecreasesBalance() {
        account.withdraw(300);
        assertEquals(700.0, account.getBalance(), 0.001);
    }

    @Test
    void withdrawThrowsWhenInsufficientFunds() {
        assertThrows(IllegalStateException.class, () -> account.withdraw(2000));
    }

    @Test
    void depositThrowsOnNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-10));
    }

    @Test
    void transferMovesMoneyCorrectly() {
        BankAccount target = new BankAccount(500);
        account.transfer(target, 200);
        assertEquals(800.0, account.getBalance(),  0.001);
        assertEquals(700.0, target.getBalance(), 0.001);
    }

    @Test
    void transferThrowsWhenInsufficientFunds() {
        BankAccount target = new BankAccount(0);
        assertThrows(IllegalStateException.class, () -> account.transfer(target, 5000));
    }

    @Test
    void concurrentDepositsAreAccurate() throws InterruptedException {
        int threads = 10, depositsEach = 100;
        double depositAmount = 10.0;
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            workers.add(new Thread(() -> {
                for (int j = 0; j < depositsEach; j++) account.deposit(depositAmount);
            }));
        }
        workers.forEach(Thread::start);
        for (Thread t : workers) t.join();
        double expected = 1000 + threads * depositsEach * depositAmount;
        assertEquals(expected, account.getBalance(), 0.001,
                "All concurrent deposits must be reflected accurately");
    }

    @Test
    void concurrentTransfersNeverDeadlock() throws InterruptedException {
        BankAccount a = new BankAccount(10_000);
        BankAccount b = new BankAccount(10_000);
        List<Thread> workers = new ArrayList<>();
        // Half transfer a→b, half transfer b→a — classic deadlock scenario without ordering
        for (int i = 0; i < 5; i++) {
            workers.add(new Thread(() -> { for (int j = 0; j < 50; j++) a.transfer(b, 1); }));
            workers.add(new Thread(() -> { for (int j = 0; j < 50; j++) b.transfer(a, 1); }));
        }
        workers.forEach(Thread::start);
        for (Thread t : workers) t.join();
        // Total money must be conserved
        assertEquals(20_000.0, a.getBalance() + b.getBalance(), 0.001,
                "Total money must be conserved across all concurrent transfers");
    }
}
