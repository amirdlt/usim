package ahd.usim.ulib.utils;

import java.util.ArrayList;
import java.util.Collection;

@Deprecated
public class EnArrayList<T> extends ArrayList<T> {

    @Override
    public boolean add(T t) {
        if (!contains(t))
            return super.add(t);
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (var e : this)
            add(e);
        return false;
    }

    @Override
    public void add(int index, T element) {
        if (contains(element))
            return;
        super.add(index, element);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new RuntimeException("AHD:: You should not call it");
    }
}
