package com.usim.ulib.jmath.datatypes.functions;

import com.usim.ulib.jmath.datatypes.tuples.Point2D;

@FunctionalInterface
public interface Mapper2D extends Function<Point2D, Point2D> {
    Mapper2D NaN = (x, y) -> new Point2D(Double.NaN, Double.NaN);

    Point2D map(double x, double y);

    @Override
    default Point2D valueAt(Point2D p) {
        return map(p.x, p.y);
    }

    @Override
    default Point2D atOrigin() {
        return map(0, 0);
    }
}
