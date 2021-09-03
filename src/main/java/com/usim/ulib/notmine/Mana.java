package com.usim.ulib.notmine;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Mana {
    private final static Scanner scanner = new Scanner(System.in);

    private static void q1() {
        int[] counters = new int[4];
        scanner.nextLine();
        Integer[] arr = Arrays.stream(scanner.nextLine().split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
        for (int i : arr)
            counters[i - 1]++;
        int diff = counters[2] - counters[0];
        if (diff == 0) {
            counters[3] += counters[2] + (int) Math.ceil(counters[1] / 2.0);
        } else if (diff < 0) {
            counters[3] += counters[2] + counters[1] / 2;
            if (counters[1] % 2 == 1) {
                diff += diff < -1 ? 2 : 1;
                counters[3]++;
            }
            counters[3] += (int) Math.ceil(-diff / 4.0);
        } else {
            counters[3] += counters[0] + (int) Math.ceil(counters[1] / 2.0) + diff;
        }
        System.out.println(counters[3]);
    }

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
            return gcd(arr[start], arr[start+1]);
        int middle = (start + end) / 2;
        return gcd(gcd(arr, start, middle), gcd(arr, middle + 1, end));
    }

    private static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private static void q3() {
        int num = Integer.parseInt(scanner.nextLine());
        HashSet<Point> set = new HashSet<>();
        while (num-- > 0) {
            String[] hw = scanner.nextLine().split(" ");
            int h = Integer.parseInt(hw[0]) / 2;
            int w = Integer.parseInt(hw[1]) / 2;
            for (int i = 0; i < h; i++)
                for (int j = 0; j < w; j++)
                    set.add(new Point(i, j));
        }
        System.out.println(4 * set.size());
    }

    public static void main(String[] args) {
        q2();
    }
}
