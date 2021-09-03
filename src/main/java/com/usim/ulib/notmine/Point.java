package com.usim.ulib.notmine;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public double distance() {
        return Math.sqrt(x * x + y * y);
    }
}
