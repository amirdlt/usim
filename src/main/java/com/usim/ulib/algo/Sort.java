package com.usim.ulib.algo;

import com.usim.ulib.utils.Utils;

import java.util.Arrays;

public final class Sort {
    public static void mergeSort(int[] arr) {
        mergeSort(arr, 0, arr.length - 1);
    }

    private static void mergeSort(int[] arr, int start, int end) {
        if (end <= start)
            return;
        var m = (start + end) / 2;

        mergeSort(arr, start, m);
        mergeSort(arr, m + 1, end);

        merge(arr, start, m, end);
    }

    private static void merge(int[] arr, int start, int middle, int end) {
        int kIndex = start;
        int rIndex = 0;
        int lIndex = 0;
        int[] left = Arrays.copyOfRange(arr, start, middle + 1);
        int[] right = Arrays.copyOfRange(arr, middle + 1, end + 1);
        while (rIndex < right.length && lIndex < left.length)
            if (left[lIndex] < right[rIndex]) {
                arr[kIndex++] = left[lIndex++];
            } else {
                arr[kIndex++] = right[rIndex++];
            }
        while (lIndex < left.length)
            arr[kIndex++] = left[lIndex++];
        while (rIndex < right.length)
            arr[kIndex++] = right[rIndex++];
    }

    //////////////

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

    /////////////

    public static void selectionSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++)
            for (int j = i + 1; j < arr.length; j++)
                if (arr[i] > arr[j]) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
    }

    /////////////

    public static void bubbleSort(int[] arr) {
        int i, j, temp;
        boolean swapped;
        var n = arr.length;
        for (i = 0; i < n - 1; i++) {
            swapped = false;
            for (j = 0; j < n - i - 1; j++)
                if (arr[j] > arr[j + 1]) {
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            if (!swapped)
                break;
        }
    }

    /////////////

    public static void recursiveBubbleSort(int[] arr) {
        recursiveBubbleSort(arr, arr.length);
    }

    public static void recursiveBubbleSort(int[] arr, int len) {
        if (len == 1)
            return;
        for (int i = 0; i < len - 1; i++)
            if (arr[i] > arr[i + 1]) {
                int temp = arr[i];
                arr[i] = arr[i + 1];
                arr[i + 1] = temp;
            }
        recursiveBubbleSort(arr, len-1);
    }

    ////////////

    public static void heapSort(int[] arr) {
        int n = arr.length;

        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(arr, n, i);

        for (int i = n - 1; i > 0; i--) {
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            heapify(arr, i, 0);
        }
    }

    private static void heapify(int[] arr, int n, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;

        if (l < n && arr[l] > arr[largest])
            largest = l;

        if (r < n && arr[r] > arr[largest])
            largest = r;

        if (largest != i) {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;

            heapify(arr, n, largest);
        }
    }

    ////////////
    public static void main(String[] args) {
        int len = 1_000_000;
        var random = new int[len];
        Arrays.setAll(random, i -> (int) (Math.random() * Integer.MAX_VALUE));
        var clone = new int[len];

        System.arraycopy(random, 0, clone, 0, len);
        Utils.checkTimePerform(() -> Arrays.sort(clone), true, "Arrays::sort");

        System.arraycopy(random, 0, clone, 0, len);
        Utils.checkTimePerform(() -> mergeSort(clone), true, "Sort::mergeSort");

        System.arraycopy(random, 0, clone, 0, len);
        Utils.checkTimePerform(() -> heapSort(clone), true, "Sort::heapSort");

        System.arraycopy(random, 0, clone, 0, len);
        Utils.checkTimePerform(() -> quickSort(clone), true, "Sort::quickSort");

        System.arraycopy(random, 0, clone, 0, len);
        Utils.checkTimePerform(() -> selectionSort(clone), true, "Sort::selectionSort");

        System.arraycopy(random, 0, clone, 0, len);
        Utils.checkTimePerform(() -> bubbleSort(clone), true, "Sort::bubbleSort");

//        System.arraycopy(random, 0, clone, 0, len);
//        Utils.checkTimePerform(() -> recursiveBubbleSort(clone), true, "Sort::recursiveBubbleSort");
//        System.out.println(Arrays.toString(Arrays.copyOfRange(clone, 0, 20)));
    }

    /////////////
}
