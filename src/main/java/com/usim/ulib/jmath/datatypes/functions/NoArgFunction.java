package com.usim.ulib.jmath.datatypes.functions;

import java.awt.*;

@FunctionalInterface
public interface NoArgFunction<Y> extends Function<Y, Void> {
    Y value();

    @Override
    default Y valueAt(Void unused) {
        return value();
    }

    @Override
    default Y atOrigin() {
        return null;
    }

    @Override
    default void render(Graphics2D g2d) {
        System.err.println("AHD:: Not Implemented Yet: " + getClass());
    }
}
