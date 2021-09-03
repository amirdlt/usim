package com.usim.ulib.notmine.tmp13;

public class Cone {
    private Point center;
    private Point upperPoint;
    private double radius;

    public Cone(Point center, Point upperPoint, double radius) {
        this.center = center;
        this.upperPoint = upperPoint;
        this.radius = radius;
    }

    public double area() {
        return Math.PI * radius * (radius - s());
    }

    public double volume() {
        return Math.PI / 3 * center.distanceFrom(upperPoint);
    }

    public double s() {
        double dis = center.distanceFrom(upperPoint);
        return Math.sqrt(dis * dis + radius * radius);
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public Point getUpperPoint() {
        return upperPoint;
    }

    public void setUpperPoint(Point upperPoint) {
        this.upperPoint = upperPoint;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public String toString() {
        return "Cone{" + "center=" + center + ", upperPoint=" + upperPoint + ", radius=" + radius + '}';
    }
}

