package ahd.usim.ulib.utils.supplier;

import ahd.usim.ulib.jmath.datatypes.functions.NoArgFunction;

import java.awt.*;

@FunctionalInterface
public interface ColorSupplier extends NoArgFunction<Color> {
    Color getColor();

    @Override
    default Color value() {
        return getColor();
    }
}
