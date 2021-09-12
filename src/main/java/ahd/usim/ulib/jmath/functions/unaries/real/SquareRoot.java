package ahd.usim.ulib.jmath.functions.unaries.real;

import ahd.usim.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class SquareRoot extends UnaryFunction {
    private SquareRoot() {
        super(Math::sqrt);
    }

    public static UnaryFunction f() {
        return new SquareRoot();
    }
}
