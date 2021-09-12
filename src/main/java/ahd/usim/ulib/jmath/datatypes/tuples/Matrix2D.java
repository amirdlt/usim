package ahd.usim.ulib.jmath.datatypes.tuples;

import ahd.usim.ulib.jmath.datatypes.functions.Function;

import java.awt.*;
import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Predicate;

public interface Matrix2D<T> extends Iterator<T>, Serializable {

    T get(int row, int col);

    T get(int num);

    Dimension dimension();

    int numOfRows();

    int numOfCols();

    int size();

    Matrix2D<T> subMatrix(int rowL, int rowU, int colL, int colU);

    Matrix2D<T> minor(int row, int col);

    T[][] getElementsArray2D();

    void writeToArray(T[] destination);

    default void affectOnAll(Function<T, T> function) {
        for (var p : getElementsArray2D())
            for (int i = 0; i < p.length; i++)
                p[i] = function.valueAt(p[i]);
    }

    default void affectIf(Function<T, T> function, Predicate<T> predicate) {
        for (var p : getElementsArray2D())
            for (int i = 0; i < p.length; i++)
                p[i] = predicate.test(p[i]) ? function.valueAt(p[i]) : p[i];
    }
}
