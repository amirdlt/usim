package com.usim.ulib.notmine.tmp7;

import java.util.Scanner;

public class ExamQ1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[][] matrix = new int[3][3];

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                System.out.println("Please enter " + (i+1) + " " + (j+1) + " element of matrix: ");
                matrix[i][j] = Integer.parseInt(scanner.nextLine().trim());
            }

        boolean isUpperTriangular = true;

        for (int i = 1; i < 3; i++)
            for (int j = 0; j < i; j++)
                if (matrix[i][j] != 0)
                    isUpperTriangular = false;
        System.out.println(isUpperTriangular ? "This matrix is upper triangular" : "This matrix is not upper triangular");
    }
}
