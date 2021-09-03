package com.usim.ulib.notmine.proj6;

public class Line2DComposition {
    private Point2D startPoint;
    private Point2D endPoint;

    public Line2DComposition(Point2D startPoint, Point2D endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Point2D getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point2D startPoint) {
        this.startPoint = startPoint;
    }

    public Point2D getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point2D endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public String toString() {
        return "Line2DComposition{" + "startPoint=" + startPoint + ", endPoint=" + endPoint + '}';
    }
}
