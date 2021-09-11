package com.usim.ulib.jmath.datatypes.functions;

import com.usim.ulib.jmath.datatypes.tuples.Point2D;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Function3D extends Function<Double, Point2D> {
    Function3D NaN = (x, y) -> Double.NaN;

    double valueAt(double x, double y);

    default BinaryFunction f() {return new BinaryFunction(this);}

    @Override
    default Double valueAt(@NotNull Point2D point) {return valueAt(point.x, point.y);}

    @Override
    default Double atOrigin() {
        return valueAt(0, 0);
    }

    default UnaryFunction f2D(double y) {
        return new UnaryFunction(x -> valueAt(x, y));
    }

    default UnaryFunction fx(double y) {
        return f2D(y);
    }

    default UnaryFunction fy(double x) {
        return new UnaryFunction(y -> valueAt(x, y));
    }
}
