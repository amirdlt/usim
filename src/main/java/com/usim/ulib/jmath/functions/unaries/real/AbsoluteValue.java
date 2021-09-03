package com.usim.ulib.jmath.functions.unaries.real;

import com.usim.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public class AbsoluteValue extends UnaryFunction {
    private AbsoluteValue() {
        super(Math::abs);
    }

    public static UnaryFunction f() {
        return new AbsoluteValue();
    }
}
