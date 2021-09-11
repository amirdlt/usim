package com.usim.engine.util;

import com.usim.ulib.jmath.datatypes.functions.Mapper3D;
import com.usim.ulib.jmath.datatypes.functions.Surface;
import com.usim.ulib.jmath.datatypes.tuples.Point3D;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Sampling {
    public record Sample(float[] vertices, float[] colors, float @Nullable [] normals, int[] indices) {}

    @Contract("_, _, _, _, _, _, _, _ -> new")
    @SuppressWarnings("DuplicatedCode")
    public static @NotNull Sample sample(double xL, double xU, double yL, double yU, double deltaX, double deltaY, Mapper3D colorFromPosition, Surface surface) {
        var nx = (int) Math.ceil((xU - xL) / deltaX + 1);
        var ny = (int) Math.ceil((yU - yL) / deltaY + 1);
        var nz = nx * ny;
        var zz = new float[nz * 3];
        var x = xL - deltaX;
        var index = 0;
        while ((x += deltaX) < xU) {
            var y = yL - deltaY;
            while ((y += deltaY) < yU) {
                var p = surface.valueAt(x, y);
                zz[index++] = (float) p.x;
                zz[index++] = (float) p.y;
                zz[index++] = (float) p.z;
            }
            var p = surface.valueAt(x, yU);
            zz[index++] = (float) p.x;
            zz[index++] = (float) p.y;
            zz[index++] = (float) p.z;
        }
        var y = yL - deltaY;
        while ((y += deltaY) < yU) {
            var p = surface.valueAt(xU, y);
            zz[index++] = (float) p.x;
            zz[index++] = (float) p.y;
            zz[index++] = (float) p.z;
        }
        var p = surface.valueAt(xU, yU);
        zz[index++] = (float) p.x;
        zz[index++] = (float) p.y;
        zz[index] = (float) p.z;
        index = 0;
        var indices = new int[nz * 6];
        for (int i = 0; i < nz; i++)
            if (i + ny + 1 < nz && i % ny < ny - 1 && i / ny < nx - 1) {
                indices[index++] = i;
                indices[index++] = i + 1;
                indices[index++] = i + ny;
                indices[index++] = i + 1;
                indices[index++] = i + ny + 1;
                indices[index++] = i + ny;
            }
        float[] colors = new float[zz.length];
        for (int i = 0; i < zz.length; ) {
            var c = colorFromPosition.valueAt(zz[i], zz[i + 1], zz[i + 2]);
            colors[i++] = (float) c.x;
            colors[i++] = (float) c.y;
            colors[i++] = (float) c.z;
        }
        return new Sample(zz, colors, null, indices);
    }

    @Contract("_, _, _, _, _, _, _ -> new")
    public static @NotNull Sample sample(double xL, double xU, double yL, double yU, double deltaX, double deltaY, Surface surface) {
        return sample(xL, xU, yL, yU, deltaX, deltaY, p -> Point3D.of(p.x * p.x, p.y * p.y, p.z * p.z).normalize(), surface);
    }
}
