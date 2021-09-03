package com.usim.ulib.notmine;

import java.util.*;

public class HW4Q2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] mn = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();

    }

    static class Trie {
        boolean leaf;
        Trie[] child = new Trie[2];


    }

}
