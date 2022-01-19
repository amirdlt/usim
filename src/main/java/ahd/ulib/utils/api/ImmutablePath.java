package ahd.ulib.utils.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ImmutablePath<K> {
    private final K[] path;

    @SafeVarargs
    private ImmutablePath(K... path) {
        this.path = path;
    }

    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public static <K> @NotNull ImmutablePath<K> of(K... path) {
        return new ImmutablePath<>(path);
    }
}
