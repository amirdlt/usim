package ahd.ulib.jmath.operators;

import ahd.ulib.jmath.datatypes.Operator;
import ahd.ulib.jmath.datatypes.functions.*;
import ahd.ulib.jmath.datatypes.tuples.Point2D;
import ahd.ulib.jmath.functions.unaries.real.ConstantFunction2D;
import ahd.ulib.jmath.functions.utils.Sampling;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public final class Integral implements Operator<UnaryFunction> {

    // 2D function integration
    @Deprecated
    public static double byDefinition(Function2D f, double lowBound, double upBound, int numOfPoints) {
        if (lowBound == upBound)
            return 0;
        double delta = (upBound - lowBound) / numOfPoints;
        if (lowBound == Double.NEGATIVE_INFINITY && upBound != Double.POSITIVE_INFINITY) {
            return infiniteL(f, upBound, delta);
        } else if (lowBound != Double.NEGATIVE_INFINITY && upBound == Double.POSITIVE_INFINITY) {
            return infiniteU(f, lowBound, delta);
        } else if (lowBound == Double.NEGATIVE_INFINITY && upBound == Double.POSITIVE_INFINITY) {
            return infiniteLU(f, delta);
        }
        double res = 0;
        for (int i = 0; i < Math.abs(numOfPoints) - 1; i++)
            res += f.valueAt(lowBound += delta) * delta;
        return res;
    }

    public static double byDefinition(Function2D f, double lowBound, double upBound, double delta) {
        if (lowBound == upBound)
            return 0;
        double res = 0;
        if (lowBound == Double.NEGATIVE_INFINITY && upBound != Double.POSITIVE_INFINITY) {
            return infiniteL(f, upBound, delta);
        } else if (lowBound != Double.NEGATIVE_INFINITY && upBound == Double.POSITIVE_INFINITY) {
            return infiniteU(f, lowBound, delta);
        } else if (lowBound == Double.NEGATIVE_INFINITY && upBound == Double.POSITIVE_INFINITY) {
            return infiniteLU(f, delta);
        }
        double temp;
        var oldL = lowBound;
        var oldU = upBound;
        upBound = Math.max(Math.max(lowBound, upBound), lowBound = Math.min(lowBound, upBound));
        lowBound -= delta;
        while ((lowBound += delta) <= upBound)
            if (Double.isFinite(temp = f.valueAt(lowBound) * delta))
                res += temp;
        return oldU < oldL ? -res : res;
    }

    public static double byDefinition(List<Point2D> sortedSample) {
        double res = 0;
        double temp;
        double delta = sortedSample.get(1).x - sortedSample.get(0).x;

        for (var p : sortedSample)
            if (Double.isFinite(temp = p.y * delta))
                res += temp;
        return res;
    }

    @Deprecated // needs to check
    public static double byMonteCarlo(Function2D f, double l, double u, double delta) {
        long numOfPoints = (long) Math.abs((u - l) / delta);
        double y, xp, yp;
        double yMin = f.f().lowestPoint(l, u, delta).y;
        double yMax = f.f().highestPoint(l, u, delta).y;
        long counter = 0;
        for (long i = 0; i < numOfPoints; i++) {
            xp = Math.random() * (u - l) + l;
            yp = Math.random() * (yMax - yMin) + yMin;
            y = f.valueAt(xp);
            counter += yp < y && y > 0 && yp > 0 ? 1 : (yp > y && y < 0 && yp < 0 ? -1 : 0);
        }
        return counter * (u - l) * (yMax - yMin) / numOfPoints;
    }

    public static double byRungeKutta(Function2D f, double l, double u, double h) {

        return 0;
    }

    public static double infiniteLU(Function2D f, double delta) {
        return byDefinition(x -> f.valueAt(x / (1 - x*x)) * (1 + x*x) / ((1 - x*x) * (1 - x*x)), -1, 1, delta);
    }

    public static double infiniteU(Function2D f, double l, double delta) {
        return byDefinition(x -> f.valueAt(l + x / (1 - x)) / ((1 - x) * (1 - x)), 0, 1, delta);
    }

    public static double infiniteL(Function2D f, double u, double delta) {
        return byDefinition(x -> f.valueAt(u + 1 - 1 / x) / (x * x), 0, 1, delta);
    }

    @Deprecated
    public static double byCompositeRule(Function2D f, double l, double u, double delta) {
        if (l == u)
            return 0;
        int n = (int) Math.abs((u - l) / delta);
        double res = f.valueAt(l) / 2 + f.valueAt(u) / 2;
        for (int i = 1; i < n; i++)
            res += f.valueAt(l += delta);
        return res * delta;
    }

    @Deprecated // 3D function integration //TODO: inf is not implemented by x projection only
    public static double byDefinitionOverRectangularRegion(Function3D f, double xL, double xU, double yL, double yU, double deltaX, double deltaY) {
        var region = Sampling.sampleOf2DRectangularRegion(xL, xU, yL, yU, deltaX, deltaY);
        double dummy;
        double res = 0;
        for (var p : region)
            if (Double.isFinite(dummy = f.valueAt(p.x + deltaX / 2, p.y + deltaY / 2) * deltaX * deltaY))
                res += dummy;
        return res;
    }

    public static double byDefinitionXY(Function3D f, double yL, double yU, Function2D xL, Function2D xU, double deltaX, double deltaY) {
        return new UnaryFunction(y -> f.f().makeYFix(y).integral(xL, xU, deltaX).valueAt(y)).integral(yL, yU, deltaY);
    }

    public static double byDefinitionYX(Function3D f, double xL, double xU, Function2D yL, Function2D yU, double deltaX, double deltaY) {
        return new UnaryFunction(x -> f.f().makeXFix(x).integral(yL, yU, deltaY).valueAt(x)).integral(xL, xU, deltaX);
    }

    public static double byDefinitionXYZ(Function4D f, double zL, double zU, Function2D yL, Function2D yU,
                                         Function3D xL, Function3D xU, double deltaX, double deltaY, double deltaZ) {
        return new UnaryFunction(z -> byDefinitionXY(f.f3D(z), yL.valueAt(z),
                yU.valueAt(z), xL.fx(z), xU.fx(z), deltaX, deltaY)).integral(zL, zU, deltaZ);
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    @Deprecated
    public static @NotNull UnaryFunction byCompositeRule(Function2D f, Function2D l, Function2D u, double delta) {
        return new UnaryFunction(x -> byCompositeRule(f, l.valueAt(x), u.valueAt(x), delta));
    }

    public static @NotNull UnaryFunction byDefinition(Function2D f, @NotNull Function2D lowBound, Function2D upBound, double delta) {
        if (lowBound.f().isConstant(Double.NEGATIVE_INFINITY) && !upBound.f().isConstant(Double.POSITIVE_INFINITY)) {
            if (upBound.f().isConstant())
                return ConstantFunction2D.f(infiniteL(f, upBound.valueAt(0), delta));
            return new UnaryFunction(x -> infiniteL(f, upBound.valueAt(x), delta));
        } else if (!lowBound.f().isConstant(Double.NEGATIVE_INFINITY) && upBound.f().isConstant(Double.POSITIVE_INFINITY)) {
            if (lowBound.f().isConstant())
                return ConstantFunction2D.f(infiniteU(f, lowBound.valueAt(0), delta));
            return new UnaryFunction(x -> infiniteU(f, lowBound.valueAt(x), delta));
        } else if (lowBound.f().isConstant(Double.NEGATIVE_INFINITY) && upBound.f().isConstant(Double.POSITIVE_INFINITY)) {
            return ConstantFunction2D.f(infiniteLU(f, delta));
        }
        return new UnaryFunction(x -> byDefinition(f, lowBound.valueAt(x), upBound.valueAt(x), delta));
    }

    @Deprecated
    public static UnaryFunction byMonteCarlo(Function2D f, Function2D lowBound, Function2D upBound, double delta) {
        if (lowBound.f().isConstant(Double.NEGATIVE_INFINITY) && !upBound.f().isConstant(Double.POSITIVE_INFINITY)) {
            if (upBound.f().isConstant())
                return ConstantFunction2D.f(infiniteL(f, upBound.valueAt(0), delta));
            return new UnaryFunction(x -> infiniteL(f, upBound.valueAt(x), delta));
        } else if (!lowBound.f().isConstant(Double.NEGATIVE_INFINITY) && upBound.f().isConstant(Double.POSITIVE_INFINITY)) {
            if (lowBound.f().isConstant())
                return ConstantFunction2D.f(infiniteU(f, lowBound.valueAt(0), delta));
            return new UnaryFunction(x -> infiniteU(f, lowBound.valueAt(x), delta));
        } else if (lowBound.f().isConstant(Double.NEGATIVE_INFINITY) && upBound.f().isConstant(Double.POSITIVE_INFINITY)) {
            return ConstantFunction2D.f(infiniteLU(f, delta));
        }
        return new UnaryFunction(x -> byMonteCarlo(f, lowBound.valueAt(x), upBound.valueAt(x), delta));
    }

    public static BinaryFunction byDefinitionXY(Function3D f, Function3D yL, Function3D yU, Function2D xL, Function2D xU, double deltaX, double deltaY) {
        return new BinaryFunction((x, y) -> byDefinitionXY(f, yL.valueAt(x, y), yU.valueAt(x, y), xL, xU, deltaX, deltaY));
    }

    public static BinaryFunction byDefinitionYX(Function3D f, Function3D xL, Function3D xU, Function2D yL, Function2D yU, double deltaX, double deltaY) {
        return new BinaryFunction((x, y) -> byDefinitionYX(f, xL.valueAt(x, y), xU.valueAt(x, y), yL, yU, deltaX, deltaY));
    }

    public static Function4D byDefinitionXYZ(Function4D f, Function4D zL, Function4D zU, Function2D yL, Function2D yU, Function3D xL, Function3D xU, double deltaX, double deltaY, double deltaZ) {
        return (x, y, z) -> byDefinitionXYZ(f, zL.valueAt(x, y, z), zU.valueAt(x, y, z), yL, yU, xL, xU, deltaX, deltaY, deltaZ);
    }

    @Override
    public UnaryFunction operate(Function2D f) {return null;}

    public static void main(String[] args) {
        System.out.println(byDefinitionXYZ((x, y, z) -> x, -1, 1,
                z -> -1, z -> z, (y, z) -> 0,
                (y, z) -> y*z, 0.01, 0.01, 0.01));
//        System.out.println(byDefinition(Math::cos, x -> 0, x -> x, 0.01).valueAt(0));
    }
}
