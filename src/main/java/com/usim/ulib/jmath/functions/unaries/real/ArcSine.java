package com.usim.ulib.jmath.functions.unaries.real;

import com.usim.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class ArcSine extends UnaryFunction {
    private ArcSine() {
        super(Math::asin);
    }

    public static UnaryFunction f() {
        return new ArcSine();
    }
}
