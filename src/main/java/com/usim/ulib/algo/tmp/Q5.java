package com.usim.ulib.algo.tmp;

import java.util.Arrays;
import java.util.Scanner;

public class Q5 {
    private final static Scanner scanner;
    private static long[] nums;
    private static long counter;

    static {
        scanner = new Scanner(System.in);
    }

    public static void main (String[] args) {
        counter = 0;
        nums = new long[scanner.nextInt()];
        Arrays.setAll(nums, i -> scanner.nextLong());
        mergeCount(new long[nums.length], 0, nums.length - 1);
        System.out.println(counter);
    }

    private static void mergeCount(long[] temp, int left, int right) {
        if (right > left) {
            int mid = (right + left) / 2;
            mergeCount(temp, left, mid);
            mergeCount(temp, mid + 1, right);
            merge(temp, left, mid + 1, right);
        }
    }

    private static void merge(long[] temp, int left, int mid, int right) {
        int i = left;
        int j = mid;
        int k = left;
        while (i <= mid - 1 && j <= right)
            if (nums[i] > 2 * nums[j]) {
                counter += mid - i;
                j++;
            } else {
                i++;
            }
        i = left;
        j = mid;
        while (i <= mid - 1 && j <= right)
            temp[k++] = nums[i] <= nums[j] ? nums[i++] : nums[j++];
        while (i <= mid - 1)
            temp[k++] = nums[i++];
        while (j <= right)
            temp[k++] = nums[j++];
        for (i = left; i <= right; i++)
            nums[i] = temp[i];
    }
}
