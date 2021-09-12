package ahd.usim.ulib.jmath.datatypes.tuples;

import ahd.usim.ulib.jmath.datatypes.matrix.MatUtils;
import ahd.usim.ulib.visualization.canvas.CoordinatedScreen;
import ahd.usim.ulib.jmath.datatypes.functions.Function2D;
import ahd.usim.ulib.jmath.datatypes.functions.Function4D;
import ahd.usim.ulib.jmath.datatypes.functions.Mapper3D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings({ "unused", "UnusedReturnValue", "SuspiciousNameCombination" })
public class Point3D extends SortableTuple<Double> implements Comparable<Point3D>, AbstractPoint {
    private static int comparatorMode = 0;
    private static Function4D comparatorFunction = (xx, yy, zz) -> xx;
    public static final int ABS_COMPARE = 0;
    public static final int X_COMPARE = 1;
    public static final int Y_COMPARE = 2;
    public static final int Z_COMPARE = 3;
    public static final int FUNCTION_COMPARATOR = 4;

    public static final Point3D NaN = of(Double.NaN, Double.NaN, Double.NaN);

    public double x;
    public double y;
    public double z;

    public Point3D(double x, double y, double z) {
        super(x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D() {
        this(0, 0, 0);
    }

    public Point3D(@NotNull Point3D p) {
        this(p.x, p.y, p.z);
    }

    public Point3D(Point2D p, double z) {
        this(p.x, p.y, z);
    }

    public Point3D(Point2D p) {
        this(p.x, p.y, 0);
    }

    @Override
    public double getCoordinate(int numOfCoordinate) {
        //noinspection EnhancedSwitchMigration
        switch (numOfCoordinate) {
            case CoordinateX: return x;
            case CoordinateY: return y;
            case CoordinateZ: return z;
        }
        return Double.NaN;
    }

    @Override
    public double distanceFromOrigin() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    @Override
    public int numOfCoordinates() {
        return 3;
    }

    @Override
    public void setCoordinate(int numOfCoordinate, double newValue) {
        //noinspection EnhancedSwitchMigration
        switch (numOfCoordinate) {
            case CoordinateX: x = newValue; break;
            case CoordinateY: y = newValue; break;
            case CoordinateZ: z = newValue; break;
        }
    }

    @Override
    public double squareOfDistanceFromOrigin() {
        return x * x + y * y + z * z;
    }

    public Point3D set(@NotNull Point3D p) {
        x = p.x;
        y = p.y;
        z = p.z;
        return this;
    }

    public Point3D set(double newX, double newY, double newZ) {
        x = newX;
        y = newY;
        z = newZ;
        return this;
    }

    public Point3D set(Point2D p, double newZ) {
        x = p.x;
        y = p.y;
        z = newZ;
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

    public Point3D addVector(Point3D vector) {
        x += vector.x;
        y += vector.y;
        z += vector.z;
        return this;
    }

    public Point3D addVector(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Point3D affectOnX(Function2D f) {
        x = f.valueAt(x);
        return this;
    }

    public Point3D affectOnY(Function2D f) {
        y = f.valueAt(y);
        return this;
    }

    public Point3D affectOnZ(Function2D f) {
        z = f.valueAt(z);
        return this;
    }

    public Point3D affectOnXYZ(Function2D f) {
        x = f.valueAt(x);
        y = f.valueAt(y);
        z = f.valueAt(z);
        return this;
    }

    public Point3D rotate(double xAngle, double yAngle, double zAngle) {
        var p = getYZ().rotate(xAngle);
        set(x, p.x, p.y);
        p = getXZ().rotate(yAngle);
        set(p.x, y, p.y);
        p = getXY().rotate(zAngle);
        set(p.x, p.y, z);
        return this;
    }

    public Point3D rotate(Point3D center, double xAngle, double yAngle, double zAngle) {
        var p = getYZ().rotate(center.getYZ(), xAngle);
        set(x, p.x, p.y);
        p = getXZ().rotate(center.getXZ(), yAngle);
        set(p.x, y, p.y);
        p = getXY().rotate(center.getXY(), zAngle);
        set(p.x, p.y, z);
        return this;
    }

    public Point3D affectMapper(Mapper3D... mappers) {
        for (var m : mappers)
            set(m.valueAt(this));
        return this;
    }

    public Point3D getCopy() {
        return new Point3D(this);
    }

    public Point3D crossProduct(Point3D p) {
        x = x * p.y - y * p.x;
        y = z * p.x - x * p.z;
        z = y * p.z - z * p.y;
        return this;
    }

    public Point3D normalize() {
        var magnitude = distanceFromOrigin();
        if (magnitude == 1)
            return this;
        x /= magnitude;
        y /= magnitude;
        z /= magnitude;
        return this;
    }

    public Point3D affectMatrix(double[][] mat) {
        var newX = x * mat[0][0] + x * mat[0][1] + x * mat[0][2];
        var newY = x * mat[1][0] + x * mat[1][1] + x * mat[1][2];
        var newZ = x * mat[2][0] + x * mat[2][1] + x * mat[2][2];
        set(newX, newY, newZ);
        return this;
    }

    public Point3D add(Point3D... points) {
        Arrays.stream(points).forEach(this::addVector);
        return this;
    }

    public Point3D sub(Point3D p) {
        x -= p.x;
        y -= p.y;
        z -= p.z;
        return this;
    }

    public Point3D sub(Point3D... points) {
        Arrays.stream(points).forEach(this::sub);
        return this;
    }

    public Point onScreenAddress(CoordinatedScreen cs) {
        return cs.screen(this);
    }

    @Deprecated
    public Point3D rotate(Point3D currentDirectionVector, Point3D newDirectionVector) {
        return affectMatrix(matrixToConvertDirection(currentDirectionVector, newDirectionVector));
    }

    public double dotProduct(Point3D p) {
        return x * p.x + y * p.y + z * p.z;
    }

    public double distanceFrom(Point3D p) {
        return new Point3D(this).addVector(-p.x, -p.y, -p.z).distanceFromOrigin();
    }

    public double distanceFrom(double x, double y, double z) {
        return new Point3D(this).addVector(-x, -y, -z).distanceFromOrigin();
    }

    public double pointToValue(Function4D f) {
        return f.valueAt(this);
    }

    public Point2D asPoint2D(double xAngle, double yAngle, double zAngle) {
        var tmp = new Point3D(this).rotate(xAngle, yAngle, zAngle);
        return new Point2D(tmp.x, tmp.y);
    }

    public Point3D immutable() {
        return immutable(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point3D point3D)) return false;
        return Double.compare(point3D.x, x) == 0 &&
                Double.compare(point3D.y, y) == 0 &&
                Double.compare(point3D.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "Point3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @SuppressWarnings("EnhancedSwitchMigration")
    @Override
    public int compareTo(@NotNull Point3D o) {
        switch (comparatorMode) {
            case ABS_COMPARE: return Double.compare(distanceFromOrigin(), o.distanceFromOrigin());
            case X_COMPARE: return Double.compare(x, o.x);
            case Y_COMPARE: return Double.compare(y, o.y);
            case Z_COMPARE: return Double.compare(z, o.z);
            case FUNCTION_COMPARATOR:
                return Double.compare(comparatorFunction.valueAt(x, y, z), comparatorFunction.valueAt(o.x, o.y, o.z));
            default: return 0;
        }
    }

    public static Function4D getComparatorFunction() {
        return comparatorFunction;
    }

    public static void setComparatorFunction(Function4D comparatorFunction) {
        Point3D.comparatorFunction = comparatorFunction;
    }

    public static void setComparatorMode(int comparatorMode) {
        Point3D.comparatorMode = comparatorMode;
    }

    public static Point3D random() {
        return new Point3D(Math.random(), Math.random(), Math.random());
    }

    public static Point3D random(double xL, double xU, double yL, double yU, double zL, double zU) {
        return new Point3D(xL + (xU - xL) * Math.random(), yL + (yU - yL) * Math.random(), zL + (zU - zL) * Math.random());
    }

    public static Point3D crossProduct(Point3D v1, Point3D v2) {
        return new Point3D(
                v1.x * v2.y - v1.y * v2.x,
                v1.z * v2.x - v1.x * v2.z,
                v1.y * v2.z - v1.z * v2.y
                );
    }

    public static double dotProduct(Point3D v1, Point3D v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static Point3D sum(Point3D... points) {
        var res = new Point3D();
        Arrays.stream(points).forEach(res::addVector);
        return res;
    }

    public static Point3D average(Point3D... points) {
        return sum(points).affectOnXYZ(x -> x / points.length);
    }

    public static Point3D sub(Point3D start, Point3D end) {
        return new Point3D(end.x - start.x, end.y - start.y, end.z - start.z);
    }

    public static double distance(Point3D p1, Point3D p2) {
        return p1.distanceFrom(p2);
    }

    public static double complexProduct(Point3D dotVector, Point3D crossVector1, Point3D crossVector2) {
        return dotProduct(dotVector, crossProduct(crossVector1, crossVector2));
    }

    public static Point3D affectMatrix(Point3D p, double[][] mat) {
        return new Point3D(
                mat[0][0] * p.x + mat[0][1] * p.y + mat[0][2] * p.z,
                mat[1][0] * p.x + mat[1][1] * p.y + mat[1][2] * p.z,
                mat[2][0] * p.x + mat[2][1] * p.y + mat[2][2] * p.z
        );
    }

    public static Point3D getNormalVecFrom3Point(Point3D p1, Point3D p2, Point3D p3) {
        return sub(p1, p2).crossProduct(sub(p1, p3)).normalize();
    }

    public static Point3D immutable(double x, double y, double z) {
        return new ImmutablePoint3D(x, y, z);
    }

    public static Point3D immutable(Point3D point) {
        return immutable(point.x, point.y, point.z);
    }

    public static Point3D rotateImmutably(Point3D point, Point3D angles, Point3D center) {
        return new Point3D(point).rotate(center, angles.x, angles.y, angles.z);
    }

    public static Point3D rotateImmutably(Point3D point, Point3D angles) {
        return new Point3D(point).rotate(angles.x, angles.y, angles.z);
    }

    public static Point3D of(double x, double y, double z) {
        return immutable(x, y, z);
    }

    public static Point3D of(Point3D point) {
        return immutable(point);
    }

    @Deprecated
    private static double[][] matrixToConvertDirection(Point3D srcVector, Point3D dstVector) {
        var a = new Point3D(srcVector).normalize();
        var b = new Point3D(dstVector).normalize();
        var v = crossProduct(a, b);
        var c = dotProduct(a, b);
        c = 1 / (1 + c);
        var vv = new double[][] {
                {0, -v.z*c, v.y*c},
                {v.z*c, 0, -v.x*c},
                {-v.y*c, v.x*c, 0}
        };
        var v2 = MatUtils.mul(vv, vv);
        return new double[][] {
                {1 + vv[0][0] + v2[0][0], vv[0][1] + v2[0][1], vv[0][2] + v2[0][2]},
                {vv[1][0] + v2[1][0], 1 + vv[1][1] + v2[1][1], vv[1][2] + v2[1][2]},
                {vv[2][0] + v2[2][0], vv[2][1] + v2[2][1], 1 + vv[2][2] + v2[2][2]}
        };
    }

    private static class ImmutablePoint3D extends Point3D {
        private final double x;
        private final double y;
        private final double z;

        private ImmutablePoint3D(double x, double y, double z) {
            super(x, y, z);
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double x() {
            return x;
        }

        public double y() {
            return y;
        }

        public double z() {
            return z;
        }

        @Override
        public boolean testAndSet(int numOfCoordinate, double oldValue, double newValue) {
            return getCopy().testAndSet(numOfCoordinate, oldValue, newValue);
        }

        @Override
        public void setCoordinate(int numOfCoordinate, double newValue) {
            getCopy().setCoordinate(numOfCoordinate, newValue);
        }

        @Override
        public Point3D set(@NotNull Point3D p) {
            return getCopy().set(p);
        }

        @Override
        public Point3D set(double newX, double newY, double newZ) {
            return getCopy().set(newX, newY, newZ);
        }

        @Override
        public Point3D set(Point2D p, double newZ) {
            return getCopy().set(p, newZ);
        }

        @Override
        public Point3D addVector(Point3D vector) {
            return getCopy().addVector(vector);
        }

        @Override
        public Point3D addVector(double x, double y, double z) {
            return getCopy().addVector(x, y, z);
        }

        @Override
        public Point3D affectOnX(Function2D f) {
            return getCopy().affectOnX(f);
        }

        @Override
        public Point3D affectOnY(Function2D f) {
            return getCopy().affectOnY(f);
        }

        @Override
        public Point3D affectOnZ(Function2D f) {
            return getCopy().affectOnZ(f);
        }

        @Override
        public Point3D affectOnXYZ(Function2D f) {
            return getCopy().affectOnXYZ(f);
        }

        @Override
        public Point3D rotate(double xAngle, double yAngle, double zAngle) {
            return getCopy().rotate(xAngle, yAngle, zAngle);
        }

        @Override
        public Point3D rotate(Point3D center, double xAngle, double yAngle, double zAngle) {
            return getCopy().rotate(center, xAngle, yAngle, zAngle);
        }

        @Override
        public Point3D affectMapper(Mapper3D... mappers) {
            return getCopy().affectMapper(mappers);
        }

        @Override
        public Point3D crossProduct(Point3D p) {
            return getCopy().crossProduct(p);
        }

        @Override
        public Point3D normalize() {
            return getCopy().normalize();
        }

        @Override
        public Point3D affectMatrix(double[][] mat) {
            return getCopy().affectMatrix(mat);
        }

        @Override
        public Point3D add(Point3D... points) {
            return getCopy().add(points);
        }

        @Override
        public Point3D sub(Point3D p) {
            return getCopy().sub(p);
        }

        @Override
        public Point3D sub(Point3D... points) {
            return getCopy().sub(points);
        }

        @Override
        public void addElement(int index, Double e) {
            getCopy().addElement(index, e);
        }

        @Override
        public void removeAll() {
            getCopy().removeAll();
        }

        @Override
        public void remove(int... indexes) {
            getCopy().remove(indexes);
        }

        @Override
        public SortableTuple<Double> subTuple(int indexStart, int indexEnd, int step) {
            return getCopy().subTuple(indexStart, indexEnd, step);
        }
    }
}
