package ahd.ulib.jmath.datatypes.functions;

import ahd.ulib.jmath.datatypes.tuples.Point3D;

@SuppressWarnings("unused")
public interface Mapper3D extends Function<Point3D, Point3D> {
    Mapper3D NaN = p -> new Point3D(Double.NaN, Double.NaN, Double.NaN);

    default Point3D map(double x, double y, double z) {return valueAt(new Point3D(x, y, x));}

    default Point3D map(Point3D p) {return valueAt(p);}

    default Point3D valueAt(double x, double y, double z) {
        return map(x, y, z);
    }

    default Function4D fx() {
        return (x, y, z) -> valueAt(new Point3D(x, y, z)).x;
    }

    default Function4D fy() {
        return (x, y, z) -> valueAt(new Point3D(x, y, z)).y;
    }

    default Function4D fz() {
        return (x, y, z) -> valueAt(new Point3D(x, y, z)).z;
    }

    @Override
    default Point3D atOrigin() {
        return map(0, 0, 0);
    }
}
