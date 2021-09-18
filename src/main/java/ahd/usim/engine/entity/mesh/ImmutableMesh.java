package ahd.usim.engine.entity.mesh;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL15;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_POLYGON;

public class ImmutableMesh extends AbstractMesh {
    public ImmutableMesh(float @NotNull [] vertices, float @Nullable [] colors, int @NotNull [] indices) {
        this(vertices, colors, null, null, indices, GL_TRIANGLES);
    }

    public ImmutableMesh(float @NotNull [] vertices, float @Nullable [] colors, float @Nullable [] normals, int @NotNull [] indices) {
        this(vertices, colors, null, normals, indices, GL_TRIANGLES);
    }

    public ImmutableMesh(float @NotNull [] vertices, float @Nullable [] colors,
            float @Nullable [] textureCoordinates, float @Nullable [] normals, int @NotNull[] indices, @MagicConstant(intValues = { GL_POINTS, GL_LINES, GL_LINE_LOOP, GL_LINE_STRIP, GL_TRIANGLES, GL_TRIANGLE_STRIP,
                    GL_TRIANGLE_FAN, GL_QUADS, GL_QUAD_STRIP, GL_POLYGON }) int drawingMode) {
        super(indices.length);

        this.drawingMode = drawingMode;

        if (colors == null) {
            colors = new float[vertices.length];
            Arrays.fill(colors, 1);
        }

        registerVbo(vertices, VERTICES_INDEX, 3, false, VERTICES_MASK, GL15.GL_STATIC_DRAW);
        registerVbo(textureCoordinates, TEXTURE_COORDINATES_INDEX, 2, false, TEXTURE_COORDINATES_MASK, GL15.GL_STATIC_DRAW);
        registerVbo(normals, VERTEX_NORMALS_INDEX, 3, false, NORMALS_MASK, GL15.GL_STATIC_DRAW);
        registerVbo(colors, COLORS_INDEX, 3, false, COLORS_MASK, GL15.GL_STATIC_DRAW);

        indexVbo(indices, GL15.GL_STATIC_DRAW);
    }

    @Override
    public final void update() {

    }
}
