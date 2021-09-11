package com.usim.ulib.jmath.datatypes.functions;

import com.usim.ulib.jmath.functions.unaries.real.IdentityFunction;
import com.usim.ulib.jmath.operators.Derivative;

@SuppressWarnings("unused")
public class BinaryFunction implements Function3D {
    private final Function3D kernel;
    private UnaryFunction xF;
    private UnaryFunction yF;
    private UnaryFunction xyF;

    public BinaryFunction(Function3D kernel) {
        xyF = xF = yF = IdentityFunction.f();
        this.kernel = kernel;
    }

    @Override
    public double valueAt(double x, double y) {
        //noinspection SuspiciousNameCombination
        return xyF.valueAt(kernel.valueAt(xF.valueAt(x), yF.valueAt(y)));
    }

    public BinaryFunction setXFunction(Function2D xF) {
        BinaryFunction res = new BinaryFunction(this);
        res.xF = new UnaryFunction(xF);
        return res;
    }

    public BinaryFunction setYFunction(Function2D yF) {
        BinaryFunction res = new BinaryFunction(this);
        res.yF = new UnaryFunction(yF);
        return res;
    }

    public BinaryFunction setXYFunction(Function2D xyF) {
        BinaryFunction res = new BinaryFunction(this);
        res.xyF = new UnaryFunction(xyF);
        return res;
    }

    public UnaryFunction makeYFix(double yFixValue) {
        return new UnaryFunction(x -> valueAt(x, yFixValue));
    }

    public UnaryFunction makeXFix(double xFixValue) {
        return new UnaryFunction(y -> this.valueAt(xFixValue, y));
    }

    public UnaryFunction makeZFix(double zFixValue, double l, double u, double delta) {
        return new UnaryFunction(x -> {
            var roots = makeXFix(x).sum(-zFixValue).roots(l, u, delta);
            if (!roots.isEmpty())
                return roots.get(0);
            return Double.NaN;
        });
    }

    public UnaryFunction makeZFixByInverse(double zFixValue, double l, double u, double delta) {
        return new UnaryFunction(x -> makeXFix(x).inverse(l, u, delta).valueAt(zFixValue));
    }

    public BinaryFunction partialDerivativeRelativeToX(double delta) {
        return Derivative.partialX(this, delta);
    }

    public BinaryFunction partialDerivativeRelativeToY(double delta) {
        return Derivative.partialY(this, delta);
    }

    public Function3D kernel() {
        return kernel;
    }
}
