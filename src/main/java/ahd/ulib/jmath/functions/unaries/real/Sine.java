package ahd.ulib.jmath.functions.unaries.real;

import ahd.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class Sine extends UnaryFunction {

    private Sine() {
        super(Math::sin);
    }

    public static UnaryFunction f() {
        return new Sine();
    }
}
