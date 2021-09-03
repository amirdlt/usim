package com.usim.ulib.jmath.datatypes.functions;

import com.usim.ulib.jmath.datatypes.tuples.Point3D;

@SuppressWarnings("unused")
public interface Function4D extends Function<Double, Point3D> {
    Function4D NaN = (x, y, z) -> Double.NaN;

    double valueAt(double x, double y, double z);

    @Override
    default Double valueAt(Point3D point) {
        return valueAt(point.x, point.y, point.z);
    }

    @Override
    default Double atOrigin() {
        return valueAt(0, 0, 0);
    }

    default UnaryFunction f2D(double y, double z) {
        return new UnaryFunction(x -> valueAt(x, y, z));
    }

    default UnaryFunction fx(double y, double z) {
        return f2D(y, z);
    }

    default UnaryFunction fy(double x, double z) {
        return new UnaryFunction(y -> valueAt(x, y, z));
    }

    default UnaryFunction fz(double x, double y) {
        return new UnaryFunction(z -> valueAt(x, y, z));
    }

    default BinaryFunction f3D(double z) {
        return new BinaryFunction((x, y) -> valueAt(x, y, z));
    }

    default BinaryFunction fxy(double z) {
        return f3D(z);
    }

    default BinaryFunction fxz(double y) {
        return new BinaryFunction((x, z) -> valueAt(x, y, z));
    }

    default BinaryFunction fyz(double x) {
        return new BinaryFunction((y, z) -> valueAt(x, y, z));
    }

    default TernaryFunction f() {
        return new TernaryFunction(this);
    }
}
