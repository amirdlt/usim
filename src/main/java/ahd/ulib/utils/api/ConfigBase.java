package ahd.ulib.utils.api;

import ahd.ulib.utils.mapper.StringMapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@FunctionalInterface
public interface ConfigBase<K, V> {
    Map<K, V> config();

    private V config(K key, V value) {
        config().put(key, value);
        return value;
    }

    private V config(K key) {
        return config().get(key);
    }

    default <T extends V> T configOrElseC(@NotNull T defaultValue, K... key) {
        var conf = configC(key);
        if (conf == null)
            return (T) configC(defaultValue, key);
        return (T) conf;
    }

    default int asIntC(K... key) {
        return (int) configC(key);
    }

    default int asIntOrElseC(int defaultValue, K... key) {
        var conf = configC(key);
        return conf == null ? defaultValue : (int) conf;
    }

    default int asIntOrElseC(K key, int defaultValue) {
        return asIntOrElseC(defaultValue, key);
    }

    default byte asByteC(K... key) {
        return (byte) configC(key);
    }

    default byte asByteOrElseC(byte defaultValue, K... key) {
        var conf = configC(key);
        return conf == null ? defaultValue : (byte) conf;
    }

    default boolean asBooleanC(K... key) {
        return (boolean) configC(key);
    }

    default float asFloatC(K... key) {
        return (float) configC(key);
    }

    default float asFloatOrElseC(float defaultValue, K... key) {
        var conf = configC(key);
        return conf == null ? defaultValue : (float) conf;
    }

    default double asDoubleC(double defaultValue, K... key) {
        var conf = configC(key);
        return conf == null ? defaultValue : (double) conf;
    }

    default long asLongC(K... key) {
        return (long) configC(key);
    }

    default long asLongOrElseC(long defaultValue, K... key) {
        var conf = configC(key);
        return conf == null ? defaultValue : (long) conf;
    }

    default String asStringC(K... key) {
        return (String) configC(key);
    }

    default String asStringOrElseC(String defaultValue, K... key) {
        var conf = configC(key);
        return conf == null ? defaultValue : (String) conf;
    }

    default <T> T asManualTypeC(@NotNull Class<T> manualClass, K... key) {
        return manualClass.cast(configC(key));
    }

    default <T> T asManualTypeC(K key, @NotNull Class<T> manualClass) {
        return asManualTypeC(manualClass, key);
    }

    default <T> T asManualTypeOrElseC(K key, T defaultValue) {
        var conf = config(key);
        return conf == null ? defaultValue : (T) conf;
    }

    default boolean isSetC(K... key) {
        return configC(key) != null;
    }

    default void logC(@NotNull PrintStream stream) {
        stream.println();
        var startMessage = "========= Config Map [" + Thread.currentThread() + " | " + getClass().getName() + "] =========";
        stream.println(startMessage);
        config().forEach((k, v) -> stream.println("KEY: " + k + ", VALUE: " + v));
        System.out.println("=".repeat(startMessage.length()));
    }

    default void logC() {
        logC(System.out);
    }

    default StringMapper asStringMapperC(K... key) {
        return (StringMapper) configC(key);
    }

    default <T> T configC(T value, K @NotNull ... address) {
        final var len = address.length;
        if (len == 0)
            return null;
        if (len == 1)
            return (T) config(address[0], (V) value);
        var node = safety(this, address[0]);
        for (int i = 1; i < len - 1; i++)
            node = safety(node, address[i]);
        return (T) node.config(address[len - 1], (V) value);
    }

    @Contract("_, !null -> param2")
    private static <K, V> @NotNull ConfigBase<K, V> safety(@NotNull ConfigBase<K, V> parent, K key) {
        var v = parent.asConfigBaseC(key);
        if (v != null)
            return v;
        v = getInstanceLike(parent);
        parent.config(key, (V) v);
        return v;
    }

    default <T> T configC(@NotNull Class<T> resultType, K @NotNull ... address) {
        final var len = address.length;
        if (len == 0)
            return null;
        if (len == 1)
            return resultType.cast(config(address[0]));
        var node = (ConfigBase<K, V>) config(address[0]);
        for (int i = 1; i < len - 1; i++)
            node = (ConfigBase<K, V>) node.config(address[i]);
        return resultType.cast(node.config(address[len - 1]));
    }

    default V configC(K @NotNull ... address) {
        final var len = address.length;
        if (len == 0)
            return null;
        if (len == 1)
            return config(address[0]);
        var node = (ConfigBase<K, V>) config(address[0]);
        for (int i = 1; i < len - 1; i++)
            node = (ConfigBase<K, V>) node.config(address[i]);
        return node.config(address[len - 1]);
    }

    default Runnable asRunnableC(K... key) {
        return (Runnable) configC(key);
    }

    default <T> List<T> asListC(@NotNull Class<T> classOfValues, K... key) {
        return (List<T>) configC(key);
    }

    default <_K, _V> Map<_K, _V> asMapC(Class<_K> classOfKeys, Class<_V> classOfValues, K... key) {
        return (Map<_K, _V>) configC(key);
    }

    default <T> Collection<T> asCollectionC(Class<T> classOfValues, K... key) {
        return (Collection<T>) configC(key);
    }

    default <_K, _V> ConfigBase<_K, _V> asConfigBaseC(Class<_K> classOfKeys, Class<_V> classOfValues, K... key) {
        return (ConfigBase<_K, _V>) configC(key);
    }

    default ConfigBase<K, V> asConfigBaseC(K... key) {
        return (ConfigBase<K, V>) configC(key);
    }

    @Contract(pure = true)
    static <K, V> @NotNull ConfigBase<K, V> getInstance(Class<K> kClass, Class<V> vClass) {
        final var map = new HashMap<K, V>();
        return () -> map;
    }

    static <K, V> @NotNull ConfigBase<K, V> getInstance(Map<K, V> fromMap) {
        return () -> fromMap;
    }

    static <K, V> ConfigBase<K, V> getInstanceLike(ConfigBase<K, V> scheme) {
        final var map = new HashMap<K, V>();
        return () -> map;
    }
}
