package ahd.ulib.jmath.functions.unaries.real;

import ahd.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class Secant extends UnaryFunction {
    private Secant() {
        super(x -> 1 / Math.cos(x));
    }

    public static UnaryFunction f() {
        return new Secant();
    }
}
