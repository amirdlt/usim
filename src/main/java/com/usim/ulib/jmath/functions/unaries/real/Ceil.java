package com.usim.ulib.jmath.functions.unaries.real;

import com.usim.ulib.jmath.datatypes.functions.UnaryFunction;

public class Ceil extends UnaryFunction {
    private Ceil() {
        super(Math::ceil);
    }

    public static UnaryFunction f() {
        return new Ceil();
    }
}
