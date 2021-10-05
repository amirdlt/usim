package ahd.ulib.jmath.functions.unaries.real;

import ahd.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class CoSecant extends UnaryFunction {
    private CoSecant() {
        super(x -> 1 / Math.sin(x));
    }

    public static UnaryFunction f() {
        return new CoSecant();
    }
}
