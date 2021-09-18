package ahd.usim.engine.internal.api;

public interface Rebuild extends Initiative, Cleanable {
    default void rebuild() {
        cleanup();
        initialize();
    }
}
