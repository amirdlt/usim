package ahd.usim.ulib.jmath.datatypes.functions;

@FunctionalInterface
public interface IntMapper extends Function<int[], int[]> {
    int[] map(int... dims);

    @Override
    default int[] valueAt(int[] dims) {
        return map(dims);
    }
}
