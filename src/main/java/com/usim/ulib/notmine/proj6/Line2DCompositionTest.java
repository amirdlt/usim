package com.usim.ulib.notmine.proj6;

public class Line2DCompositionTest {
    public static void main(String[] args) {
        var line = new Line2DComposition(new Point2D(1, 2), new Point2D(3, 4));
        System.out.println(line);
        System.out.println(line.getStartPoint());
        line.setEndPoint(new Point2D(5, 6));
        System.out.println(line);
    }
}
