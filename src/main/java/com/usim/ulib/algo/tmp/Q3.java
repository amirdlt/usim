package com.usim.ulib.algo.tmp;

import java.util.Arrays;
import java.util.Scanner;

public class Q3 {
    private static final Scanner scanner;

    static {
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        long[] array = new long[scanner.nextInt()];
        Arrays.setAll(array, i -> scanner.nextInt());
        long[] pre = new long[array.length + 1];
        long[] suf = new long[array.length + 1];
        long res = 0;
        long pow = 2;
        pre[0] = 0;
        suf[array.length] = 0;
        for (int i = 0; i < array.length; i++) {
            pre[i + 1] = pre[i] | array[i];
            suf[array.length - i - 1] = suf[array.length - i] | array[array.length - i - 1];
        }
        for (int i = 0; i < array.length; i++)
            res = Math.max(res, pre[i] | (array[i] * pow) | suf[i + 1]);
        System.out.println(res);
    }
}
