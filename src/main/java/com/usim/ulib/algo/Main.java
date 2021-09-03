package com.usim.ulib.algo;

import java.util.*;

public class Main {
    private static void subsequenceCheck(String s, String t) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < t.length(); i++)
            stack.push(t.charAt(i));
        for (int i = s.length() - 1; i >= 0; i--) {
            if (stack.empty()) {
                System.out.println("YES");
                return;
            }
            if (s.charAt(i) == stack.peek())
                stack.pop();
        }
        if (stack.empty())
            System.out.println("YES");
        else
            System.out.println("NO");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = Integer.parseInt(scanner.nextLine().trim());
        while (num-- > 0) {
            scanner.nextLine();
            subsequenceCheck(scanner.nextLine(), scanner.nextLine().trim());
        }
    }
}
