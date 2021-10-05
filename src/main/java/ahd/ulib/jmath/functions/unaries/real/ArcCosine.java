package ahd.ulib.jmath.functions.unaries.real;

import ahd.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class ArcCosine extends UnaryFunction {
    private ArcCosine() {
        super(Math::acos);
    }

    public static UnaryFunction f() {
        return new ArcCosine();
    }
}
