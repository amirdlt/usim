package com.usim.ulib.visualization.canvas;

@FunctionalInterface
public interface Tick extends Runnable {
    void tick();

    @Override
    default void run() {
        tick();
    }

    default void tick(int repeats) {
        for (int i = 0; i < repeats; i++)
            tick();
    }
}
