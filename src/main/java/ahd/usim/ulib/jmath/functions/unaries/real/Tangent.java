package ahd.usim.ulib.jmath.functions.unaries.real;

import ahd.usim.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class Tangent extends UnaryFunction {
    private Tangent() {
        super(Math::tan);
    }

    public static UnaryFunction f() {
        return new Tangent();
    }
}
