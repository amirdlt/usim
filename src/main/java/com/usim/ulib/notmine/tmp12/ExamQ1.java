package com.usim.ulib.notmine.tmp12;

import java.util.Arrays;
import java.util.Scanner;

public class ExamQ1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter n: ");
        int n = Integer.parseInt(scanner.nextLine());
        int[] array = new int[n];
        System.out.println("Please enter the array: ");
        for (int i = 0; i < n; i++)
            array[i] = Integer.parseInt(scanner.nextLine());
        System.out.println("Please enter k value: ");
        int k = Integer.parseInt(scanner.nextLine());
        System.out.println("initial array: " + Arrays.toString(array));
        for (int i = 0; i < k; i++) {
            int last;
            last = array[n - 1];
            for (int j = n - 1; j > 0; j--)
                array[j] = array[j - 1];
            array[0] = last;
        }
        System.out.println("rotated array: " + Arrays.toString(array));
    }
}
