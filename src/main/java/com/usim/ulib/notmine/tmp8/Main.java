package com.usim.ulib.notmine.tmp8;

public class Main {
    public static <T> int countInArray(T[] array, T key) {
        int count = 0;
        for (T k : array)
            if (k.equals(key))
                count++;
        return count;
    }
}
