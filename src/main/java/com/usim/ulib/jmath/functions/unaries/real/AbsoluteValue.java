package com.usim.ulib.jmath.functions.unaries.real;

import com.usim.ulib.jmath.datatypes.functions.UnaryFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class AbsoluteValue extends UnaryFunction {
    private AbsoluteValue() {
        super(Math::abs);
    }

    @Contract(" -> new")
    public static @NotNull UnaryFunction f() {
        return new AbsoluteValue();
    }
}
