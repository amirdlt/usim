package ahd.ulib.jmath.functions.unaries.real;

import ahd.ulib.jmath.datatypes.functions.Function2D;
import ahd.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class IdentityFunction extends UnaryFunction {
    private IdentityFunction() {
        super(x -> x);
    }

    public static boolean isIdentityFunction(Function2D function) {
        return f().equals(function);
    }

    public static UnaryFunction f() {
        return new IdentityFunction();
    }
}
