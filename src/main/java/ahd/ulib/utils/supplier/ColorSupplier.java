package ahd.ulib.utils.supplier;

import ahd.ulib.jmath.datatypes.functions.NoArgFunction;

import java.awt.*;

@FunctionalInterface
public interface ColorSupplier extends NoArgFunction<Color> {
    Color getColor();

    @Override
    default Color value() {
        return getColor();
    }
}
