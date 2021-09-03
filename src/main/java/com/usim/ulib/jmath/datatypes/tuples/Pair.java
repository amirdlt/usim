package com.usim.ulib.jmath.datatypes.tuples;

@SuppressWarnings("unused")
public class Pair<K, V> {
    private K k;
    private V v;
    private boolean approximateEquals;
    private final double precisionK;
    private final double precisionV;

    public Pair(K k, V v) {
        this(k, v, false, 0.0001, 0.0001);
    }

    public Pair(K k, V v, boolean approximateEquals, double precisionK, double precisionV) {
        this.k = k;
        this.v = v;
        this.approximateEquals = approximateEquals;
        this.precisionK = precisionK;
        this.precisionV = precisionV;
    }

    public boolean isApproximateEquals() {
        return approximateEquals;
    }

    public void setApproximateEquals(boolean approximateEquals) {
        this.approximateEquals = approximateEquals;
    }

    public K getK() {
        return k;
    }

    public void setK(K k) {
        this.k = k;
    }

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return "Pair[" + k + ", " + v + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Pair<?, ?> that))
            return false;
        if (approximateEquals && that.isApproximateEquals()) {
            Number k1 = (Number) k;
            Number v1 = (Number) v;
            Number k2 = (Number) that.getK();
            Number v2 = (Number) that.getV();
            return Math.abs(k1.doubleValue() - k2.doubleValue()) < precisionK &&
                        Math.abs(v1.doubleValue() - v2.doubleValue()) < precisionV;
        }
        return k.equals(that.getK()) && v.equals(that.getV());
    }
}
