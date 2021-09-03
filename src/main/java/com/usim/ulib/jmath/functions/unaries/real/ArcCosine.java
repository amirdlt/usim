package com.usim.ulib.jmath.functions.unaries.real;

import com.usim.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class ArcCosine extends UnaryFunction {
    private ArcCosine() {
        super(Math::acos);
    }

    public static UnaryFunction f() {
        return new ArcCosine();
    }
}
