package com.usim.ulib.notmine.tmp14;

import java.util.Arrays;

public class ExamQ3 {
    public static void main(String[] args) {
        int[] array = new int[] { 23, 5, 35, 64, 32, 14, 6, 75, 34 };
        int sum = 0;
        for (int a : array)
            sum += a;
        double average = (double) sum / array.length;
        System.out.println("The average of numbers " + Arrays.toString(array) + " is: " + average);
    }
}
