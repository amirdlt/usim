package com.usim.ulib.notmine.proj8;

import java.util.Arrays;

public class QuickSort {

    public static void quickSort(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    private static void quickSort(int[] arr, int start, int end) {
        if (end <= start)
            return;

        var partition = partition(arr, start, end);

        quickSort(arr, start, partition - 1);
        quickSort(arr, partition + 1, end);
    }

    private static int partition(int[] arr, int start, int end) {
        var pivot = arr[end];
        int index = start;
        for (int i = start; i < end; i++)
            if (arr[i] < pivot) {
                var temp = arr[i];
                arr[i] = arr[index];
                arr[index++] = temp;
            }
        var temp = arr[end];
        arr[end] = arr[index];
        arr[index] = temp;
        return index;
    }

    public static void main(String[] args) {
        int[] arr = new int[] { 12, 1, 5, 22, 7, 14, 11 };
        System.out.println("Before Sort: ");
        System.out.println(Arrays.toString(arr));
        quickSort(arr);
		System.out.println("After Sort: ");
        System.out.println(Arrays.toString(arr));
    }
}
