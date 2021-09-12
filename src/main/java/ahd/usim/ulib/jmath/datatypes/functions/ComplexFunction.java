package ahd.usim.ulib.jmath.datatypes.functions;

import ahd.usim.ulib.jmath.datatypes.ComplexNumber;

@SuppressWarnings("unused")
public class ComplexFunction implements CFunction {
    private final CFunction kernel;

    public ComplexFunction(CFunction kernel) {
        this.kernel = kernel;
    }

    public ComplexFunction(BinaryFunction realOrAbsFunction, BinaryFunction imaginaryOrPhaseFunction, boolean isPolar) {
        this(z -> new ComplexNumber(realOrAbsFunction.valueAt(
                isPolar ? z.absoluteValue : z.realValue, isPolar ? z.phase : z.imaginaryValue),
                imaginaryOrPhaseFunction.valueAt(
                        isPolar ? z.absoluteValue : z.realValue, isPolar ? z.phase : z.imaginaryValue), isPolar));
    }

    public ComplexFunction(BinaryFunction realFunction, BinaryFunction imaginaryFunction) {
        this(z -> new ComplexNumber(realFunction.valueAt(z.realValue, z.imaginaryValue),
                imaginaryFunction.valueAt(z.realValue, z.imaginaryValue), false));
    }

    @Override
    public ComplexNumber valueAt(ComplexNumber c) {
        return kernel.valueAt(c);
    }
}
