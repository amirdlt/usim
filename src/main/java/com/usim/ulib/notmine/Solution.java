package com.usim.ulib.notmine;

import java.util.Arrays;
import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var mode = scanner.nextLine().trim();

        if (mode.equalsIgnoreCase("hasConflict")) {
            var line1 = Arrays.stream(scanner.nextLine().trim().split(" ")).mapToInt(Integer::parseInt).toArray();
            var line2 = Arrays.stream(scanner.nextLine().trim().split(" ")).mapToInt(Integer::parseInt).toArray();
            System.out.println(isConflict(line1, line2) ? "no" : "yes");
        } else if (mode.equalsIgnoreCase("numOfConflict")) {
            var numOfLines = Integer.parseInt(scanner.nextLine().trim());
            var lines = new int[numOfLines][];
            int count = 0;
            while (numOfLines-- > 0)
                lines[count++] = Arrays.stream(scanner.nextLine().trim().split(" ")).mapToInt(Integer::parseInt).toArray();
            numOfLines = count;
            count = 0;
            for (int i = 0; i < numOfLines - 1; i++)
                for (int j = i + 1; j < numOfLines; j++)
                    if (isConflict(lines[i], lines[j]))
                        count++;
            System.out.println(count);
        }
    }

    private static boolean isConflict(int[] line1, int[] line2) {
        return (line1[0] - line1[2]) * (line2[1] - line2[3]) != (line2[0] - line2[2]) * (line1[1] - line1[3]);
    }
}
