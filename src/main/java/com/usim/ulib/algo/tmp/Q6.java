package com.usim.ulib.algo.tmp;

import java.util.*;
import java.util.stream.*;

public class Q6 {
    private static final Scanner scanner;
    private static final Map<Long, Integer> indexMap;

    static {
        scanner = new Scanner(System.in);
        indexMap = new HashMap<>();
    }

    public static void main(String[] args) {
        final long[] array = new long[scanner.nextInt()];
        final long c = scanner.nextLong();
        for (int i = 0; i < array.length; i++)
            indexMap.put(array[i] = scanner.nextLong(), i + 1);
        if (c == 0) {
            System.out.println(array.length + " " + array.length);
            System.exit(0);
        }
        Arrays.sort(array);
        final long[] clone = array.clone();
        int from = 0;
        int to = 1;
        long diff;
        while (from <= to && to < array.length)
            if ((diff = array[to] - array[from] - c) == 0) {
                int[] indexes = IntStream.of(indexMap.get(array[from]) - 1, indexMap.get(array[to]) - 1).sorted().toArray();
                long[] values = LongStream.of(clone[indexes[0]], clone[indexes[1]]).sorted().toArray();
                if (Arrays.stream(Arrays.copyOfRange(clone, indexes[0], indexes[1])).noneMatch(e -> e < values[0] || e > values[1])) {
                    System.out.println(++indexes[0] + " " + ++indexes[1]);
                    System.exit(0);
                } else {
                    from++;
                    to++;
                }
            } else if (diff < 0) {
                to++;
            } else {
                from++;
            }
        System.out.println("-1");
    }
}
