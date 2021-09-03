package com.usim.ulib.notmine.tmp13;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        Cone[] cones = new Cone[100];
        Arrays.setAll(cones, i -> new Cone(new Point(Math.random(), Math.random(), Math.random()),
                new Point(Math.random(), Math.random(), Math.random()), Math.random()));
        System.out.println("least area: " + Collections.min(Arrays.asList(cones), Comparator.comparingDouble(Cone::area)));
        System.out.println("most volume: " + Collections.max(Arrays.asList(cones), Comparator.comparingDouble(Cone::volume)));
    }
}
