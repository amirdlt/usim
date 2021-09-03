package com.usim.ulib.visualization.canvas;

import java.awt.*;

@FunctionalInterface
public interface Render extends Tick {
    void render(Graphics2D g2d);

    default boolean inViewPort() {
        return true;
    }

    default void renderIfInView(Graphics2D g2d) {
        if (isVisible() && inViewPort())
            render(g2d);
    }

    default boolean isVisible() {
        return true;
    }

    @Override
    default void tick() {}
}
