package ahd.ulib.jmath.datatypes.tuples;

import java.util.Objects;

public class ComparablePair<X extends Comparable<X>,
        Y extends  Comparable<Y>> implements Comparable<ComparablePair<X, Y>> {
    private X x;
    private Y y;

    public ComparablePair(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public static <X extends Comparable<X>, Y extends Comparable<Y>> ComparablePair<X, Y> create(X x, Y y) {
        return new ComparablePair<>(x, y);
    }

    public X getX() {
        return x;
    }

    public Y getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComparablePair)) return false;
        ComparablePair<?, ?> pair = (ComparablePair<?, ?>) o;
        return Objects.equals(x, pair.x) &&
                Objects.equals(y, pair.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(ComparablePair o) {
        int res = this.x.compareTo((X) o.x);
        if (res == 0)
            res = this.y.compareTo((Y) o.y);
        return res;
    }
}
