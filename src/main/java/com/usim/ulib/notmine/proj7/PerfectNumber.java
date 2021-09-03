package com.usim.ulib.notmine.proj7;

import java.util.Scanner;

public class PerfectNumber {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Please enter the upper bound: ");
        double upperBound = scanner.nextDouble();
        int counter = 1, index = 0;
        System.out.println("These are the perfect numbers: ");
        while ((counter += 1) <= upperBound)
            if (isPerfect(counter))
                System.out.println(counter);

        counter = 0;
        System.out.println("These are the symmetric numbers: ");
        while ((counter += 1) <= upperBound)
            if (isPalindrome(counter))
                System.out.print(counter + (index++ % 5 == 0 ? "\n" : " "));

        System.out.println();

        counter = 0;
        System.out.println("These are the strange numbers: ");
        while ((counter += 1) <= upperBound)
            if (isStrange(counter))
                System.out.print(counter + (index++ % 5 == 0 ? "\n" : " "));
    }

    private static boolean isStrange(long num) {
        num = Math.abs(num);
        long accumulator = 1;
        for (long i = 2; i <= num / 2; i++)
            if (num % i == 0)
                accumulator += i * i;
        return accumulator < num;
    }

    private static boolean isPalindrome(long num) {
        return new StringBuffer(Long.toString(num)).reverse().toString().equals(Long.toString(num));
    }

    private static boolean isPerfect(long num) {
        num = Math.abs(num);
        long accumulator = 1;
        for (long i = 2; i <= num / 2; i++)
            if (num % i == 0)
                accumulator += i;
        return accumulator == num;
    }
}
