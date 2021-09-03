package com.usim.ulib.notmine.tmp7;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExamQ4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int[] nums = new int[10];
        for (int i = 0; i < 10; i++) {
            System.out.println("Please enter " + (i + 1) + "-th number: ");
            nums[i] = Integer.parseInt(scanner.nextLine().trim());
        }

        System.out.println("Please enter the number to check its repeats: ");
        int key = Integer.parseInt(scanner.nextLine().trim());

        List<Integer> indexes = new ArrayList<>();
        int count = 0;

        for (int i : nums)
            if (i == key) {
                count++;
                indexes.add(i);
            }

        System.out.println("There is " + count + " number of " + key + " in the array at indexes: " + indexes);
    }
}
