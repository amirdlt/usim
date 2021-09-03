package com.usim.ulib.algo.tmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Q1 {
    private static final Scanner scanner;
    private static char[] string1, string2;

    static {
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        int numOfQueries = Integer.parseInt(scanner.nextLine());
        List<String> results = new ArrayList<>();
        while (numOfQueries-- > 0) {
            scanner.nextLine();
            string1 = scanner.nextLine().toCharArray();
            string2 = scanner.nextLine().toCharArray();
            results.add(check());
        }
        results.forEach(System.out::println);
    }

    private static String check() {
        int index = 0;
        for (char c : string1) {
            boolean flag = false;
            for (int i = index; i < string2.length; i++)
                if (c == string2[i]) {
                    flag = true;
                    index = i + 1;
                    break;
                }
            if (!flag)
                return "NO";
        }
        return "YES";
    }
}
