package com.usim.ulib.notmine;
import java.util.HashSet;
import java.util.Scanner;

public class HW1Q3 {
    private final static Scanner scanner = new Scanner(System.in);
//    private static void q3() {
//        int num = Integer.parseInt(scanner.nextLine());
//        HashSet<Point> set = new HashSet<>();
//        while (num-- > 0) {
//            String[] hw = scanner.nextLine().split(" ");
//            int h = Integer.parseInt(hw[0]) / 2;
//            int w = Integer.parseInt(hw[1]) / 2;
//            for (int i = 0; i < h; i++)
//                for (int j = 0; j < w; j++)
//                    set.add(new Point(i, j));
//        }
//        System.out.println(4 * set.size());
//    }
    private static void q3() {
        int num = Integer.parseInt(scanner.nextLine());
        HashSet<java.awt.Point> set = new HashSet<>();
        while (num-- > 0) {
            String[] hw = scanner.nextLine().split(" ");
            int h = Integer.parseInt(hw[0]) / 2;
            int w = Integer.parseInt(hw[1]) / 2;
            for (java.awt.Point p : set) {
                if (w < p.x)
                    w = p.x - w;
                if (h < p.y)
                    h = p.y - h;
            }
            set.add(new java.awt.Point(w, h));
        }
        int res = 0;
        for (java.awt.Point p : set)
            res += p.x * p.y;
        System.out.println(res * 4);
    }
    public static void main(String[] args) {
        q3();
    }

}
