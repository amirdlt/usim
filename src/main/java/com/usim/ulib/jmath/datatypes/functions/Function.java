package com.usim.ulib.jmath.datatypes.functions;


import com.usim.ulib.visualization.canvas.Render;

import java.awt.*;
import java.io.Serializable;

@FunctionalInterface
@SuppressWarnings("unused")
public interface Function<Y, X> extends Serializable, Render {
    Y valueAt(X x);
    default Y atOrigin() {return null;}
    default Y valueAt_(Object x) {
        //noinspection unchecked
        return valueAt((X) x);
    }

    @Override
    default void render(Graphics2D g2d) {}
}
