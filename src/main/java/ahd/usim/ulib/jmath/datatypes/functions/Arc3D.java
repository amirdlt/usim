package ahd.usim.ulib.jmath.datatypes.functions;

import ahd.usim.ulib.jmath.datatypes.tuples.Point2D;
import ahd.usim.ulib.jmath.datatypes.tuples.Point3D;
import ahd.usim.ulib.jmath.functions.utils.Sampling;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Arc3D extends Function<Point3D, Double> {
    Arc3D NaN = t -> new Point3D(Double.NaN, Double.NaN, Double.NaN);

    Point3D valueAt(double t);

    default List<Point3D> sample(double l, double u, double delta, int numOfThreads) {
        return Sampling.multiThreadSampling(this, l, u, delta, numOfThreads);
    }

    default UnaryFunction fx() {
        return new UnaryFunction(x -> valueAt(x).x);
    }

    default UnaryFunction fy() {
        return new UnaryFunction(x -> valueAt(x).y);
    }

    default UnaryFunction fz() {
        return new UnaryFunction(x -> valueAt(x).z);
    }

    default UnaryFunction zAsFunctionOfY(double l, double u, double delta) {
        return new UnaryFunction(x -> valueAt(fy().inverse(l, u, delta).valueAt(x)).z);
    }

    default Arc3D derivative(double delta, int order) {
        return t -> new Point3D(
                fx().derivative(delta, order).valueAt(t),
                fy().derivative(delta, order).valueAt(t),
                fz().derivative(delta, order).valueAt(t)
        );
    }

    default Arc3D rotate(Point3D center, double xAngle, double yAngle, double zAngle) {
        return t -> valueAt(t).rotate(center, xAngle, yAngle, zAngle);
    }

    default Arc3D rotate(double xAngle, double yAngle, double zAngle) {
        return rotate(new Point3D(), xAngle, yAngle, zAngle);
    }

    default Arc2D asArc2D() {
        return t -> new Point2D(fx().valueAt(t), fy().valueAt(t));
    }

    @Override
    default Point3D valueAt(@NotNull Double t) {
        return valueAt(t.doubleValue());
    }

    @Override
    default Point3D atOrigin() {
        return valueAt(0);
    }

//    @Override
//    default void renderWithParams(Graphics2D g2d, Object... params) {
//        CoordinatedCanvasPanel canvas = (CoordinatedCanvasPanel) params[0];
//        Color color = (Color) params[1];
//        double l = (double) params[2];
//        double u = (double) params[3];
//        double delta = (double) params[4];
//        int numOfThreads = (int) params[5];
//        asArc2D().renderWithParams(g2d, canvas, color, l, u, delta, numOfThreads);
//    }

    static @NotNull Arc3D toArc3D(@NotNull Arc2D arc, Point3D offset) {
        var x = arc.fx();
        var y = arc.fy();
        return t -> new Point3D(
                x.valueAt(t) + offset.x,
                y.valueAt(t) + offset.y,
                offset.z
        );
    }

    static @NotNull Arc3D circle(Point3D center, double radius) {
//        var xc = center.x;
//        var yc = center.y;
//        var zc = center.z;
//        return t -> new Point3D(radius * Math.sin(t) + xc, radius * Math.cos(t) + yc, zc);
        return toArc3D(Arc2D.circle(new Point2D(), radius), center);
    }

    static @NotNull Arc3D circle(Point3D center, double radius, Point3D normal) {
//        var tmp = (normal.x + normal.y) / normal.z;
//        var a = new Point3D(-normal.y * tmp - normal.z, normal.z + normal.x * tmp, normal.x - normal.y).normalize();
//        var b = new Point3D(1, 1, -tmp).normalize();
        return rotatedArc2D(Arc2D.circle(new Point2D(), radius), center, normal);
//        return t -> new Point3D(
//                center.x + radius * Math.cos(t) * a.x + radius * Math.sin(t) * b.x,
//                center.y + radius * Math.cos(t) * a.y + radius * Math.sin(t) * b.y,
//                center.z + radius * Math.cos(t) * a.z + radius * Math.sin(t) * b.z
//        );
    }
    
    static @NotNull Arc3D rotatedArc2D(@NotNull Arc2D arc, Point3D offset, @NotNull Point3D normal) {
        var tmp = (normal.x + normal.y) / normal.z;
        var a = new Point3D(-normal.y * tmp - normal.z, normal.z + normal.x * tmp, normal.x - normal.y).normalize();
        var b = new Point3D(1, 1, -tmp).normalize();
        var x = arc.fx();
        var y = arc.fy();
        return t -> new Point3D(
                offset.x + x.valueAt(t) * a.x + y.valueAt(t) * b.x,
                offset.y + x.valueAt(t) * a.y + y.valueAt(t) * b.y,
                offset.z + x.valueAt(t) * a.z + y.valueAt(t) * b.z
        );
    }
    
    static @NotNull Arc3D rotatedArc2D(Arc2D arc, Point3D normal) {
        return rotatedArc2D(arc, new Point3D(), normal);
    }
}
