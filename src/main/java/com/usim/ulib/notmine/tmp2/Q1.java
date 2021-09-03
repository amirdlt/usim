package com.usim.ulib.notmine.tmp2;

import java.util.Scanner;

public final class Q1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] line = scanner.nextLine().split(" ");
        int sum = 0;
        for (String word : line)
            try {
                sum += Integer.parseInt(word);
            } catch (NumberFormatException ignore) {}
        System.out.println(sum);
    }
}
