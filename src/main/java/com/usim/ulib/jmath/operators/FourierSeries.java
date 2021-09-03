package com.usim.ulib.jmath.operators;

import com.usim.ulib.jmath.datatypes.Operator;
import com.usim.ulib.jmath.datatypes.functions.Function2D;
import com.usim.ulib.jmath.datatypes.functions.UnaryFunction;

import static java.lang.Math.*;

public class FourierSeries implements Operator<UnaryFunction> {
    private static final int MAX_NUM_OF_POINTS = 10000;

    public static double aN(int n, Function2D f, double l, double u, double delta) {
        double p = u - l;
        return (2 / p) * Integral.byDefinition(x -> f.valueAt(x) * cos(2 * PI * x * n / p), l, u, delta);
    }

    public static double bN(int n, Function2D f, double l, double u, double delta) {
        double p = u - l;
        return (2 / p) * Integral.byDefinition(x -> f.valueAt(x) * sin(2 * PI * x * n / p), l, u, delta);
    }

    public static UnaryFunction sN(int n, Function2D f, double l, double u, double delta) {
        int numOfPoints = (int) Math.abs((u - l) / delta);
        if (numOfPoints > MAX_NUM_OF_POINTS)
            delta = Math.abs((u - l) / MAX_NUM_OF_POINTS);
        double finalDelta = delta;
        return new UnaryFunction(x -> {
            double res = aN(0, f, l, u, finalDelta) / 2;
            double p = u - l;
            for (int i = 1; i <= n; i++)
                res += aN(i, f, l, u, finalDelta) * cos(2 * PI * x * i / p) + bN(i, f, l, u, finalDelta) * sin(2 * PI * x * i / p);
            return res;
        });
    }

    @Override
    public UnaryFunction operate(Function2D f) {
        return null;
    }
}
