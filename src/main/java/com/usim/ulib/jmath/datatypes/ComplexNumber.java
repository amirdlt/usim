package com.usim.ulib.jmath.datatypes;

import com.usim.ulib.jmath.datatypes.tuples.Point2D;

import java.util.Objects;

@SuppressWarnings("unused")
public class ComplexNumber implements Comparable<ComplexNumber> {
    public static final ComplexNumber i = new ComplexNumber(0, 1, false);

    public final double realValue;
    public final double imaginaryValue;
    public final double phase;
    public final double absoluteValue;

    public ComplexNumber(double absOrR, double phaseOrI, boolean isPolarForm) {
        if (!isPolarForm) {
            this.realValue = absOrR;
            this.imaginaryValue = phaseOrI;
            phase = Math.atan2(imaginaryValue, realValue);
            absoluteValue = Math.sqrt(realValue*realValue+imaginaryValue*imaginaryValue);
        } else {
            this.absoluteValue = absOrR;
            this.phase = stdPhase(phaseOrI);
            realValue = absoluteValue * Math.cos(phase);
            imaginaryValue = absoluteValue * Math.sin(phase);
        }
    }

    public ComplexNumber(ComplexNumber c) {
        absoluteValue = c.absoluteValue;
        realValue = c.realValue;
        phase = c.phase;
        imaginaryValue = c.imaginaryValue;
    }

    public ComplexNumber power(double power) {
        return new ComplexNumber(Math.pow(absoluteValue, power), power*phase, true);
    }

    public ComplexNumber power(ComplexNumber c) {
        return new ComplexNumber(Math.exp(c.realValue * Math.log(absoluteValue) - phase * c.imaginaryValue),
                c.imaginaryValue * Math.log(absoluteValue) + c.realValue * phase, true);
    }

    public ComplexNumber sum(ComplexNumber... numbers) {
        double rValue = realValue;
        double iValue = imaginaryValue;
        for (var c : numbers) {
            rValue += c.realValue;
            iValue += c.imaginaryValue;
        }
        return new ComplexNumber(rValue, iValue, false);
    }

    public ComplexNumber sub(ComplexNumber... numbers) {
        var number = new ComplexNumber(0, 0, false).sum(numbers);
        return new ComplexNumber(realValue - number.realValue, imaginaryValue - number.imaginaryValue, false);
    }

    public ComplexNumber mul(ComplexNumber... numbers) {
        double abs = absoluteValue;
        double arg = phase;
        for (var c : numbers) {
            abs *= c.absoluteValue;
            arg += c.phase;
        }
        return new ComplexNumber(abs, arg, true);
    }

    public ComplexNumber div(ComplexNumber number, boolean isNominator) {
        if (isNominator) {
            return new ComplexNumber(number.absoluteValue / absoluteValue, number.phase - phase, true);
        } else {
            return new ComplexNumber(absoluteValue / number.absoluteValue, phase-  number.phase, true);
        }
    }

    public boolean isPureReal() {
        return isPureReal(this);
    }

    public boolean isPureImaginary() {
        return isPureImaginary(this);
    }

    public boolean isComplex() {
        return isComplex(this);
    }

    public Point2D asPoint() {
        return asPoint(false);
    }

    public Point2D asPoint(boolean isPolar) {
        if (isPolar)
            return new Point2D(absoluteValue, phase);
        return new Point2D(realValue, imaginaryValue);
    }

    public static double stdPhase(double phase) {
        while (phase > Math.PI)
            phase -= Math.PI;
        while (phase < -Math.PI)
            phase += Math.PI;
        return phase;
    }

    public static boolean isPureReal(ComplexNumber c) {
        return c.imaginaryValue == 0;
    }

    public static boolean isPureImaginary(ComplexNumber c) {
        return c.realValue == 0;
    }

    public static boolean isComplex(ComplexNumber c) {
        return !isPureReal(c) && !isPureImaginary(c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComplexNumber)) return false;
        ComplexNumber that = (ComplexNumber) o;
        return Double.compare(that.realValue, realValue) == 0 &&
                Double.compare(that.imaginaryValue, imaginaryValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(realValue, imaginaryValue, phase, absoluteValue);
    }

    @Override
    public String toString() {
        return "ComplexNumber{" +
                "realValue=" + realValue +
                ", imaginaryValue=" + imaginaryValue +
                '}';
    }

    @Override
    public int compareTo(ComplexNumber o) {
        return Double.compare(this.absoluteValue, o.absoluteValue);
    }
}
