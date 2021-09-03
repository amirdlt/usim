package com.usim.ulib.jmath.datatypes.functions;

import java.awt.*;

public interface IntMapper2D extends IntMapper {
    Point map(int x, int y);

    @Override
    default int[] map(int... dims) {
        var p = map(dims[0], dims[1]);
        return new int[] { p.x, p.y };
    }
}
