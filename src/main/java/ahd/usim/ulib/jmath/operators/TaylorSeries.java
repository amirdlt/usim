package ahd.usim.ulib.jmath.operators;

import ahd.usim.ulib.jmath.datatypes.functions.Function2D;
import ahd.usim.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class TaylorSeries {
    public static UnaryFunction taylorSeries(int order, Function2D f, double x0, double delta) {
        final var der = new double[order + 1];
        var ff = f.f();
        for (int i = 0; i <= order; i++)
            der[i] = ff.derivative(delta, i).valueAt(x0);
        return new UnaryFunction(x -> {
            double fact = 1;
            double res = der[0];
            for (int i = 1; i <= order; i++)
                res += der[i] * (fact *= (x - x0) / -i);
            return -res;
        });
    }

    public static UnaryFunction taylorSeries(int order, Function2D f, double delta) {
        return taylorSeries(order, f, 0, delta);
    }
}
