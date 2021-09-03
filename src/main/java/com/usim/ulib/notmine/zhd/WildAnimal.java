package com.usim.ulib.notmine.zhd;

public class WildAnimal extends Animal {
    public static int stop;

    public WildAnimal(int x, int y, int step, int stop) {
        super(x, y, step);
        this.stop = stop;
    }
}
