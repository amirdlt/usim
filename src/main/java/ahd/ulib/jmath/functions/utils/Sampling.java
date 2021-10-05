package ahd.ulib.jmath.functions.utils;

import ahd.ulib.jmath.datatypes.functions.Arc2D;
import ahd.ulib.jmath.datatypes.functions.Arc3D;
import ahd.ulib.jmath.datatypes.functions.Function2D;
import ahd.ulib.jmath.datatypes.functions.UnaryFunction;
import ahd.ulib.jmath.datatypes.tuples.AbstractPoint;
import ahd.ulib.jmath.datatypes.tuples.Pair;
import ahd.ulib.jmath.datatypes.tuples.Point2D;
import ahd.ulib.jmath.datatypes.tuples.Point3D;
import ahd.ulib.jmath.functions.unaries.real.ConstantFunction2D;
import ahd.ulib.jmath.functions.unaries.real.LinearFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({ "unused", "DuplicatedCode", "SuspiciousNameCombination" })
public final class Sampling {
    public static List<Point2D> sample(Function2D f, double l, double u, double delta) {
        u = Math.max(Math.max(u, l), l = Math.min(u, l));
        double x = l;
        var res = new ArrayList<Point2D>();
        res.add(new Point2D(l, f.valueAt(l)));
        if (l == u)
            return res;
        while ((x += delta) < u)
            res.add(new Point2D(x, f.valueAt(x)));
        res.add(new Point2D(u, f.valueAt(u)));
        return res;
    }

