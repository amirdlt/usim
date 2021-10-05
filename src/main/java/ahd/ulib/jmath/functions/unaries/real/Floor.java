package ahd.ulib.jmath.functions.unaries.real;

import ahd.ulib.jmath.datatypes.functions.UnaryFunction;

public class Floor extends UnaryFunction {
    private Floor() {
        super(Math::floor);
    }

    public static UnaryFunction f() {
        return new Floor();
    }
}
