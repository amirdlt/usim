package com.usim.ulib.jmath.datatypes.tuples;

import java.io.Serializable;

public interface AbstractPoint extends Serializable {
    int CoordinateX = 0;
    int CoordinateY = 1;
    int CoordinateZ = 2;
    int CoordinateW = 3;

    double getCoordinate(int numOfCoordinate);
    double distanceFromOrigin();
    int numOfCoordinates();
    void setCoordinate(int numOfCoordinate, double newValue);
    double squareOfDistanceFromOrigin();
    default boolean testAndSet(int numOfCoordinate, double oldValue, double newValue) {
        if (getCoordinate(numOfCoordinate) != oldValue)
            return false;
        setCoordinate(numOfCoordinate, newValue);
        return true;
    }
    default double distanceFrom(AbstractPoint p) {
        var dim = Math.min(p.numOfCoordinates(), numOfCoordinates());
        double res = 0;
        for (int i = 0; i < dim; i++) {
            var r = p.getCoordinate(i) - getCoordinate(i);
            res += r*r;
        }
        return Math.sqrt(res);
    }
}
