package com.concurrency.beginner.p15;

import java.util.function.IntConsumer;

/**
 * Problem 15 – FizzBuzz with Four Threads
 *
 * Four threads run fizz(), buzz(), fizzbuzz(), and number() concurrently.
 * They must collectively produce the correct FizzBuzz sequence for 1..n
 * by coordinating access to a shared counter.
 *
 * Use synchronized + wait()/notifyAll() (or Semaphores) to coordinate.
 */
public class FizzBuzzPrinter {

    private final int n;
    private int current = 1;  // shared counter — the number to process next

    public FizzBuzzPrinter(int n) {
        this.n = n;
    }

    /**
     * Runs on the "fizz" thread.
     * Calls printFizz.run() for every multiple of 3 (but NOT 15) in 1..n.
     */
    public void fizz(Runnable printFizz) throws InterruptedException {
        // TODO: loop while current <= n
        //   wait until current is divisible by 3 but NOT 15 (or current > n)
        //   invoke printFizz.run(), increment current, notifyAll()
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Runs on the "buzz" thread.
     * Calls printBuzz.run() for every multiple of 5 (but NOT 15) in 1..n.
     */
    public void buzz(Runnable printBuzz) throws InterruptedException {
        // TODO: symmetric to fizz() but checks % 5 != 0 || % 15 == 0
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Runs on the "fizzbuzz" thread.
     * Calls printFizzBuzz.run() for every multiple of 15 in 1..n.
     */
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        // TODO: symmetric to fizz() but checks % 15 == 0
        throw new UnsupportedOperationException("Implement this method");
    }

    /**
     * Runs on the "number" thread.
     * Calls printNumber.accept(i) for every number in 1..n NOT divisible by 3 or 5.
     */
    public void number(IntConsumer printNumber) throws InterruptedException {
        // TODO: symmetric to fizz() but checks % 3 != 0 && % 5 != 0
        throw new UnsupportedOperationException("Implement this method");
    }
}
