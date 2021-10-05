package ahd.ulib.jmath.functions.unaries.real;

import ahd.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class Tangent extends UnaryFunction {
    private Tangent() {
        super(Math::tan);
    }

    public static UnaryFunction f() {
        return new Tangent();
    }
}
