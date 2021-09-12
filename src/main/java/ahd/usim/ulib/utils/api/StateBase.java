package ahd.usim.ulib.utils.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface StateBase<K, V> {
    K currentState();
    Map<K, V> stateMap();
    void addState(K key, V value);
    void removeState(K key);
    void setState(String state);
    default List<K> stateKeys() {
        return new ArrayList<>(stateMap().keySet());
    }
    default List<K> statesOf(V value) {
        var res = new ArrayList<K>();
        for (var kv : stateMap().entrySet())
            if (value.equals(kv.getValue()))
                res.add(kv.getKey());
        return res;
    }
    default K oneOfStatesOf(V value) {
        for (var kv : stateMap().entrySet())
            if (value.equals(kv.getValue()))
                return kv.getKey();
        throw new RuntimeException("AHD:: There is no key with this value");
    }
    default int numOfStates() {
        return stateMap().size();
    }
    default boolean isExist(K key) {
        return stateMap().containsKey(key);
    }
    default V getState(K key) {
        return stateMap().get(key);
    }
}
