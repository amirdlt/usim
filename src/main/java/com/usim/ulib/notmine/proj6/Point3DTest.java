package com.usim.ulib.notmine.proj6;

public class Point3DTest {
    public static void main(String[] args) {
        var point = new Point3D(1, 5, 10);
        System.out.println(point);
        System.out.println(point.getX());
        System.out.println(point.getY());
        System.out.println(point.getZ());
        point.setX(6);
        point.setY(7);
        point.setZ(8);
        System.out.println(point);
    }
}
