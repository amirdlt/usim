package com.usim.ulib.notmine;

import java.util.*;
import java.util.stream.Collectors;

public class HW4Q1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] mn = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        boolean[] results = new boolean[mn[1]];
        Arrays.fill(results, true);
        int[] firstRow = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        while (mn[0]-- > 1) {
            List<Integer> row = Arrays.stream(scanner.nextLine().split(" ")).map(Integer::parseInt).collect(Collectors.toList());
            for (int i = 0; i < mn[1]; i++) {
                if (!results[i])
                    continue;
                if (!row.contains(firstRow[i]))
                    results[i] = false;
            }
        }
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < mn[1]; i++)
            if (results[i])
                res.add(firstRow[i]);
        Collections.sort(res);
        for (Integer i : res)
            System.out.print(i + " ");

    }
}
