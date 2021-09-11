package com.usim.ulib.jmath.datatypes.functions;

import com.usim.ulib.jmath.datatypes.tuples.Point2D;
import com.usim.ulib.jmath.datatypes.ComplexNumber;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@FunctionalInterface
public interface CFunction extends Function<ComplexNumber, ComplexNumber> {
    ComplexNumber valueAt(ComplexNumber c);
    default ComplexNumber valueAt(double real, double imaginary) {
        return valueAt(new ComplexNumber(real, imaginary, false));
    }
    default ComplexNumber valueAt(@NotNull Point2D c, boolean isPolar) {return valueAt(new ComplexNumber(c.x, c.y, isPolar));}
    default ComplexNumber valueAt(Point2D c) {return valueAt(c, false);}
    default Point2D valueAtAsPoint(ComplexNumber z) {return valueAt(z).asPoint();}
    default Point2D valueAtAsPoint(Point2D c, boolean isPolar) {return valueAt(c, isPolar).asPoint();}
    default Point2D valueAtAsPoint(Point2D c) {return valueAt(c, false).asPoint();}
    default List<Point2D> onList(List<Point2D> domain) {
        var res = new ArrayList<>(domain);
        res.replaceAll(this::valueAtAsPoint);
        return res;
    }
    default double phase(ComplexNumber z) {
        return valueAt(z).phase;
    }
    default double phase(double x, double y) {
        return phase(new ComplexNumber(x, y, false));
    }
    default double abs(ComplexNumber z) {
        return valueAt(z).absoluteValue;
    }
    default double abs(double x, double y) {
        return phase(new ComplexNumber(x, y, false));
    }
    default UnaryFunction mapF2DAbsoluteValue(Function2D f) {
        return new UnaryFunction(x -> valueAt(x, f.valueAt(x)).absoluteValue);
    }
    default CFunction mapF2D(Function2D f) {
        return x -> valueAt(x.realValue, f.valueAt(x.imaginaryValue));
    }
    default UnaryFunction mapF2DPhase(Function2D f) {
        return new UnaryFunction(x -> valueAt(x, f.valueAt(x)).phase);
    }
    default BinaryFunction real() {
        return new BinaryFunction((x, y) -> valueAt(x, y).realValue);
    }
    default BinaryFunction imaginary() {
        return new BinaryFunction((x, y) -> valueAt(x, y).imaginaryValue);
    }
    default BinaryFunction phase() {
        return new BinaryFunction((x, y) -> valueAt(x, y).phase);
    }
    default BinaryFunction abs() {
        return new BinaryFunction((x, y) -> valueAt(x, y).absoluteValue);
    }
}
