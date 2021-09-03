package com.usim.ulib.jmath.functions.unaries.real;

import com.usim.ulib.jmath.datatypes.functions.UnaryFunction;

public class Floor extends UnaryFunction {
    private Floor() {
        super(Math::floor);
    }

    public static UnaryFunction f() {
        return new Floor();
    }
}
