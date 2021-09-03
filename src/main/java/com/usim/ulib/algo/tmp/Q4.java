package com.usim.ulib.algo.tmp;

import java.util.Arrays;
import java.util.Scanner;

public class Q4 {
    private static final Scanner scanner;

    static {
        scanner = new Scanner(System.in);
    }

    public static void main (String[] args) {
        long[] info = Arrays.stream(scanner.nextLine().split(" ")).mapToLong(Long::parseLong).toArray();
        long c1 = info[2];
        long c2 = info[3];
        char[] s1 = scanner.nextLine().toCharArray();
        char[] s2 = scanner.nextLine().toCharArray();
        int[][] temp = new int[s1.length + 1][s2.length + 1];
        int res = 0;
        for (int i = 0; i <= s1.length; i++)
            for (int j = 0; j <= s2.length; j++)
                if (i == 0 || j == 0) {
                    temp[i][j] = 0;
                } else if (s1[i - 1] == s2[j - 1]) {
                    temp[i][j] = temp[i - 1][j - 1] + 1;
                    res = Integer.max(res, temp[i][j]);
                } else {
                    temp[i][j] = Math.max(temp[i - 1][j], temp[i][j - 1]);
                }
        System.out.println(c1 * (s1.length - res) + c2 * (s2.length - res));
    }
}
