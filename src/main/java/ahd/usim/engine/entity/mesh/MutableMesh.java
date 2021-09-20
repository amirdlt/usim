package ahd.usim.engine.entity.mesh;

import ahd.usim.ulib.utils.annotation.ChangeReference;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_LINES;
import static org.lwjgl.opengl.GL11C.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11C.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11C.GL_POINTS;
import static org.lwjgl.opengl.GL11C.GL_QUADS;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL45C.*;

public class MutableMesh extends AbstractMesh {
    private int change;

    private float[] colors;
    private float[] vertices;
    private float[] normals;

    public MutableMesh(float @NotNull [] vertices, float @Nullable [] colors, float @Nullable [] textureCoordinates,
            float @Nullable [] normals, int @NotNull [] indices,
            @MagicConstant(intValues = { GL_POINTS, GL_LINES, GL_LINE_LOOP, GL_LINE_STRIP, GL_TRIANGLES, GL_TRIANGLE_STRIP,
                    GL_TRIANGLE_FAN, GL_QUADS, GL_QUAD_STRIP, GL_POLYGON }) int drawingMode) {
        super(indices.length);

        change = -1;

        this.drawingMode = drawingMode;

        if (colors == null) {
            colors = new float[vertices.length];
            Arrays.fill(colors, 1);
        }

        this.colors = colors;
        this.vertices = vertices;
        this.normals = normals;

        registerVbo(vertices, 0, 3, false, VERTICES_MASK, GL_DYNAMIC_DRAW);
        registerVbo(textureCoordinates, 1, 2, false, TEXTURE_COORDINATES_MASK, GL_DYNAMIC_DRAW);
        registerVbo(normals, 2, 3, false, NORMALS_MASK, GL_DYNAMIC_DRAW);
        registerVbo(colors, 3, 3, false, COLORS_MASK, GL_DYNAMIC_DRAW);

        indexVbo(indices, GL_DYNAMIC_DRAW);
    }

    public MutableMesh(float @NotNull [] vertices, float @Nullable [] colors, float @Nullable [] normals, int @NotNull [] indices) {
        this(vertices, null, colors, normals, indices);
    }

    public MutableMesh(float @NotNull [] vertices, float @Nullable [] textureCoordinates, float @Nullable [] colors, float @Nullable [] normals,
            int @NotNull [] indices) {
        this(vertices, colors, textureCoordinates, normals, indices, GL_TRIANGLES);
    }

    public MutableMesh(float @NotNull [] vertices, int @NotNull [] indices,
        @MagicConstant(intValues = { GL_POINTS, GL_LINES, GL_LINE_LOOP, GL_LINE_STRIP, GL_TRIANGLES, GL_TRIANGLE_STRIP,
                GL_TRIANGLE_FAN, GL_QUADS, GL_QUAD_STRIP, GL_POLYGON }) int drawingMode) {
        this(vertices, null, indices, drawingMode);
    }

    public MutableMesh(float @NotNull [] vertices, float @Nullable [] colors, int @NotNull [] indices) {
        this(vertices, colors, indices, GL_TRIANGLES);
    }

    public MutableMesh(float @NotNull [] vertices, float @Nullable [] colors, int @NotNull [] indices,
            @MagicConstant(intValues = { GL_POINTS, GL_LINES, GL_LINE_LOOP, GL_LINE_STRIP, GL_TRIANGLES, GL_TRIANGLE_STRIP,
                    GL_TRIANGLE_FAN, GL_QUADS, GL_QUAD_STRIP, GL_POLYGON }) int drawingMode) {
        this(vertices, colors, null, null, indices, drawingMode);
    }

    public float[] getColors() {
        return colors;
    }

    @ChangeReference
    public void setColors(float[] colors) {
        this.colors = colors;
        change = Math.max(0, change) | COLORS_MASK;
    }

    public float[] getVertices() {
        return vertices;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
        change = Math.max(0, change) | VERTICES_MASK;
    }

    public float[] getNormals() {
        return normals;
    }

    public void setNormals(float[] normals) {
        this.normals = normals;
        change = Math.max(0, change) | NORMALS_MASK;
    }

    @Override
    public void update() {
        if (change < 0)
            return;
        if ((change & VERTICES_MASK) == VERTICES_MASK) {
            glBindBuffer(GL_ARRAY_BUFFER, vboIds.get(VERTICES_MASK));
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        if ((change & COLORS_MASK) == COLORS_MASK) {
            glBindBuffer(GL_ARRAY_BUFFER, vboIds.get(COLORS_MASK));
            glBufferSubData(GL_ARRAY_BUFFER, 0, colors);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        if ((change & NORMALS_MASK) == NORMALS_MASK) {
            glBindBuffer(GL_ARRAY_BUFFER, vboIds.get(NORMALS_MASK));
            glBufferSubData(GL_ARRAY_BUFFER, 0, normals);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        change = -1;
    }

    public void update(@MagicConstant(intValues = { VERTICES_MASK, COLORS_MASK, NORMALS_MASK }) int mask) {
        change = mask;
    }

    public void updateVertices() {
        update(VERTICES_MASK);
    }

    public void updateColors() {
        update(COLORS_MASK);
    }
}
