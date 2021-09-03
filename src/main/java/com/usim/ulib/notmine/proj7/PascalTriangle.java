package com.usim.ulib.notmine.proj7;

import java.util.Scanner;

public class PascalTriangle {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Please enter the height of the triangle: ");
        int height = scanner.nextInt();
        displayPascalTriangle(height);
    }

    private static void displayPascalTriangle(int height) {
        for (int i = 1; i <= height; i++) {
            int a = 1;
            System.out.print(" ".repeat(height - i));
            for (int j = 1; j <= i; j++) {
                System.out.print(a + "  ");
                a *= (double) (i - j) / j;
            }
            System.out.println();
        }
    }
}
