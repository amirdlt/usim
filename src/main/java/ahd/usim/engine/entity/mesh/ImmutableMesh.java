package ahd.usim.engine.entity.mesh;

import ahd.usim.engine.Constants;
import ahd.usim.engine.entity.material.Material;
import ahd.usim.engine.entity.material.Texture;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL15;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class ImmutableMesh implements Mesh {
    private final int vaoId;
    private final int vertexCount;
    private int drawingMode;
    private Material material;
    private final Map<Integer, Integer> vboIds;
    private Vector3f color;

    public ImmutableMesh(float @NotNull [] vertices, float @Nullable [] colors, int @NotNull [] indices) {
        this(vertices, colors, null, null, indices, GL_TRIANGLES);
    }

    public ImmutableMesh(float @NotNull [] vertices, float @Nullable [] colors, float @Nullable [] normals, int @NotNull [] indices) {
        this(vertices, colors, null, normals, indices, GL_TRIANGLES);
    }

    public ImmutableMesh(float @NotNull [] vertices, float @Nullable [] colors,
            float @Nullable [] textureCoordinates, float @Nullable [] normals, int @NotNull[] indices, @MagicConstant(intValues = { GL_POINTS, GL_LINES, GL_LINE_LOOP, GL_LINE_STRIP, GL_TRIANGLES, GL_TRIANGLE_STRIP,
                    GL_TRIANGLE_FAN, GL_QUADS, GL_QUAD_STRIP, GL_POLYGON }) int drawingMode) {
        vboIds = new HashMap<>();
        this.drawingMode = drawingMode;

        vaoId = glGenVertexArrays();

        vertexCount = indices.length;
        color = Constants.DEFAULT_MESH_COLOR;
        if (colors == null) {
            colors = new float[vertices.length];
            Arrays.fill(colors, 1);
        }

        registerVbo(vertices, 0, 3, false, VERTICES_MASK, GL15.GL_STATIC_DRAW);
        registerVbo(textureCoordinates, 1, 2, false, TEXTURE_COORDINATES_MASK, GL15.GL_STATIC_DRAW);
        registerVbo(normals, 2, 3, false, NORMALS_MASK, GL15.GL_STATIC_DRAW);
        registerVbo(colors, 3, 3, false, COLORS_MASK, GL15.GL_STATIC_DRAW);

        indexVbo(indices, GL15.GL_STATIC_DRAW);
    }

    @Override
    public int vertexCount() {
        return vertexCount;
    }

    @Override
    public int vaoId() {
        return vaoId;
    }

    @Override
    public Map<Integer, Integer> vboIds() {
        return vboIds;
    }

    @Override
    public int getDrawingMode() {
        return drawingMode;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public void setDrawingMode(int mode) {
        drawingMode = mode;
    }

    @Override
    public Vector3f getColor() {
        return color;
    }

    @Override
    public void setColor(Vector3f color) {
        this.color = color;
    }

    @Override
    public final void update() {

    }
}
