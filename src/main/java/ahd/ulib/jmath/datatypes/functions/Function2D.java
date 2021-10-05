package ahd.ulib.jmath.datatypes.functions;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Function2D extends Function<Double, Double> {
    Function2D NaN = x -> Double.NaN;

    double valueAt(double x);

    default UnaryFunction f(Function2D... inners) {return new UnaryFunction(this).setInnerFunction(inners);}

    @Override
    default Double atOrigin() {
        return valueAt(0);
    }

    @Override
    default Double valueAt(@NotNull Double x) {
        return valueAt(x.doubleValue());
    }
}
