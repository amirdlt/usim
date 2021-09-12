package ahd.usim.ulib.jmath.functions.unaries.real;

import ahd.usim.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class ArcTangent extends UnaryFunction {
    private ArcTangent() {
        super(Math::atan);
    }

    public static UnaryFunction f() {
        return new ArcTangent();
    }
}
