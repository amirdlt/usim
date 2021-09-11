package com.usim.ulib.jmath.datatypes.functions;

import com.usim.ulib.jmath.datatypes.tuples.Point2D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static java.lang.Math.*;

@FunctionalInterface
public interface ColorFunction extends Function<Color, Point2D> {
    Color valueAt(double x, double y);

    @Override
    default Color valueAt(@NotNull Point2D p) {
        return valueAt(p.x, p.y);
    }

    default Color arcTanScaled(double x, double y) {
        return valueAt(2/PI*atan(x)+PI/2, 2/PI*atan(x)+PI/2);
    }
}
