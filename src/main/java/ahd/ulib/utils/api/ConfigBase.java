package ahd.ulib.utils.api;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@FunctionalInterface
public interface ConfigBase<K, V> {
    Map<K, V> config();

    default V config(K key, V value) {
        config().put(key, value);
        return value;
    }

    default V config(K key) {
        return config().get(key);
    }

    default V configOrElse(K key, @NotNull V defaultValue) {
        var conf = config(key);
        return conf == null ? defaultValue : conf;
    }

    default int asIntC(K key) {
        return (int) config(key);
    }

    default int asIntOrElseC(K key, int defaultValue) {
        var conf = config(key);
        return conf == null ? defaultValue : (int) conf;
    }

    default byte asByteC(K key) {
        return (byte) config(key);
    }

    default byte asByteOrElseC(K key, byte defaultValue) {
        var conf = config(key);
        return conf == null ? defaultValue : (byte) conf;
    }

    default float asFloatC(K key) {
        return (float) config(key);
    }

    default float asFloatOrElseC(K key, float defaultValue) {
        var conf = config(key);
        return conf == null ? defaultValue : (float) conf;
    }

    default double asDoubleC(K key, double defaultValue) {
        var conf = config(key);
        return conf == null ? defaultValue : (double) conf;
    }

    default long asLongC(K key) {
        return (long) config(key);
    }

    default long asLongOrElseC(K key, long defaultValue) {
        var conf = config(key);
        return conf == null ? defaultValue : (long) conf;
    }

    default String asStringC(K key) {
        return (String) config(key);
    }

    default String asStringOrElseC(K key, String defaultValue) {
        var conf = config(key);
        return conf == null ? defaultValue : (String) conf;
    }

    default <T> T asManualTypeC(K key, @NotNull Class<T> manualClass) {
        return manualClass.cast(config(key));
    }

    default <T> T asManualTypeOrElseC(K key, T defaultValue) {
        var conf = config(key);
        return conf == null ? defaultValue : (T) conf;
    }

    default boolean isSetC(K key) {
        return config(key) != null;
    }

    @SuppressWarnings("unused")
    default <T> List<T> asListC(K key, @NotNull Class<T> classOfValues) {
        return (List<T>) config(key);
    }

    @SuppressWarnings("unused")
    default <_K, _V> Map<_K, _V> asMapC(K key, Class<_K> classOfKey, Class<_V> classOfValue) {
        return (Map<_K, _V>) config(key);
    }

    @SuppressWarnings("unused")
    default <T> Collection<T> asCollection(K key, Class<T> classOfValues) {
        return (Collection<T>) config(key);
    }
}
