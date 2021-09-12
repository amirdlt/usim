package ahd.usim.ulib.jmath.datatypes.functions;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public interface IntMapper2D extends IntMapper {
    Point map(int x, int y);

    @Override
    default int[] map(int @NotNull ... dims) {
        var p = map(dims[0], dims[1]);
        return new int[] { p.x, p.y };
    }
}
