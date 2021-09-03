package com.usim.ulib.visualization.fractals;

public final class ColorScheme {
    private int maxIteration;

    public ColorScheme(int maxIteration) {
        this.maxIteration = maxIteration;
    }

    public int getMaxIteration() {
        return maxIteration;
    }

    public void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
    }

    public int getColor(int factor) {
        int a = (int) (255.0 * 4.0 * factor / maxIteration);
        return (2 * a << 16) | (a << 8) | (2 * a);
    }
}
