package ahd.usim.ulib.jmath.datatypes.functions;

import ahd.usim.ulib.jmath.datatypes.tuples.Point2D;
import ahd.usim.ulib.jmath.datatypes.tuples.Point3D;

import static java.lang.Math.*;

public interface Surface extends Function<Point3D, Point2D> {
    Surface NaN = (x, y) -> new Point3D(Double.NaN, Double.NaN, Double.NaN);

    Point3D valueAt(double x, double y);

    default BinaryFunction fx() {
        return new BinaryFunction((x, y) -> valueAt(x, y).x);
    }

    default BinaryFunction fy() {
        return new BinaryFunction((x, y) -> valueAt(x, y).y);
    }

    default BinaryFunction fz() {
        return new BinaryFunction((x, y) -> valueAt(x, y).z);
    }

    default Surface partialDerivativeRelativeToX(double deltaX) {
        var dx = fx().partialDerivativeRelativeToX(deltaX);
        var dy = fy().partialDerivativeRelativeToX(deltaX);
        var dz = fz().partialDerivativeRelativeToX(deltaX);
        return (x, y) -> new Point3D(dx.valueAt(x, y), dy.valueAt(x, y), dz.valueAt(x, y));
    }

    default Surface partialDerivativeRelativeToY(double deltaY) {
        var dx = fx().partialDerivativeRelativeToY(deltaY);
        var dy = fy().partialDerivativeRelativeToY(deltaY);
        var dz = fz().partialDerivativeRelativeToY(deltaY);
        return (x, y) -> new Point3D(dx.valueAt(x, y), dy.valueAt(x, y), dz.valueAt(x, y));
    }

    default Surface unitNormal(double deltaX, double deltaY) {
        var dx = partialDerivativeRelativeToX(deltaX);
        var dy = partialDerivativeRelativeToY(deltaY);
        return (x, y) -> dx.valueAt(x, y).crossProduct(dy.valueAt(x, y)).normalize();
    }

    @Override
    default Point3D valueAt(Point2D p) {
        return valueAt(p.x, p.y);
    }

    @Override
    default Point3D atOrigin() {
        return valueAt(new Point2D());
    }

    static Surface surfaceOfRevolution(Arc2D arc) {
        return (x, y) -> new Point3D(arc.fx().valueAt(x) * Math.cos(y), arc.fx().valueAt(x) * Math.sin(y), arc.fy().valueAt(x));
    }

    static Surface circulation(Arc3D arc, Function2D radius) {
        return (x, y) ->
            Arc3D.circle(arc.valueAt(x), radius.valueAt(x), arc.derivative(0.01, 1).valueAt(x)).valueAt(y);
    }

    static Surface circulation(Arc3D arc, double radius) {
        return (x, y) ->
                Arc3D.circle(arc.valueAt(x), radius, arc.derivative(0.01, 1).valueAt(x)).valueAt(y);
    }

    static Surface curveWrapping(Arc3D arc, Arc2D curve) {
        return (x, y) ->
                Arc3D.rotatedArc2D(curve, arc.valueAt(x), arc.derivative(0.01, 1).valueAt(x)).valueAt(y);
    }

    // x: 0, 2pi y: -1, 1
    static Surface mobius() {
        return (u, v) -> new Point3D((1 + v * cos(u/2) / 2) * cos(u), (1 + v * cos(u/2) / 2) * sin(u), v * sin(u/2) / 2);
    }

    // x: 0, pi y: 0, 2pi
    static Surface kleinBottle() {
        return (u, v) ->
                new Point3D(
                        -2*cos(u)*(3*cos(v) - 30*sin(u) + 90*pow(cos(u), 4)*sin(u) - 60*pow(cos(u), 6)*sin(u) + 5*cos(u)*cos(v)*sin(u))/15,
                        -sin(u)*(3*cos(v) - 3*cos(u)*cos(u)*cos(v) - 48*pow(cos(u), 4)*cos(v) + 48*pow(cos(u), 6)*cos(v) - 60*sin(u) +
                                5*cos(u)*cos(v)*sin(u) - 5*pow(cos(u), 3)*cos(v)*sin(u) - 80*pow(cos(u), 5)*cos(v)*sin(u) + 80*pow(cos(u), 7)*cos(v)*sin(u))/15,
                        2*sin(v)*(3+5*cos(u)*sin(u))/15
                );
    }
}
