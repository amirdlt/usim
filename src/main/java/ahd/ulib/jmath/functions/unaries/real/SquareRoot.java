package ahd.ulib.jmath.functions.unaries.real;

import ahd.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class SquareRoot extends UnaryFunction {
    private SquareRoot() {
        super(Math::sqrt);
    }

    public static UnaryFunction f() {
        return new SquareRoot();
    }
}
