package ahd.usim.ulib.utils.api;

import org.jetbrains.annotations.NotNull;

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
    default List<K> statesOf(@NotNull V value) {
        //noinspection unchecked
        return (List<K>) stateMap().values().stream().filter(value::equals).toList();
    }
    default K oneOfStatesOf(@NotNull V value) {
        //noinspection unchecked
        return (K) stateMap().values().stream().filter(value::equals).findAny().orElse(null);
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
