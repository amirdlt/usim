package ahd.usim.ulib.utils.supplier;

import ahd.usim.ulib.jmath.datatypes.functions.NoArgFunction;

@FunctionalInterface
public interface BooleanSupplier extends NoArgFunction<Boolean> {
    boolean is();

    @Override
    default Boolean value() {
        return is();
    }
}
