package ahd.usim.engine.util;

import ahd.ulib.jmath.datatypes.functions.Arc3D;
import ahd.ulib.jmath.datatypes.functions.Mapper3D;
import ahd.ulib.jmath.datatypes.functions.Surface;
import ahd.ulib.jmath.datatypes.tuples.Point3D;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class Sampling {
    private static final double DOUBLE_ACCURACY_TOLERANCE = 1e-9;
    public record SurfaceSample(float @NotNull [] vertices, float @Nullable [] colors, float @Nullable [] normals, int @NotNull [] indices) {}
    public record ArcSample(float @NotNull [] vertices, float @Nullable [] colors, int @Nullable [] indices) {}

    @Contract("_, _, _, _, _, _, _, _ -> new")
    @SuppressWarnings("DuplicatedCode")
    public static @NotNull SurfaceSample sample(double xL, double xU, double yL, double yU, double deltaX, double deltaY,
            Mapper3D colorFromPosition, @NotNull Surface surface) {
        var nx = (int) Math.ceil((xU - xL) / deltaX + 1);
        var ny = (int) Math.ceil((yU - yL) / deltaY + 1);
        var nz = nx * ny;
        var zz = new float[nz * 3];
        var indices = new int[nz * 6];
        var normals = new float[zz.length];
        var colors = new float[zz.length];
        var x = xL - deltaX;
        var normal = surface.unitNormal(deltaX, deltaY);
        var index = 0;
        while ((x += deltaX) < xU && xU - x > DOUBLE_ACCURACY_TOLERANCE) {
            var y = yL - deltaY;
            while ((y += deltaY) < yU && yU - y > DOUBLE_ACCURACY_TOLERANCE) {
                var p = surface.valueAt(x, y);
                var n = normal.valueAt(x, y);
                normals[index] = (float) n.x;
                zz[index++] = (float) p.x;
                normals[index] = (float) n.y;
                zz[index++] = (float) p.y;
                normals[index] = (float) n.z;
                zz[index++] = (float) p.z;
            }
            var p = surface.valueAt(x, yU);
            var n = normal.valueAt(x, yU);
            normals[index] = (float) n.x;
            zz[index++] = (float) p.x;
            normals[index] = (float) n.y;
            zz[index++] = (float) p.y;
            normals[index] = (float) n.z;
            zz[index++] = (float) p.z;
        }
        var y = yL - deltaY;
        while ((y += deltaY) < yU && yU - y > DOUBLE_ACCURACY_TOLERANCE) {
            var p = surface.valueAt(xU, y);
            var n = normal.valueAt(xU, y);
            normals[index] = (float) n.x;
            zz[index++] = (float) p.x;
            normals[index] = (float) n.y;
            zz[index++] = (float) p.y;
            normals[index] = (float) n.z;
            zz[index++] = (float) p.z;
        }
        var p = surface.valueAt(xU, yU);
        var n = normal.valueAt(xU, yU);
        normals[index] = (float) n.x;
        zz[index++] = (float) p.x;
        normals[index] = (float) n.y;
        zz[index++] = (float) p.y;
        normals[index] = (float) n.z;
        zz[index] = (float) p.z;
        index = 0;
        for (int i = 0; i < nz; i++)
            if (i + ny + 1 < nz && i % ny < ny - 1 && i / ny < nx - 1) {
                indices[index++] = i;
                indices[index++] = i + 1;
                indices[index++] = i + ny;
                indices[index++] = i + 1;
                indices[index++] = i + ny + 1;
                indices[index++] = i + ny;
            }
        for (int i = 0; i < zz.length; ) {
            var c = colorFromPosition.valueAt(zz[i], zz[i + 1], zz[i + 2]);
            colors[i++] = (float) c.x;
            colors[i++] = (float) c.y;
            colors[i++] = (float) c.z;
        }
        return new SurfaceSample(zz, colors, normals, indices);
    }

    @Contract("_, _, _, _, _, _, _ -> new")
    public static @NotNull SurfaceSample sample(double xL, double xU, double yL, double yU, double deltaX, double deltaY, Surface surface) {
        return sample(xL, xU, yL, yU, deltaX, deltaY, p -> Point3D.of(p.x * p.x, p.y * p.y, p.z * p.z).normalize(), surface);
    }

    public static @NotNull ArcSample sample(double xL, double xU, double deltaX, @NotNull Arc3D arc) {
        return sample(xL, xU, deltaX, p -> p.normalize().addVector(0.3, 0.3, 0.3), arc);
    }

    public static @NotNull ArcSample sample(double xL, double xU, double deltaX, @NotNull Mapper3D colorFromPosition, @NotNull Arc3D arc) {
        var nx = (int) Math.ceil((xU - xL) / deltaX + 1);
        var vertices = new float[nx * 3];
        int index = 0;
        var x = xL - deltaX;
        while ((x += deltaX) < xU && xU - x > DOUBLE_ACCURACY_TOLERANCE) {
            var p = arc.valueAt(x);
            vertices[index++] = (float) p.x;
            vertices[index++] = (float) p.y;
            vertices[index++] = (float) p.z;
        }
        var p = arc.valueAt(xU);
        vertices[index++] = (float) p.x;
        vertices[index++] = (float) p.y;
        vertices[index] = (float) p.z;
        var colors = new float[vertices.length];
        //noinspection DuplicatedCode
        for (int i = 0; i < colors.length; ) {
            var c = colorFromPosition.valueAt(vertices[i], vertices[i + 1], vertices[i + 2]);
            colors[i++] = (float) c.x;
            colors[i++] = (float) c.y;
            colors[i++] = (float) c.z;
        }
        var indices = new int[nx];
        Arrays.setAll(indices, i -> i);
        return new ArcSample(vertices, colors, indices);
    }
}
