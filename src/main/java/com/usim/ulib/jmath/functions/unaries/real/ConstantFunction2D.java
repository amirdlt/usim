package com.usim.ulib.jmath.functions.unaries.real;

import com.usim.ulib.jmath.datatypes.functions.Function2D;
import com.usim.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class ConstantFunction2D extends UnaryFunction {

    private ConstantFunction2D(double value) {
        super(x -> value);
    }

    public static UnaryFunction f(double value) {
        return new ConstantFunction2D(value);
    }

    public static UnaryFunction zero() {
        return f(0);
    }

    public static UnaryFunction one() {
        return f(1);
    }

    public static UnaryFunction NaN() {
        return f(Double.NaN);
    }

    public static boolean isConstant(Function2D function) {
        var v = function.valueAt(1);
        boolean res = true;
        for (int i = 0; i < 30; i++)
            if (function.valueAt(Math.random() * 500) != v)
                res = false;
        return res;
    }

    public static boolean isConstant(Function2D function, double value) {
        boolean res = true;
        for (int i = 0; i < 30; i++)
            if (function.valueAt(Math.random() * 500) != value)
                res = false;
        return res;
    }

    public static boolean isZeroConstant(Function2D function) {
        return function.equals(zero());
    }

    public static boolean isOneConstant(Function2D function) {
        return function.equals(one());
    }
}
