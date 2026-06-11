package com.concurrency.intermediate.p17;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ParallelMatrixMultiplierTest {

    private final ParallelMatrixMultiplier multiplier = new ParallelMatrixMultiplier();

    private int[][] seq(int[][] a, int[][] b) {
        int n = a.length;
        int[][] r = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    r[i][j] += a[i][k] * b[k][j];
        return r;
    }

    @Test
    void multiply1x1() throws Exception {
        int[][] a = {{7}}, b = {{3}};
        assertArrayEquals(new int[][]{{21}}, multiplier.multiply(a, b));
    }

    @Test
    void multiply2x2() throws Exception {
        int[][] a = {{1, 2}, {3, 4}};
        int[][] b = {{5, 6}, {7, 8}};
        assertArrayEquals(seq(a, b), multiplier.multiply(a, b));
    }

    @Test
    void multiply3x3() throws Exception {
        int[][] a = {{1,2,3},{4,5,6},{7,8,9}};
        int[][] b = {{9,8,7},{6,5,4},{3,2,1}};
        assertArrayEquals(seq(a, b), multiplier.multiply(a, b));
    }

    @Test
    void multiplyIdentityMatrix() throws Exception {
        int n = 4;
        int[][] identity = new int[n][n];
        for (int i = 0; i < n; i++) identity[i][i] = 1;
        int[][] a = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,16}};
        assertArrayEquals(a, multiplier.multiply(a, identity));
        assertArrayEquals(a, multiplier.multiply(identity, a));
    }

    @Test
    void multiply5x5IsCorrect() throws Exception {
        int[][] a = new int[5][5], b = new int[5][5];
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) { a[i][j] = i*5+j+1; b[i][j] = (i+j)%7; }
        assertArrayEquals(seq(a, b), multiplier.multiply(a, b));
    }
}
