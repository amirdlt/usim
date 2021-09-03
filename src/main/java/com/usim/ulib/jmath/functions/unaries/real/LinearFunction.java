package com.usim.ulib.jmath.functions.unaries.real;

import com.usim.ulib.jmath.datatypes.functions.UnaryFunction;
import com.usim.ulib.jmath.datatypes.tuples.Point2D;

public class LinearFunction extends UnaryFunction {
    private LinearFunction(double slope, double widthFromOrigin) {
        super(x -> slope * x + widthFromOrigin);
    }

    public static UnaryFunction f(double slope, double widthFromOrigin) {
        return new LinearFunction(slope, widthFromOrigin);
    }

    public static UnaryFunction f(double slope) {
        return f(slope, 0);
    }

    public static UnaryFunction f() {
        return IdentityFunction.f();
    }

    public static UnaryFunction f(Point2D p1, Point2D p2) {
        var slope = (p2.y - p1.y) / (p2.x - p1.x);
        return f(slope, p1.y - slope * p1.x);
    }
}
