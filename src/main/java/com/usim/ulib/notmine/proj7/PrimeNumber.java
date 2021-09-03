package com.usim.ulib.notmine.proj7;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrimeNumber {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("*".repeat(20) + " Prime number generator " + "*".repeat(20));
        System.out.print("Please enter N(The upper bound): ");
        double upperBound = scanner.nextDouble();
        int num = 1;
        int pre = -1;
        int counter = 0;
        List<Integer> twins = new ArrayList<>();
        System.out.println("These are prime numbers: ");
        while ((num += 1) <= upperBound)
            if (isPrime(num)) {
                System.out.println(++counter + ") " + num);
                if (num - pre == 2)
                    twins.add(pre);
                pre = num;
            }
        System.out.println("These are the twin primes found: ");
        counter = 0;
        for (int p : twins)
            System.out.println(++counter + ") " + p + "  " + (p + 2));
    }

    private static boolean isPrime(long num) {
        if (num < 2)
            return false;
        if (num == 2 || num == 3)
            return true;
        if (num % 2 == 0 || num % 3 == 0)
            return false;
        double sqrt = Math.sqrt(num);
        for (int i = 5; i <= sqrt; i += 6)
            if (num % i == 0)
                return false;
        return true;
    }
}
