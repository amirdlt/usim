package ahd.ulib.jmath;

import ahd.ulib.jmath.datatypes.functions.BinaryFunction;
import ahd.ulib.jmath.datatypes.functions.Function2D;
import ahd.ulib.jmath.datatypes.functions.UnaryFunction;

@SuppressWarnings("unused")
public final class JMath {

    public static UnaryFunction sin(Function2D... inners) {
        return new UnaryFunction(Math::sin).setInnerFunction(inners);
    }

    public static UnaryFunction cos(Function2D... inners) {
        return new UnaryFunction(Math::cos).setInnerFunction(inners);
    }

    public static UnaryFunction tan(Function2D... inners) {
        return new UnaryFunction(Math::tan).setInnerFunction(inners);
    }

    public static UnaryFunction cot(Function2D... inners) {
        return new UnaryFunction(x -> 1 / Math.tan(x)).setInnerFunction(inners);
    }

    public static UnaryFunction sec(Function2D... inners) {
        return new UnaryFunction(x -> 1 / Math.cos(x)).setInnerFunction(inners);
    }

    public static UnaryFunction csc(Function2D... inners) {
        return new UnaryFunction(x -> 1 / Math.sin(x)).setInnerFunction(inners);
    }

    public static BinaryFunction pow() {
        return new BinaryFunction(Math::pow);
    }

    public static UnaryFunction floor(Function2D... inners) {
        return new UnaryFunction(Math::floor).setInnerFunction(inners);
    }

    public static UnaryFunction ceil(Function2D... inners) {
        return new UnaryFunction(Math::ceil).setInnerFunction(inners);
    }

    public static UnaryFunction atan(Function2D... inners) {
        return new UnaryFunction(Math::atan).setInnerFunction(inners);
    }

    public static BinaryFunction modulo() {
        return new BinaryFunction((x, y) -> x % y);
    }

    public static UnaryFunction identical(Function2D... inners) {
        return new UnaryFunction(x -> x).setInnerFunction(inners);
    }

    public static UnaryFunction sqrt(Function2D... inners) {
        return new UnaryFunction(Math::sqrt).setInnerFunction(inners);
    }

    public static UnaryFunction abs(Function2D... inners) {
        return new UnaryFunction(Math::abs).setInnerFunction(inners);
    }

    public static BinaryFunction atan2() {
        return new BinaryFunction(Math::atan2);
    }

    public static UnaryFunction acos(Function2D... inners) {
        return new UnaryFunction(Math::acos).setInnerFunction(inners);
    }

    public static UnaryFunction asin(Function2D... inners) {
        return new UnaryFunction(Math::asin).setInnerFunction(inners);
    }

    public static UnaryFunction asec(Function2D... inners) {
        return new UnaryFunction(sec().inverse(-Math.PI / 2, Math.PI / 2, 0.001)).setInnerFunction(inners);
    }

    public static UnaryFunction acsc(Function2D... inners) {
        return new UnaryFunction(sec().inverse(0, Math.PI, 0.001)).setInnerFunction(inners);
    }

    public static UnaryFunction exp(Function2D... inners) {
        return new UnaryFunction(Math::exp).setInnerFunction(inners);
    }

    public static UnaryFunction log10(Function2D... inners) {
        return new UnaryFunction(Math::log10).setInnerFunction(inners);
    }

    public static UnaryFunction log(Function2D... inners) {
        return new UnaryFunction(Math::log).setInnerFunction(inners);
    }

    public static UnaryFunction log2(Function2D... inners) {
        return new UnaryFunction(x -> Math.log(x) / Math.log(2)).setInnerFunction(inners);
    }

    public static void main(String[] args) {
    }
}
