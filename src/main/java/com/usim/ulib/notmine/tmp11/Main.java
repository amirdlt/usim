package com.usim.ulib.notmine.tmp11;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine().trim());
        int[] nums = new int[n];
        int count = 0;
        while (count < n)
            nums[count++] = scanner.nextInt();
        int max = nums[0] * nums[1] * nums[2];
        int temp;
        for (int i = 1; i < n - 2; i++)
            for (int j = i + 1; j < n - 1; j++)
                for (int k = j + 1; k < n; k++)
                    if (max < (temp = nums[i] * nums[j] * nums[k]))
                        max = temp;
        System.out.println(max);
    }
}
