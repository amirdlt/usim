package com.usim.ulib.notmine.tmp7;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Q2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine().trim());
        List<Rectangle> rects = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String[] bounds = scanner.nextLine().trim().split(" ");
            rects.add(new Rectangle(Integer.parseInt(bounds[0]), Integer.parseInt(bounds[1]), Integer.parseInt(bounds[2]), Integer.parseInt(bounds[3])));
        }
        int m = Integer.parseInt(scanner.nextLine().trim());
        List<Point> points = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            String[] coordinates = scanner.nextLine().trim().split(" ");
            points.add(new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
        }

        for (Point p : points) {
            boolean flag = false;
            for (int i = rects.size() - 1; i >= 0; i--) {
                if (rects.get(i).contains(p)) {
                    flag = true;
                    System.out.println("window " + (i + 1));
                    break;
                }
            }
            if (!flag)
                System.out.println("background");
        }
    }
}