    public static List<Point2D> multiThreadSampling(Function2D f, double l, double u, double delta, int numOfThreads) {
        u = Math.max(Math.max(u, l), l = Math.min(u, l));

        Point2D[] allPoints = new Point2D[(int) ((u - l) / delta) + 1];
        allPoints[0] = new Point2D(l, f.valueAt(l));
        List<Thread> threads = new ArrayList<>(numOfThreads);
        AtomicInteger counter = new AtomicInteger(0);

        final var numOfPoints = allPoints.length;

        for (int i = 0; i < numOfThreads; i++) {
            int start = counter.get() * numOfPoints / numOfThreads;
            int end = counter.incrementAndGet() * numOfPoints / numOfThreads;
            double finalL = l;
            Thread t = new Thread(() -> {
                double dummy;
                for (int j = start + 1; j <= end; j++)
                    try {
                        allPoints[j] = new Point2D(dummy = finalL + j * delta, f.valueAt(dummy));
                    } catch (IndexOutOfBoundsException ignore) {
                    }
            });

            threads.add(t);
            t.start();
        }

        for (var thread : threads)
            try {
                thread.join();
            } catch (Exception e) {
                return new ArrayList<>();
            }
        if (allPoints[allPoints.length - 1].x > u)
            allPoints[allPoints.length - 1] = new Point2D(u, f.valueAt(u));
        return new ArrayList<>(Arrays.asList(allPoints));
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull List<Point2D> multiThreadSampling(@NotNull Arc2D arc, double l, double u, double delta, int numOfThreads) {
        u = Math.max(Math.max(u, l), l = Math.min(u, l));

        Point2D[] allPoints = new Point2D[(int) ((u - l) / delta) + 1];
        allPoints[0] = arc.valueAt(l);
        List<Thread> threads = new ArrayList<>(numOfThreads);
        int counter = 0;

        final var numOfPoints = allPoints.length;

        for (int i = 0; i < numOfThreads; i++) {
            int start = counter++ * numOfPoints / numOfThreads;
            int end = counter * numOfPoints / numOfThreads;
            double finalL = l;
            Thread t = new Thread(() -> {
                for (int j = start + 1; j <= end; j++)
                    try {
                        allPoints[j] = arc.valueAt(finalL + j * delta);
                    } catch (IndexOutOfBoundsException ignore) {
                    }
            });

            threads.add(t);
            t.start();
        }

        for (var thread : threads)
            try {
                thread.join();
            } catch (Exception e) {
                return new ArrayList<>();
            }
        if (l + (allPoints.length - 1) * delta > u)
            allPoints[allPoints.length - 1] = arc.valueAt(u);
        return new ArrayList<>(Arrays.asList(allPoints));
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull List<Point3D> multiThreadSampling(Arc3D arc, double l, double u, double delta, int numOfThreads) {
        u = Math.max(Math.max(u, l), l = Math.min(u, l));

        Point3D[] allPoints = new Point3D[(int) ((u - l) / delta) + 1];
        List<Thread> threads = new ArrayList<>(numOfThreads);
        AtomicInteger counter = new AtomicInteger(0);

        final var numOfPoints = allPoints.length;

        for (int i = 0; i < numOfThreads; i++) {
            int start = counter.get() * numOfPoints / numOfThreads;
            int end = counter.incrementAndGet() * numOfPoints / numOfThreads;
            double finalL = l;
            Thread t = new Thread(() -> {
                for (int j = start + 1; j < end; j++)
                    try {
                        allPoints[j] = arc.valueAt(finalL + j * delta);
                    } catch (IndexOutOfBoundsException ignore) {
                    }
            });

            threads.add(t);
            t.start();
        }

        for (var thread : threads)
            try {
                thread.join();
            } catch (Exception e) {
                return new ArrayList<>();
            }
        if (l + (allPoints.length - 1) * delta > u)
            allPoints[allPoints.length - 1] = arc.valueAt(u);
        return new ArrayList<>(Arrays.asList(allPoints));
    }

    @Deprecated
    public static @NotNull UnaryFunction regularSampleToFunction(List<Point2D> points) {
        if (points == null || points.isEmpty())
            return ConstantFunction2D.zero();
        var points_ = new ArrayList<>(points);
        Point2D.setComparatorMode(Point2D.X_COMPARE);
        Collections.sort(points_);
        double l = points_.get(0).x;
        double u = points_.get(points_.size() - 1).x;
        double delta = points_.get(1).x - points_.get(0).x;
        return new UnaryFunction(x -> {
            double dummy;
            if (x < l || x > u)
                return Double.NaN;
            if (isInteger(dummy = (x - l) / delta)) {
                return points_.get((int) dummy).y;
            } else {
                return points_.get((int) dummy + 1).y / 2 + points_.get((int) dummy).y;
            }
        });
    }

    public static @NotNull UnaryFunction sampleToFunction(List<Point2D> points) {
        if (points == null || points.isEmpty())
            return ConstantFunction2D.NaN();
        var points_ = new ArrayList<>(points);
        Point2D.setComparatorMode(Point2D.X_COMPARE);
        Collections.sort(points_);
        double l = points_.get(0).x;
        double u = points_.get(points_.size() - 1).x;
        return new UnaryFunction(x -> {
            if (x < l || x > u)
                return Double.NaN;
            if (x == u) {
                return points_.get(points_.size() - 1).y;
            } else if (x == l) {
                return points_.get(0).y;
            }
            double xx;
            int index = 0;
            do {
                xx = points_.get(index++).x;
                if (index == points_.size() - 2 || points_.get(index + 1).x > x)
                    break;
            } while (xx < x);

            if (xx == x)
                return points_.get(index).y;

            return LinearFunction.f(points_.get(index), points_.get(index + 1)).valueAt(x);
        });
    }

    public static @NotNull Arc2D sampleToArc(@NotNull List<Point2D> points, double l, double u) {
        final var delta = (u - l) / (points.size() - 1);
        ArrayList<Point2D> xSample = new ArrayList<>();
        ArrayList<Point2D> ySample = new ArrayList<>();

        l -= delta;
        int counter = 0;
        Point2D dummy;
        while ((l += delta) <= u) {
            dummy = points.get(counter++);
            xSample.add(new Point2D(l, dummy.x));
            ySample.add(new Point2D(l, dummy.y));
        }

        var fx = sampleToFunction(xSample);
        var fy = sampleToFunction(ySample);
        return t -> new Point2D(fx.valueAt(t), fy.valueAt(t));
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull Pair<List<Double>, List<Double>> sample_(Function2D f, double lowBound, double upBound, double stepLen) {
        lowBound = Math.min(Math.min(lowBound, upBound), upBound = Math.max(lowBound, upBound));
        double x = lowBound - stepLen;
        List<Double> xa = new ArrayList<>();
        List<Double> ya = new ArrayList<>();
        while ((x += stepLen) <= upBound) {
            xa.add(x);
            ya.add(f.valueAt(x));
        }

        if ((x - stepLen) != upBound) {
            xa.add(upBound);
            ya.add(f.valueAt(upBound));
        }

        return new Pair<>(xa, ya);
    }

    public static @NotNull List<Point2D> sampleOf2DRectangularRegion(double xStart, double xEnd, double yStart, double yEnd, double deltaX,
            double deltaY) {
        List<Point2D> res = new ArrayList<>();
        double y = yStart - deltaY;
        var xSample = sample(xStart, xEnd, deltaX);
        while ((y += deltaY) < yEnd) {
            double finalY = y;
            xSample.forEach(x -> res.add(new Point2D(x, finalY)));
        }
        xSample.forEach(x -> res.add(new Point2D(x, yEnd)));
        return res;
    }

    public static @NotNull List<Point2D> sampleOf2DRectangularRegion(List<Double> xSample, @NotNull List<Double> ySample) {
        List<Point2D> res = new ArrayList<>();
        ySample.forEach(y -> xSample.forEach(x -> res.add(new Point2D(x, y))));
        return res;
    }

    public static @NotNull List<Double> sample(double l, double u, double delta) {
        List<Double> res = new ArrayList<>();
        double x = l - delta;
        while ((x += delta) < u)
            res.add(x);
        res.add(u);
        return res;
    }

    public static double lengthOfOrderedSample(@NotNull List<AbstractPoint> points) {
        double res = 0;
        for (int i = 0; i < points.size() - 1; i++)
            res += points.get(i).distanceFrom(points.get(i + 1));
        return res;
    }

    private static boolean isInteger(double num) {
        if (!Double.isFinite(num))
            return false;
        return (int) num == num;
    }
}
