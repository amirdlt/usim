package ahd.ulib.utils.supplier;

import ahd.ulib.jmath.datatypes.functions.NoArgFunction;

@FunctionalInterface
public interface BooleanSupplier extends NoArgFunction<Boolean> {
    boolean is();

    @Override
    default Boolean value() {
        return is();
    }
}
