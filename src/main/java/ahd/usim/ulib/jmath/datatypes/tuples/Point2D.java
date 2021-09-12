package ahd.usim.ulib.jmath.datatypes.tuples;

import ahd.usim.ulib.jmath.datatypes.functions.Function2D;
import ahd.usim.ulib.jmath.datatypes.functions.Function3D;

import java.awt.*;
import java.util.Objects;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Point2D extends SortableTuple<Double> implements Comparable<Point2D>, AbstractPoint {
    private static int comparatorMode = 0;
    private static Function3D comparatorFunction;
    public static final int ABS_COMPARE = 0;
    public static final int X_COMPARE = 1;
    public static final int Y_COMPARE = 2;
    public static final int FUNCTION_COMPARATOR = 3;

    public static final Point2D NaN = new Point2D(Double.NaN, Double.NaN);

    public double x;
    public double y;

    public Point2D(double x, double y) {
        super(x, y);
        this.x = x;
        this.y = y;
        comparatorFunction = (xx, yy) -> xx;
    }

    public Point2D(Point2D p) {
        this(p.x, p.y);
    }

    public Point2D() {
        this(0, 0);
    }

    public Point2D(Point p) {
        this(p.x, p.y);
    }

    @Override
    public double distanceFromOrigin() {
        return Math.sqrt(x*x + y*y);
    }

    public Point2D set(double newX, double newY) {
        x = newX;
        y = newY;
        return this;
    }

    public Point2D set(Point2D p) {
        set(p.x, p.y);
        return this;
    }

    public Point2D addVector(double vx, double vy) {
        x += vx;
        y += vy;
        return this;
    }

    public Point2D addVector(Point2D vector) {
        addVector(vector.x, vector.y);
        return this;
    }

    public Point2D subVector(Point2D vector) {
        addVector(-vector.x, -vector.y);
        return this;
    }

    public Point2D affectFunctionToX(Function2D f) {
        x = f.valueAt(x);
        return this;
    }

    public Point2D affectFunctionToY(Function2D f) {
        y = f.valueAt(y);
        return this;
    }

    public Point2D affectFunctionToXY(Function2D f) {
        x = f.valueAt(x);
        y = f.valueAt(y);
        return this;
    }

    public Point2D rotate(Point2D centerOfRotation, double angleInRadians) {
        double sin = Math.sin(angleInRadians);
        double cos = Math.cos(angleInRadians);
        return this.set(cos *(x - centerOfRotation.x) - sin * (y - centerOfRotation.y) + centerOfRotation.x,
                sin * (x - centerOfRotation.x) + cos * (y - centerOfRotation.y) + centerOfRotation.y);
    }

    public Point2D rotate(double angleInRadians) {
        return this.rotate(new Point2D(), angleInRadians);
    }

    public Point2D inverse() {
        return set(y, x);
    }

    public double distanceFrom(double x, double y) {
        return Math.sqrt((this.x - x)*(this.x - x) + (this.y - y)*(this.y - y));
    }

    public double distanceFrom(Point2D p) {
        return distanceFrom(p.x, p.y);
    }

    public static void setComparatorMode(int mode) {
        comparatorMode = mode;
    }

    public static Point2D random(double xLowBound, double xUpBound, double yLowBound, double yUpBound) {
        return new Point2D(Math.random() * (xUpBound - xLowBound) + xLowBound,
                Math.random() * (yUpBound - yLowBound) + yLowBound);
    }

    public static Point2D random() {
        return new Point2D(Math.random(), Math.random());
    }

    public static Function3D getComparatorFunction() {
        return comparatorFunction;
    }

    public static void setComparatorFunction(Function3D comparatorFunction) {
        Point2D.comparatorFunction = comparatorFunction;
    }

    public Point2D getCopy() {
        return new Point2D(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point2D)) return false;
        Point2D point2D = (Point2D) o;
        return Double.compare(point2D.x, x) == 0 &&
                Double.compare(point2D.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @SuppressWarnings("EnhancedSwitchMigration")
    @Override
    public int compareTo(Point2D o) {
        switch (comparatorMode) {
            case ABS_COMPARE: return Double.compare(distanceFromOrigin(), o.distanceFromOrigin());
            case X_COMPARE: return Double.compare(x, o.x);
            case Y_COMPARE: return Double.compare(y, o.y);
            case FUNCTION_COMPARATOR:
                return Double.compare(comparatorFunction.valueAt(x, y), comparatorFunction.valueAt(o.x, o.y));
            default: return 0;
        }
    }

    @Override
    public double getCoordinate(int numOfCoordinate) {
        //noinspection EnhancedSwitchMigration
        switch (numOfCoordinate) {
            case CoordinateX: return x;
            case CoordinateY: return y;
        }
        return Double.NaN;
    }

    @Override
    public int numOfCoordinates() {
        return 2;
    }

    @Override
    public void setCoordinate(int numOfCoordinate, double newValue) {
        //noinspection EnhancedSwitchMigration
        switch (numOfCoordinate) {
            case CoordinateX: x = newValue; break;
            case CoordinateY: y = newValue; break;
        }
    }

    @Override
    public double squareOfDistanceFromOrigin() {
        return x * x + y * y;
    }
    
    
}
