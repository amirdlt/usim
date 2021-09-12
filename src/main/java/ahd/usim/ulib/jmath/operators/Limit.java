package ahd.usim.ulib.jmath.operators;

import ahd.usim.ulib.jmath.datatypes.Operator;
import ahd.usim.ulib.jmath.datatypes.functions.Function2D;

@SuppressWarnings("unused")
public final class Limit implements Operator<Double> {
    public final static int RIGHT = 1;
    public final static int LEFT = -1;
    public final static int BOTH = 0;
    private final static Double PRECISION = 5 * Math.pow(10, -5);
//    private final double INF = Math.pow(10, 15);
    private int mode = BOTH;
    private double x;

    private Limit(double xToApproach) {
        x = xToApproach;
    }

    private void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public Double operate(Function2D f) {
        double stepLen = PRECISION / 1000;
        double n = f.valueAt(x + stepLen);
        double p = f.valueAt(x - stepLen);
        switch (mode) {
            case BOTH:
                if (Math.abs(n - p) < PRECISION)
                    return (n + p) / 2;
                return Double.NaN;
            case LEFT:
                return p;
            case RIGHT:
                return n;
        }

        return Double.NaN;
    }

    public static double limit(Function2D f, double x, int mode, double delta) {
        Limit l = new Limit(x);
        l.setMode(mode);

        try {
            double scale = Math.pow(10, 6);
            return Math.round(l.operate(f) * scale) / scale;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return Double.NaN;
    }
}
