package com.concurrency.beginner.p15;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class FizzBuzzPrinterTest {

    /**
     * Runs all four FizzBuzz threads concurrently and collects output in order.
     */
    private List<String> runFizzBuzz(int n) throws InterruptedException {
        FizzBuzzPrinter printer = new FizzBuzzPrinter(n);
        List<String> output = new ArrayList<>();
        Object outputLock = new Object();

        Runnable appendFizz     = () -> { synchronized (outputLock) { output.add("fizz");     } };
        Runnable appendBuzz     = () -> { synchronized (outputLock) { output.add("buzz");     } };
        Runnable appendFizzBuzz = () -> { synchronized (outputLock) { output.add("fizzbuzz"); } };

        CountDownLatch done = new CountDownLatch(4);

        Thread tFizz = new Thread(() -> {
            try { printer.fizz(appendFizz); }
            catch (InterruptedException ignored) {}
            finally { done.countDown(); }
        });
        Thread tBuzz = new Thread(() -> {
            try { printer.buzz(appendBuzz); }
            catch (InterruptedException ignored) {}
            finally { done.countDown(); }
        });
        Thread tFizzBuzz = new Thread(() -> {
            try { printer.fizzbuzz(appendFizzBuzz); }
            catch (InterruptedException ignored) {}
            finally { done.countDown(); }
        });
        Thread tNumber = new Thread(() -> {
            try { printer.number(i -> { synchronized (outputLock) { output.add(String.valueOf(i)); } }); }
            catch (InterruptedException ignored) {}
            finally { done.countDown(); }
        });

        tFizz.start(); tBuzz.start(); tFizzBuzz.start(); tNumber.start();
        assertTrue(done.await(8, TimeUnit.SECONDS),
                "All threads must finish within the timeout");
        return output;
    }

    @Test
    void fizzBuzz15ProducesCorrectSequence() throws InterruptedException {
        List<String> expected = List.of(
                "1", "2", "fizz", "4", "buzz",
                "fizz", "7", "8", "fizz", "buzz",
                "11", "fizz", "13", "14", "fizzbuzz"
        );
        assertEquals(expected, runFizzBuzz(15),
                "Output must match classic FizzBuzz sequence for n=15");
    }

    @Test
    void singleElementN1() throws InterruptedException {
        assertEquals(List.of("1"), runFizzBuzz(1));
    }

    @Test
    void fizzBuzz3() throws InterruptedException {
        assertEquals(List.of("1", "2", "fizz"), runFizzBuzz(3));
    }

    @Test
    void fizzBuzz5() throws InterruptedException {
        assertEquals(List.of("1", "2", "fizz", "4", "buzz"), runFizzBuzz(5));
    }

    @Test
    void outputHasCorrectLength() throws InterruptedException {
        int n = 30;
        List<String> output = runFizzBuzz(n);
        assertEquals(n, output.size(), "Output must have exactly " + n + " elements");
    }

    @Test
    void allMultiplesOf15AreFizzBuzz() throws InterruptedException {
        List<String> output = runFizzBuzz(30);
        for (int i = 15; i <= 30; i += 15) {
            assertEquals("fizzbuzz", output.get(i - 1),
                    "Position " + i + " must be 'fizzbuzz'");
        }
    }

    @Test
    void noFizzAtMultiplesOf15() throws InterruptedException {
        List<String> output = runFizzBuzz(30);
        // Position 15 and 30 must be "fizzbuzz", not "fizz"
        assertEquals("fizzbuzz", output.get(14));
        assertEquals("fizzbuzz", output.get(29));
    }

    @Test
    void fizzBuzz100IsCorrect() throws InterruptedException {
        List<String> output = runFizzBuzz(100);
        assertEquals(100, output.size());
        // Spot-check key positions
        assertEquals("fizzbuzz", output.get(14));  // 15  -> fizzbuzz
        assertEquals("fizzbuzz", output.get(29));  // 30  -> fizzbuzz
        assertEquals("buzz",     output.get(4));   // 5   -> buzz
        assertEquals("fizz",     output.get(2));   // 3   -> fizz
        assertEquals("buzz",     output.get(99));  // 100 -> buzz (100 % 5 == 0)
        assertEquals("97",       output.get(96));  // 97  -> 97 (prime, no division)
    }
}
