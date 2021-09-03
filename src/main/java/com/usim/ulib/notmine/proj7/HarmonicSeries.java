package com.usim.ulib.notmine.proj7;

import java.util.Scanner;

public class HarmonicSeries {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Please enter number of terms: ");
        int numOfTerms = scanner.nextInt();
        int counter = numOfTerms;
        double result = 0;
        while (counter > 0)
            result += 1.0 / counter--;
        System.out.println("The harmonic series with " + numOfTerms + " terms = " + result);

        double pi = 0;
        double innerFactor = -1.0 / 3;
        double cofactor = -3;
        while (numOfTerms-- > 0)
            pi += (cofactor *= innerFactor) / (2 * counter++ + 1);
        pi *= Math.sqrt(12);
        System.out.println("PI approximation: " + pi);

        counter = Math.max(60, Math.min(200, counter));
        double step = Math.PI / counter;
        double x = -step;
        double yStep = 2.0 / Math.max(5, Math.min(20, (int) (2 / step) + 1));
        char[][] sine = new char[(int) (2 / yStep) + 1][counter + 1];
        while ((x += step) <= Math.PI)
            sine[(int) ((-Math.sin(x * 6) + 1) / yStep)][(int) (x / step)] = '$';
        for (char[] line : sine) {
            for (int i = 0; i < line.length; i++)
                line[i] = line[i] == 0 ? ' ' : line[i];
            System.out.println(new String(line));
        }
    }
}
