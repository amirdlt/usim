package ahd.ulib.jmath.datatypes.tuples;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SortableTuple<T extends Comparable<? super T>> implements Serializable {
    private final List<T> elements;

    @SafeVarargs
    public SortableTuple(T... elements) {
        this(new ArrayList<>(Arrays.asList(elements)));
    }

    public SortableTuple(List<T> elements) {
        this.elements = new ArrayList<>();
        this.elements.addAll(elements);
    }

    @SafeVarargs
    public final void addElements(T... elements) {
        this.elements.addAll(new ArrayList<>(Arrays.asList(elements)));
    }

    public void addElement(int index, T e) {
        elements.add(index, e);
    }

    public void removeAll() {
        elements.clear();
    }

    public int dimension() {
        return elements.size();
    }

    public void remove(int... indexes) {
        for (var i : indexes)
            elements.remove(i);
    }

    @SafeVarargs
    public final void remove(T... elements) {
        this.elements.removeAll(new ArrayList<>(Arrays.asList(elements)));
    }

    public void sort() {
        if (elements.isEmpty())
            return;
        Collections.sort(elements);
    }

    public SortableTuple<T> subTuple(int indexStart, int indexEnd, int step) {
        var nels = new ArrayList<T>();
        for (int i = indexStart; i < indexEnd; i += step)
            nels.add(elements.get(i));
        return new SortableTuple<>(nels);
    }

    public List<T> getElements() {
        return elements;
    }

    public Point2D asPoint2D() {
        if (dimension() < 2 || !(elements.get(0) instanceof Double) || !(elements.get(1) instanceof Double))
            throw new RuntimeException("AHD:: Not suitable dimension");
        return new Point2D((Double) elements.get(0), (Double) elements.get(1));
    }

    public Point3D asPoint3D() {
        if (dimension() < 3 || !(elements.get(0) instanceof Double) ||
                !(elements.get(1) instanceof Double) || !(elements.get(2) instanceof Double))
            throw new RuntimeException("AHD:: Not suitable dimension");
        return new Point3D((Double) elements.get(0), (Double) elements.get(1), (Double) elements.get(2));
    }
}

