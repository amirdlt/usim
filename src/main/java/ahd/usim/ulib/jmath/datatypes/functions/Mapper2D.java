package ahd.usim.ulib.jmath.datatypes.functions;

import ahd.usim.ulib.jmath.datatypes.tuples.Point2D;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Mapper2D extends Function<Point2D, Point2D> {
    Mapper2D NaN = (x, y) -> new Point2D(Double.NaN, Double.NaN);

    Point2D map(double x, double y);

    @Override
    default Point2D valueAt(@NotNull Point2D p) {
        return map(p.x, p.y);
    }

    @Override
    default Point2D atOrigin() {
        return map(0, 0);
    }
}
