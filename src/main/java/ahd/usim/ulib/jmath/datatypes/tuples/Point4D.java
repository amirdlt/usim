package ahd.usim.ulib.jmath.datatypes.tuples;

import ahd.usim.ulib.jmath.datatypes.functions.Function2D;
import ahd.usim.ulib.jmath.datatypes.functions.FunctionVD;

import java.util.Objects;

@SuppressWarnings("unused")
public class Point4D extends SortableTuple<Double> implements Comparable<Point4D>, AbstractPoint {
    private static int comparatorMode = 0;
    private static FunctionVD comparatorFunction = (xx) -> xx[0];
    public static final int ABS_COMPARE = 0;
    public static final int X_COMPARE = 1;
    public static final int Y_COMPARE = 2;
    public static final int Z_COMPARE = 3;
    public static final int W_COMPARE = 5;
    public static final int FUNCTION_COMPARATOR = 4;

    public static final Point4D NaN = new Point4D(Double.NaN, Double.NaN, Double.NaN, Double.NaN);

    public double x;
    public double y;
    public double z;
    public double w;

    public Point4D(double x, double y, double z, double w) {
        super(x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Point4D() {
        this(0, 0, 0, 0);
    }

    public Point4D(Point4D p) {
        this(p.x, p.y, p.z, p.w);
    }

    public Point4D(Point3D p, double w) {
        this(p.x, p.y, p.z, w);
    }

    @Override
    public double getCoordinate(int numOfCoordinate) {
        //noinspection EnhancedSwitchMigration
        switch (numOfCoordinate) {
            case CoordinateX: return x;
            case CoordinateY: return y;
            case CoordinateZ: return z;
            case CoordinateW: return w;
        }
        return Double.NaN;
    }

    @Override
    public double distanceFromOrigin() {
        return Math.sqrt(x*x + y*y + z*z + w*w);
    }

    @Override
    public int numOfCoordinates() {
        return 4;
    }

    @Override
    public void setCoordinate(int numOfCoordinate, double newValue) {
//noinspection EnhancedSwitchMigration
        switch (numOfCoordinate) {
            case CoordinateX: x = newValue; break;
            case CoordinateY: y = newValue; break;
            case CoordinateZ: z = newValue; break;
            case CoordinateW: w = newValue; break;
        }
    }

    @Override
    public double squareOfDistanceFromOrigin() {
        return x * x + y * y + z * z + w * w;
    }

    public Point4D set(Point4D p) {
        x = p.x;
        y = p.y;
        z = p.z;
        w = p.w;
        return this;
    }

    public Point4D set(double newX, double newY, double newZ, double newW) {
        x = newX;
        y = newY;
        z = newZ;
        w = newW;
        return this;
    }

    public Point4D set(Point4D p, double newW) {
        x = p.x;
        y = p.y;
        z = p.z;
        w = newW;
        return this;
    }

    public Point2D getXY() {
        return new Point2D(x, y);
    }

    public Point2D getXZ() {
        return new Point2D(x, z);
    }

    public Point2D getYZ() {
        return new Point2D(y, z);
    }

    public Point2D getXW() {
        return new Point2D(x, w);
    }

    public Point2D getZW() {
        return new Point2D(z, w);
    }

    public Point2D getYW() {
        return new Point2D(y, w);
    }

    public Point4D addVector(Point4D vector) {
        x += vector.x;
        y += vector.y;
        z += vector.z;
        w += vector.w;
        return this;
    }

    public Point4D addVector(double x, double y, double z, double w) {
        return addVector(new Point4D(x, y, z, w));
    }

    public Point4D affectOnX(Function2D f) {
        x = f.valueAt(x);
        return this;
    }

    public Point4D affectOnY(Function2D f) {
        y = f.valueAt(y);
        return this;
    }

    public Point4D affectOnZ(Function2D f) {
        z = f.valueAt(z);
        return this;
    }

    public Point4D affectOnW(Function2D f) {
        w = f.valueAt(w);
        return this;
    }

    public Point4D affectOnXYZW(Function2D f) {
        x = f.valueAt(x);
        y = f.valueAt(y);
        z = f.valueAt(z);
        w = f.valueAt(w);
        return this;
    }

    public double distanceFrom(Point4D p) {
        return new Point4D(this).addVector(-p.x, -p.y, -p.z, -p.w).distanceFromOrigin();
    }

    public double distanceFrom(double x, double y, double z, double w) {
        return distanceFrom(new Point4D(x, y, z, w));
    }

    public double pointToValue(FunctionVD f) {
        return f.valueAt(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point4D)) return false;
        Point4D point4D = (Point4D) o;
        return Double.compare(point4D.x, x) == 0 &&
                Double.compare(point4D.y, y) == 0 &&
                Double.compare(point4D.z, z) == 0 &&
                Double.compare(point4D.w, w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    @Override
    public String toString() {
        return "Point4D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }

    @SuppressWarnings("EnhancedSwitchMigration")
    @Override
    public int compareTo(Point4D o) {
        switch (comparatorMode) {
            case ABS_COMPARE: return Double.compare(distanceFromOrigin(), o.distanceFromOrigin());
            case X_COMPARE: return Double.compare(x, o.x);
            case Y_COMPARE: return Double.compare(y, o.y);
            case Z_COMPARE: return Double.compare(z, o.z);
            case W_COMPARE: return Double.compare(w, o.w);
            case FUNCTION_COMPARATOR:
                return Double.compare(comparatorFunction.valueAt(x, y, z, w), comparatorFunction.valueAt(o.x, o.y, o.z, o.w));
            default: return 0;
        }
    }

    public static FunctionVD getComparatorFunction() {
        return comparatorFunction;
    }

    public static void setComparatorFunction(FunctionVD comparatorFunction) {
        Point4D.comparatorFunction = comparatorFunction;
    }

    public static void setComparatorMode(int comparatorMode) {
        Point4D.comparatorMode = comparatorMode;
    }

    public static Point4D random() {
        return new Point4D(Math.random(), Math.random(), Math.random(), Math.random());
    }

    public static Point4D random(double xL, double xU, double yL, double yU, double zL, double zU, double wL, double wU) {
        return new Point4D(xL + (xU - xL) * Math.random(), yL + (yU - yL) * Math.random(), zL + (zU - zL) * Math.random(), wL + (wU - wL) * Math.random());
    }
}
