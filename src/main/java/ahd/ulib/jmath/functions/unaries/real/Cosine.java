package ahd.ulib.jmath.functions.unaries.real;

import ahd.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class Cosine extends UnaryFunction {
    private Cosine() {
        super(Math::cos);
    }

    public static UnaryFunction f() {
        return new Cosine();
    }
}
