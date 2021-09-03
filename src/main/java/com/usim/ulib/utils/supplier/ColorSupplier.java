package com.usim.ulib.utils.supplier;

import com.usim.ulib.jmath.datatypes.functions.NoArgFunction;

import java.awt.*;

@FunctionalInterface
public interface ColorSupplier extends NoArgFunction<Color> {
    Color getColor();

    @Override
    default Color value() {
        return getColor();
    }
}
