package com.usim.ulib.notmine.proj6;

public class Line2DInheritance extends Point2D {
    private Point2D endPoint;

    public Line2DInheritance(Point2D startPoint, Point2D endPoint) {
        setX(startPoint.getX());
        setY(startPoint.getY());
        this.endPoint = endPoint;
    }

    public void setStartPoint(Point2D startPoint) {
        setX(startPoint.getX());
        setY(startPoint.getY());
    }

    public Point2D getStartPoint() {
        return this;
    }

    public Point2D getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point2D endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public String toString() {
        return "Line2DInheritance{" + "startPoint=" + this.getX() + ", " + this.getY() + "endPoint=" + endPoint + '}';
    }
}
