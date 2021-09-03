package com.usim.ulib.notmine.proj7;

import java.util.Scanner;

public class PerfectSquareNumber {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Please enter the pattern of the left side digits to find the smallest perfect square: ");
        String left = scanner.nextLine();

        System.out.println("Please enter how many number do you wanna find: ");
        int count = scanner.nextInt();

        long base = (long) Math.sqrt(Long.parseLong(left));

        while (count-- > 0) {
            //noinspection StatementWithEmptyBody
            while (!Long.toString(base * base++).startsWith(left));
            System.out.println(--base + " ^ 2 = " + base * base++);
        }
    }
}
