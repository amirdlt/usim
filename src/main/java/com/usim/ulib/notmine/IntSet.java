package com.usim.ulib.notmine;

public class IntSet {
    private final boolean[] elements;

    public IntSet(int[] elements) {
        this.elements = new boolean[101];
        for (int element : elements)
            this.elements[element] = true;
    }

    public IntSet() {
        this(new int[0]);
    }

    public void addElement(int n) {
        elements[n] = true;
    }

    public static IntSet intersection(IntSet set1, IntSet set2) {
        IntSet intersection = new IntSet();
        for (int i = 0; i < set1.elements.length; i++)
            if (set2.elements[i])
                intersection.addElement(i);
        return intersection;
    }
}
