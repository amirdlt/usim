package ahd.usim.ulib.jmath.functions.unaries.real;

import ahd.usim.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class StirlingApproximation extends UnaryFunction {
    private StirlingApproximation() {
        super(x -> {
            if (x == 0)
                return 1;
            return Math.sqrt(2 * Math.PI * x) * Math.pow(x / Math.E, x);
        });
    }

    public static UnaryFunction f() {
        return new StirlingApproximation();
    }
}
