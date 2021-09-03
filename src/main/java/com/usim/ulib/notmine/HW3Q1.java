//package com.usim.ulib.notmine;

import java.util.*;

public class HW3Q1 {
    private final static Scanner in;

    static {
        in = new Scanner(System.in);
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(in.nextLine());
        MinHeap minHeap = new MinHeap(n);
        int[] nums = Arrays.stream(in.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        for (int num : nums)
            minHeap.push(num);
    }
}

class MinHeap {
    private final int[] heap;
    private int heapSize;

    public MinHeap(int n) {
        heap = new int[n];
        heapSize = 0;
    }

    public void heapify(int index) {
        while (left(index) != -1) {
            int rci = right(index);
            int lci = left(index);
            int min = heap[index] < heap[lci] ? index : lci;
            if (rci != -1)
                min = heap[rci] < heap[min] ? rci : min;
            if (min == index)
                return;
            swap(min == lci ? lci : rci, index);
            index = min == lci ? lci : rci;
        }
    }

    private int left(int index) {
        int res = 2 * index + 1;
        return isOutOfBound(res) ? res : -1;
    }

    private int right(int index) {
        int res = 2 * index + 2;
        return isOutOfBound(res) ? res : -1;
    }

    private boolean isOutOfBound(int index) {
        return index >= 0 && index < heapSize;
    }

    public void push(int value) {
        heap[heapSize] = value;
        int index = heapSize++;
        while (true) {
            int parent = parent(index);
            if (parent == -1 || heap[parent] < heap[index])
                return;
            swap(parent, index);
            index = parent;
        }
    }

    private int parent(int index) {
        if (index < 1)
            return -1;
        return (index-1) / 2;
    }

    private void swap(int index1, int index2) {
        int temp = heap[index1];
        heap[index1] = heap[index2];
        heap[index2] = temp;
        System.out.println(Math.min(heap[index1], heap[index2]) + " " + Math.max(heap[index1], heap[index2]));
    }
}
