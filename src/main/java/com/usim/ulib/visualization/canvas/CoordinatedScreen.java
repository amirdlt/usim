package com.usim.ulib.visualization.canvas;

import com.usim.ulib.jmath.datatypes.tuples.AbstractPoint;
import com.usim.ulib.jmath.datatypes.tuples.Point2D;
import com.usim.ulib.jmath.datatypes.tuples.Point3D;

import java.awt.*;

@SuppressWarnings("ALL")
public interface CoordinatedScreen {
    int screenX(double coordinateX);
    int screenY(double coordinateY);

    double coordinateX(int screenX);
    double coordinateY(int screenY);

    Camera camera();

    default int screenXLen(double coordinateXLen) {
        return screenX(coordinateXLen) - screenX(0);
    }

    default int screenYLen(double coordinateYLen) {
        return -(screenY(coordinateYLen) - screenY(0));
    }

    default double coordinateXLen(int screenXLen) {
        return coordinateX(screenXLen) - coordinateX(0);
    }

    default double coordinateYLen(int screenYLen) {
        return -(coordinateY(screenYLen) - coordinateY(0));
    }

    default Point screen(Point2D p) {return new Point();}

    default Point screen(Point3D p) {return new Point();}

    default Point screen(AbstractPoint point) {
        if (point instanceof Point3D p)
            return screen(p);
        if (point instanceof Point2D p)
            return screen(p);
        throw new RuntimeException("AHD:: Only Point2D and Point3D is Considered");
    }

    default double scaleX() {
        return 1 / coordinateXLen(1);
    }

    default double scaleY() {
        return 1 / coordinateYLen(1);
    }

    static CoordinatedScreen default2D() {
        return new Graph2DCanvas();
    }

    static CoordinatedScreen default3D() {
        return new Graph3DCanvas();
    }
}
