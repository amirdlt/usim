package com.usim.ulib.jmath.datatypes.functions;

import com.usim.ulib.jmath.datatypes.tuples.Point2D;
import com.usim.ulib.jmath.functions.utils.*;
import com.usim.ulib.jmath.JMath;
import com.usim.ulib.jmath.functions.unaries.real.ConstantFunction2D;
import com.usim.ulib.jmath.operators.*;

import java.util.List;

@SuppressWarnings("unused")
public class UnaryFunction implements Function2D {
    private final Function2D kernel;

    protected double a;
    protected double b;
    protected double c;
    protected double d;
    protected Function2D sum;
    protected Function2D mul;
    protected Function2D inn;
    protected Function2D out;

    public UnaryFunction(Function2D kernel) {
        this.kernel = kernel;

        a = 1;
        b = 1;
        c = 0;
        d = 0;

        sum = x -> 0;
        mul = x -> 1;
        out = inn = x -> x;
    }

    @Override
    public double valueAt(double x) {
        return a * mul.valueAt(x) * out.valueAt(kernel.valueAt(b * inn.valueAt(x) + c)) + d + sum.valueAt(x);
    }

    public UnaryFunction derivative(double delta) {
        return Derivative.derivative(this, delta);
    }

    public UnaryFunction derivative(double delta, int order) {
        return Derivative.derivative(this, order, delta);
    }

    public UnaryFunction fractionUnder(Function2D numerator) {
        return FunctionUtil.fraction(numerator, this);
    }

    public UnaryFunction fractionOver(Function2D denominator) {
        return FunctionUtil.fraction(this, denominator);
    }

    public UnaryFunction power(double power) {
        return FunctionUtil.power(this, power);
    }

    public UnaryFunction power(Function2D power) {
        return FunctionUtil.power(this, power);
    }

    public UnaryFunction multiply(Function2D... functions) {
        UnaryFunction res = new UnaryFunction(this);
        res.mul = FunctionUtil.multiply(functions);
        return res;
    }

    public UnaryFunction multiply(double verticalScale) {
        UnaryFunction res = new UnaryFunction(this);
        res.a = verticalScale;
        return res;
    }

    public UnaryFunction sum(Function2D... functions) {
        UnaryFunction res = new UnaryFunction(this);
        res.sum = FunctionUtil.sum(functions);
        return res;
    }

    public UnaryFunction sum(double verticalShift) {
        UnaryFunction res = new UnaryFunction(this);
        res.d = verticalShift;
        return res;
    }

    public UnaryFunction sub(Function2D function) {
        UnaryFunction res = new UnaryFunction(this);
        res.sum = new UnaryFunction(function).multiply(-1);
        return res;
    }

    public UnaryFunction fourierSeries(int n, double l, double u, double delta) {
        return FourierSeries.sN(n, this, l, u, delta);
    }

    public UnaryFunction laplaceTransform(double delta) {
        return LaplaceTransform.laplaceOf(this, delta);
    }

    public UnaryFunction setInnerFunction(Function2D... functions) {
        UnaryFunction res = new UnaryFunction(this);
        res.inn = FunctionUtil.combine(functions);
        return res;
    }

    public UnaryFunction setOuterFunction(Function2D... functions) {
        UnaryFunction res = new UnaryFunction(this);
        res.out = FunctionUtil.combine(functions);
        return res;
    }

    public UnaryFunction inverse(double l, double u, double delta) {
        return InverseFinder.byReSampling(this, l, u, delta);
    }

    public Arc2D asArc2D() {
        return t -> new Point2D(t, valueAt(t));
    }

    public Arc2D inverseAsArc(double l, double u, double lt, double ut, double delta) {
        var sample = sample(l, u, delta);
        sample.forEach(Point2D::inverse);
        return Sampling.sampleToArc(sample, lt, ut);
    }

    public Arc2D inverseAsArc(double l, double u, double delta) {
        return inverseAsArc(l, u, -1, 1, delta);
    }

    public Point2D highestPoint(double l, double u, double delta) {
        return FunctionAnalyser.highestPoint(this, l, u, delta);
    }

    public Point2D lowestPoint(double l, double u, double delta) {
        return FunctionAnalyser.lowestPoint(this, l, u, delta);
    }

    public List<Point2D> sample(double l, double u, double delta) {
        return Sampling.sample(this, l, u, delta);
    }

    public List<Point2D> multiThreadSampling(double l, double u, double delta, int numOfThreads) {
        return Sampling.multiThreadSampling(this, l, u, delta, numOfThreads);
    }

    public List<Point2D> stationaryPoints(double l, double u, double delta) {
        return FunctionAnalyser.stationaryPoints(this, l, u, delta);
    }

    public List<Double> roots(double l, double u, double delta) {
        return RootsFinder.bySampling(this, l, u, delta);
    }

    public UnaryFunction taylorSeries(int order, double x0, double delta) {
        return TaylorSeries.taylorSeries(order, this, x0, delta);
    }

    public UnaryFunction taylorSeries(int order, double delta) {
        return TaylorSeries.taylorSeries(order, this, 0, delta);
    }

    public BinaryFunction lengthFunction(double delta) {
        return new BinaryFunction((x, y) -> new UnaryFunction(
                JMath.sqrt(xx -> 1 + this.derivative(delta).power(2).valueAt(xx))).integral(x, y, delta));
    }

    public boolean isConstant(double value) {
        return ConstantFunction2D.isConstant(this, value);
    }

    public boolean isConstant() {
        return ConstantFunction2D.isConstant(this);
    }

    public double integral(double l, double u, double delta) {
        return Integral.byDefinition(this, l, u, delta);
    }

    public UnaryFunction integral(Function2D xL, Function2D xU, double delta) {
        return Integral.byDefinition(this, xL, xU, delta);
    }

    public Arc2D asPolarArc(boolean rExpression) {
        if (rExpression)
            return t -> new Point2D(valueAt(t) * Math.cos(t), valueAt(t) * Math.sin(t));
        return t -> new Point2D(t * Math.cos(valueAt(t)), t * Math.sin(valueAt(t)));
    }

    public Arc2D asPolarArc() {
        return asPolarArc(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnaryFunction)) return false;
        var sub = FunctionUtil.sub(this, (UnaryFunction) o);
        boolean isEqual = true;

        try {
            for (int i = -200; i < 200; i++)
                if (Math.abs(sub.valueAt(Math.random() * i)) > 0.000000001)
                    isEqual = false;
        } catch (Exception ignored) {
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
