package com.usim.ulib.jmath.functions.utils;

import com.usim.ulib.jmath.datatypes.functions.Function2D;
import com.usim.ulib.jmath.datatypes.functions.UnaryFunction;
import com.usim.ulib.jmath.datatypes.tuples.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class FunctionAnalyser {
    public static List<Point2D> stationaryPoints(Function2D f, double l, double u, double delta) {
        u = Math.max(Math.max(u, l), l = Math.min(u, l));
        List<Point2D> res = new ArrayList<>();
        final var derivative = new UnaryFunction(f).derivative(delta);
        var dRoots = RootsFinder.bySampling(derivative, l, u, delta);
        for (var x : dRoots)
            res.add(new Point2D(x, f.valueAt(x)));
        return res;
    }

    public static List<Point2D> intersectionPoints(Function2D f1, Function2D f2, double l, double u, double delta) {
        u = Math.max(Math.max(u, l), l = Math.min(u, l));
        Function2D nf = x -> f1.valueAt(x) - f2.valueAt(x);
        List<Point2D> res = new ArrayList<>();
        for (var r : RootsFinder.bySampling(nf, l, u, delta))
            res.add(new Point2D(r, f1.valueAt(r)));
        return res;
    }

    public static Point2D highestPoint(Function2D f, double l, double u, double delta) {
        u = Math.max(Math.max(u, l), l = Math.min(u, l));
        double x = l, dummy;
        var res = new Point2D(l, f.valueAt(l));
        while ((x += delta) < u)
            if (res.y < (dummy = f.valueAt(x)))
                res.set(x, dummy);
        if (res.y < (dummy = f.valueAt(u)))
            res.set(u, dummy);
        return res;
    }

    public static Point2D lowestPoint(Function2D f, double l, double u, double delta) {
        u = Math.max(Math.max(u, l), l = Math.min(u, l));
        double x = l, dummy;
        var res = new Point2D(l, f.valueAt(l));
        while ((x += delta) < u)
            if (res.y > (dummy = f.valueAt(x)))
                res.set(x, dummy);
        if (res.y > (dummy = f.valueAt(u)))
            res.set(u, dummy);
        return res;
    }

    public static double minValue(Function2D f, double l, double u, double delta) {
        return lowestPoint(f, l, u, delta).y;
    }

    public static double maxValue(Function2D f, double l, double u, double delta) {
        return highestPoint(f, l, u, delta).y;
    }

    public static double maxX(List<Point2D> sample) {
        Point2D.setComparatorMode(Point2D.X_COMPARE);
        return Collections.max(sample).x;
    }

    public static double minX(List<Point2D> sample) {
        Point2D.setComparatorMode(Point2D.X_COMPARE);
        return Collections.min(sample).x;
    }

    public static Point2D highestPoint(List<Point2D> sample) {
        Point2D.setComparatorMode(Point2D.Y_COMPARE);
        return Collections.max(sample);
    }

    public static Point2D lowestPoint(List<Point2D> sample) {
        Point2D.setComparatorMode(Point2D.Y_COMPARE);
        return Collections.min(sample);
    }
}
