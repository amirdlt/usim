package ahd.ulib.jmath.functions.utils;

import ahd.ulib.jmath.datatypes.functions.Function2D;
import ahd.ulib.jmath.datatypes.functions.UnaryFunction;
import ahd.ulib.jmath.datatypes.tuples.Point2D;

import java.util.ArrayList;
import java.util.List;

public class RootsFinder {
    public static final int MAX_POINT = 1000000;

    public static double byNewtonMethod(Function2D f, double guess, double tolerance, double delta) {
        var df = new UnaryFunction(f).derivative(delta);

        return 0;
    }

    public static List<Double> bySampling(Function2D f, double l, double u, double delta) {
        u = Math.max(Math.max(u, l), l = Math.min(u, l));
        delta = Math.max((u - l) / MAX_POINT, delta);
        List<Double> roots = new ArrayList<>();
        l -= delta;
        while ((l += delta) < u)
            if (f.valueAt(l) == 0) {
                roots.add(l);
            } else if (l + delta < u) {
                if (f.valueAt(l) * f.valueAt(l + delta) < 0)
                    roots.add(l + delta / 2);
            }
        if (f.valueAt(u) == 0)
            roots.add(u);
        return roots;
    }

    public static List<Double> bySampling(List<Point2D> points) {
        List<Double> roots = new ArrayList<>();
        double delta;
        try {
            delta = points.get(1).x - points.get(0).x;
        } catch (IndexOutOfBoundsException e) {
            return roots;
        }
        for (int i = 0; i < points.size(); i++) {
            var p = points.get(i);
            if (p.y == 0) {
                roots.add(p.x);
            } else if (i + 1 < points.size()) {
                if (p.y * points.get(i + 1).y < 0)
                    roots.add(p.x + delta / 2);
            }
        }
        return roots;
    }
}
