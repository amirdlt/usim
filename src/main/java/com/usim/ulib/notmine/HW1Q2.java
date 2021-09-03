package com.usim.ulib.notmine;

import java.util.Arrays;
import java.util.Scanner;

public class HW1Q2 {
    private final static Scanner scanner = new Scanner(System.in);
    private static void q2() {
        String[] nm = scanner.nextLine().split(" ");
        int n = Integer.parseInt(nm[0]);
        int m = Integer.parseInt(nm[1]);
        Long[] ai = Arrays.stream(scanner.nextLine().split(" ")).map(Long::parseLong).toArray(Long[]::new);
        Long[] bi = Arrays.stream(scanner.nextLine().split(" ")).map(Long::parseLong).toArray(Long[]::new);
        StringBuilder sb = new StringBuilder();
        while (m > 0) {
            int fm = m--;
            sb.append(gcd(Arrays.stream(ai).map(e -> e + bi[bi.length - fm]).toArray(Long[]::new), 0, n)).append(' ');
        }
        System.out.println(sb.substring(0, sb.length() - 1));
    }
    private static long gcd(Long[] arr, int start, int end) {
        if (end - start == 1)
            return arr[start];
        if (end - start == 2)
            return gcd2(arr[start], arr[start+1]);
        int middle = (start + end) / 2;
        return gcd2(gcd(arr, start, middle), gcd(arr, middle + 1, end));
    }
    private static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }
    private static long gcd2(long u, long v) {
        int shift;
        if (u == 0) return v;
        if (v == 0) return u;
//        while (u < 0) u += v;
//        while (v < 0) v += u;
        shift = Long.numberOfTrailingZeros(u | v);
        u >>= Long.numberOfTrailingZeros(u);
        do {
            v >>= Long.numberOfTrailingZeros(v);
            if (u > v) {
                long t = v;
                v = u;
                u = t;
            }
            v = v - u;
        } while (v != 0);
        return u << shift;
    }
    public static void main(String[] args) {
        q2();
    }
}
