package com.usim.ulib.jmath.datatypes.functions;


import com.usim.ulib.jmath.datatypes.tuples.Point2D;
import com.usim.ulib.jmath.datatypes.tuples.Point3D;

public class TernaryFunction implements Function4D {

    private final Function4D kernel;
    private final TernaryFunction[] allDimensions;
    private final Point2D LOW_UP_BOUNDS;
    private final double[] delta;

    public TernaryFunction(Function4D... kernels) {
        delta = new double[kernels.length];
        LOW_UP_BOUNDS = new Point2D(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        if (kernels.length == 0) {
            kernel = (x, y, z) -> Double.NaN;
            allDimensions = new TernaryFunction[1];
            allDimensions[0] = this;
            return;
        }
        allDimensions = new TernaryFunction[kernels.length];
        kernel = kernels[0];
        allDimensions[0] = this;
        for (int i = 1; i < kernels.length; i++) {
            delta[i] = 0.01;
            allDimensions[i] = new TernaryFunction(kernels[i]);
        }
    }

    public int getDimension() {
        return allDimensions.length;
    }

    @Override
    public double valueAt(double x, double y, double z) {
        return kernel.valueAt(x, y, z);
    }

    public Mapper3D asMapper3D() {
        if (allDimensions.length > 2)
            return p -> new Point3D(allDimensions[0].valueAt(p), allDimensions[1].valueAt(p), allDimensions[2].valueAt(p));
        return Mapper3D.NaN;
    }

    public Mapper2D asMapper2D(double zFixValue) {
        if (allDimensions.length > 1)
            return (x, y) -> new Point2D(
                    allDimensions[0].valueAt(new Point3D(x, y, zFixValue)),
                    allDimensions[1].valueAt(new Point3D(x, y, zFixValue)));
        return Mapper2D.NaN;
    }

    public Arc2D asArc2D(double yFixValue, double zFixValue) {
        if (allDimensions.length > 1)
            return t -> new Point2D(
                    allDimensions[0].valueAt(t, yFixValue, zFixValue),
                    allDimensions[1].valueAt(t, yFixValue, zFixValue));
        return Arc2D.NaN;
    }

    public Arc3D asArc3D(double yFixValue, double zFixValue) {
        if (allDimensions.length > 2)
            return t -> new Point3D(
                    allDimensions[0].valueAt(t, yFixValue, zFixValue),
                    allDimensions[1].valueAt(t, yFixValue, zFixValue),
                    allDimensions[2].valueAt(t, yFixValue, zFixValue));
        return Arc3D.NaN;
    }

    public Function4D fx() {
        return kernel;
    }

    public Function4D fy() {
        return allDimensions.length < 2 ? Function4D.NaN : allDimensions[1];
    }

    public Function4D fz() {
        return allDimensions.length < 3 ? Function4D.NaN : allDimensions[2];
    }

    public Function4D fw() {
        return allDimensions.length < 4 ? Function4D.NaN : allDimensions[3];
    }

    public Surface asSurface(double zFixValue) {
        if (allDimensions.length > 2)
            return (x, y) -> new Point3D(
                    allDimensions[0].valueAt(x, y, zFixValue),
                    allDimensions[1].valueAt(x, y, zFixValue),
                    allDimensions[2].valueAt(x, y, zFixValue));
        return Surface.NaN;
    }

    public Point2D getBounds() {
        return LOW_UP_BOUNDS;
    }

    public TernaryFunction setBounds(double x, double y) {
        LOW_UP_BOUNDS.set(x, y);
        return this;
    }

    public double[] getDelta() {
        return delta;
    }

    public TernaryFunction setDelta(double... deltas) {
        System.arraycopy(deltas, 0, delta, 0, deltas.length);
        return this;
    }
}
