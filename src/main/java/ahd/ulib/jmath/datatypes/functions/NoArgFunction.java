package ahd.ulib.jmath.datatypes.functions;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

@FunctionalInterface
public interface NoArgFunction<Y> extends Function<Y, Void> {
    Y value();

    @Override
    default Y valueAt(Void unused) {
        return value();
    }

    @Override
    default Y atOrigin() {
        return null;
    }

    @Override
    default void render(@NotNull Graphics2D g2d) {
        System.err.println("AHD:: Not Implemented Yet: " + getClass());
    }
}
