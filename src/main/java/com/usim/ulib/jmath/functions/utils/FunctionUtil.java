package com.usim.ulib.jmath.functions.utils;

import com.usim.ulib.jmath.datatypes.functions.Function2D;
import com.usim.ulib.jmath.datatypes.functions.Function4D;
import com.usim.ulib.jmath.datatypes.functions.UnaryFunction;
import com.usim.ulib.jmath.functions.unaries.real.ConstantFunction2D;
import com.usim.ulib.jmath.functions.unaries.real.IdentityFunction;

@SuppressWarnings("unused")
public class FunctionUtil {
    public static UnaryFunction verticalShift(Function2D f, double deltaY) {
        return new UnaryFunction(x -> f.valueAt(x) + deltaY);
    }

    public static UnaryFunction fraction(Function2D numerator, Function2D denominator) {
        return new UnaryFunction(x -> numerator.valueAt(x) / denominator.valueAt(x));
    }

    public static UnaryFunction sub(Function2D f1, Function2D f2) {
        return new UnaryFunction(x -> f1.valueAt(x) - f2.valueAt(x));
    }

    public static UnaryFunction power(Function2D f, double p) {
        if (p == 1)
            return new UnaryFunction(f);
        return new UnaryFunction(x -> Math.pow(f.valueAt(x), p));
    }

    public static UnaryFunction verticalScale(Function2D f, double scale) {
        return new UnaryFunction(x -> f.valueAt(x) * scale);
    }

    public static UnaryFunction horizontalShift(Function2D f, double deltaX) {
        return new UnaryFunction(x -> f.valueAt(x + deltaX));
    }

    public static UnaryFunction multiply(Function2D... functions) {
        if (functions.length == 0)
            return null;
        if (functions.length == 1)
            return new UnaryFunction(functions[0]);
        return new UnaryFunction(x -> {
            double res = 1;
            for (Function2D function : functions)
                res *= function.valueAt(x);
            return res;
        });
    }

    public static UnaryFunction sum(Function2D... functions) {
        if (functions.length == 0)
            return null;
        return new UnaryFunction(x -> {
            double res = 0;
            for (Function2D function : functions)
                res += function.valueAt(x);
            return res;
        });
    }

    public static UnaryFunction power(Function2D base, Function2D power) {
        if (ConstantFunction2D.isOneConstant(power))
            return new UnaryFunction(base);
        return new UnaryFunction(x -> {
            if (x <= 0 && !ConstantFunction2D.isConstant(power))
                return Double.NaN;
            return Math.pow(base.valueAt(x), power.valueAt(x));
        });
    }

    public static UnaryFunction combine(Function2D... functions) {
        if (functions.length == 0)
            return IdentityFunction.f();
        return new UnaryFunction(x -> {
            double res = x;
            for (int i = functions.length - 1; i >= 0; i--)
                res = functions[i].valueAt(res);
            return res;
        });
    }

    public static UnaryFunction modulo(Function2D dividend, Function2D divisor) {
        return new UnaryFunction(x -> dividend.valueAt(x) % divisor.valueAt(x));
    }

    public static Function4D fraction(Function4D numerator, Function4D denominator) {
        return (x, y, z) -> numerator.valueAt(x, y, z) / denominator.valueAt(x, y, z);
    }

    public static Function4D sub(Function4D f1, Function4D f2) {
        return (x, y, z) -> f1.valueAt(x, y, z) - f2.valueAt(x, y, z);
    }

    public static Function4D multiply(Function4D... functions) {
        if (functions.length == 0)
            return null;
        if (functions.length == 1)
            return functions[0];
        return (x, y, z) -> {
            double res = 1;
            for (var function : functions)
                res *= function.valueAt(x, y, z);
            return res;
        };
    }

    public static Function4D sum(Function4D... functions) {
        if (functions.length == 0)
            return null;
        return (x, y, z) -> {
            double res = 0;
            for (var function : functions)
                res += function.valueAt(x, y, z);
            return res;
        };
    }

    public static Function4D power(Function4D base, Function4D power) {
        return (x, y, z) -> Math.pow(base.valueAt(x, y, z), power.valueAt(x, y, z));
    }

    public static Function4D modulo(Function4D dividend, Function4D divisor) {
        return (x, y, z) -> dividend.valueAt(x, y, z) % divisor.valueAt(x, y, z);
    }

}
