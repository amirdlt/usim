package ahd.usim.ulib.visualization.canvas;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

@FunctionalInterface
public interface Render extends Tick {
    void render(@NotNull Graphics2D g2d);

    default boolean inViewPort() {
        return true;
    }

    default void renderIfInView(@NotNull Graphics2D g2d) {
        if (isVisible() && inViewPort())
            render(g2d);
    }

    default boolean isVisible() {
        return true;
    }

    @Override
    default void tick() {}
}
