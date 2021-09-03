package com.usim.ulib.jmath.functions.unaries.real;

import com.usim.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class Cotangent extends UnaryFunction {
    private Cotangent() {
        super(x -> 1 / Math.tan(x));
    }

    public static UnaryFunction f() {
        return new Cotangent();
    }
}
