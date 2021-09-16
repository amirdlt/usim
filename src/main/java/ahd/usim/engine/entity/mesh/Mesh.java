package ahd.usim.engine.entity.mesh;

import ahd.usim.engine.entity.material.Material;
import ahd.usim.engine.entity.material.Texture;
import ahd.usim.engine.internal.Cleanable;
import ahd.usim.engine.internal.Mutable;
import ahd.usim.engine.internal.Visible;
import ahd.usim.ulib.utils.annotation.ChangeReference;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public interface Mesh extends Visible, Cleanable, Mutable {
    int VERTICES_MASK = 0x1;
    int COLORS_MASK = 0x2;
    int NORMALS_MASK = 0x4;
    int TEXTURE_COORDINATES_MASK = 0x8;
    int INDICES_MASK = 0x10;

    int vertexCount();

    int vaoId();

    Map<Integer, Integer> vboIds();

    int getDrawingMode();

    Material getMaterial();

    void setMaterial(Material material);

    void setDrawingMode(@MagicConstant(intValues = { GL_POINTS, GL_LINES, GL_LINE_LOOP, GL_LINE_STRIP, GL_TRIANGLES, GL_TRIANGLE_STRIP,
            GL_TRIANGLE_FAN, GL_QUADS, GL_QUAD_STRIP, GL_POLYGON }) int mode);

    Vector3f getColor();

    @ChangeReference
    void setColor(Vector3f color);

    @Override
    default void render() {
        update();

        var material = getMaterial();
        if (material != null)
            material.activateTexture();

        // Draw the mesh
        glBindVertexArray(vaoId());

        glDrawElements(getDrawingMode(), vertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glBindVertexArray(0);
    }

    @Override
    default void cleanup() {
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        vboIds().values().forEach(GL15::glDeleteBuffers);

        var material = getMaterial();
        if (material != null)
            material.cleanup();

        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId());
    }

    default void registerVbo(float @Nullable [] data,
            int glslLayoutLocation, int perVertexSize, boolean normalized, int mask, int usage) {
        if (data == null)
            return;
        glBindVertexArray(vaoId());
        FloatBuffer buffer = null;
        try {
            var id = glGenBuffers();
            vboIds().put(mask, id);
            buffer = MemoryUtil.memAllocFloat(data.length).put(data).flip();
            glBindBuffer(GL_ARRAY_BUFFER, id);
            glBufferData(GL_ARRAY_BUFFER, buffer, usage);
            glEnableVertexAttribArray(glslLayoutLocation);
            glVertexAttribPointer(glslLayoutLocation, perVertexSize, GL_FLOAT, normalized, 0, 0);
        } finally {
            MemoryUtil.memFree(buffer);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    default void indexVbo(int @Nullable [] indices, int usage) {
        if (indices == null)
            return;
        glBindVertexArray(vaoId());
        IntBuffer buffer = null;
        try {
            var id = glGenBuffers();
            vboIds().put(INDICES_MASK, id);
            buffer = MemoryUtil.memAllocInt(indices.length).put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, usage);
        } finally {
            MemoryUtil.memFree(buffer);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    default ImmutableMesh immutableMesh() {
        return (ImmutableMesh) this;
    }

    default MutableMesh mutableMesh() {
        return (MutableMesh) this;
    }
}
