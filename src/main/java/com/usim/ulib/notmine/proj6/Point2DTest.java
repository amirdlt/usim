package com.usim.ulib.notmine.proj6;

public class Point2DTest {
    public static void main(String[] args) {
        var point = new Point2D(1, 5);
        System.out.println(point);
        System.out.println(point.getX());
        System.out.println(point.getY());
        point.setX(6);
        point.setY(7);
        System.out.println(point);
    }
}
