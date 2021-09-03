package com.usim.ulib.algo;

import com.usim.ulib.jmath.datatypes.tuples.AbstractPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class PathFinders {
    public static List<AbstractPoint> aStar(AbstractPoint start, AbstractPoint stop, NeighborOwner neighborFunction) {
        var path = new ArrayList<AbstractPoint>();
        var current = start;
        while (!current.equals(stop)) {
            current = Collections.min(neighborFunction.neighbors(current), Comparator.comparingDouble(p -> start.distanceFrom(p) + stop.distanceFrom(p)));
            path.add(current);
            System.out.println(current);
        }
        return path;
    }

    public interface NeighborOwner {
        List<AbstractPoint> neighbors(AbstractPoint p);
    }
}
