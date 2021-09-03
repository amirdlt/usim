package com.usim.ulib.notmine.tmp10;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine().trim();
        if (line.isEmpty())
            return;
        StringBuilder sb = new StringBuilder();
        char[] chars = line.toCharArray();
        boolean toUpperCase = false;
        for (char ch : chars) {
            if (Character.isWhitespace(ch)) {
                toUpperCase = true;
                continue;
            }
            if (toUpperCase) {
                sb.append(Character.toUpperCase(ch));
                toUpperCase = false;
            } else {
                sb.append(ch);
            }
        }
        System.out.println(sb);
    }
}
