package ahd.usim.engine.entity.mesh;

import ahd.usim.engine.entity.material.Material;
import ahd.usim.engine.internal.api.Cleanable;
import ahd.usim.engine.internal.api.Updatable;
import ahd.usim.engine.internal.api.Visible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL45C;
import org.lwjgl.opengl.GL45C.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

public abstract class AbstractMesh implements Updatable, Visible, Cleanable {

    public static final int
        VERTICES_MASK = 0x1,
        COLORS_MASK = 0x2,
        NORMALS_MASK = 0x4,
        TEXTURE_COORDINATES_MASK = 0x8,
        INDICES_MASK = 0x10;

    protected static final int
        DEFAULT_DRAWING_MODE = GL_TRIANGLES,
        VERTICES_INDEX = 0,
        TEXTURE_COORDINATES_INDEX = 1,
        VERTEX_NORMALS_INDEX = 2,
        COLORS_INDEX = 3;


    protected final Map<Integer, Integer> vboIds;
    protected final int vertexCount;
    protected final int vaoId;
    protected Material material;
    protected int drawingMode;

    private boolean isCleaned;
    private int meshUseCount;
    private final Object mutex = new Object();

    protected AbstractMesh(int vertexCount) {
        vboIds = new HashMap<>();
        drawingMode = DEFAULT_DRAWING_MODE;
        vaoId = glGenVertexArrays();
        this.vertexCount = vertexCount;
        material = null;
        isCleaned = false;
        meshUseCount = 0;
    }

    protected void registerVbo(float @Nullable [] data,
            int glslLayoutLocation, int perVertexSize, boolean normalized, int mask, int usage) {
        if (data == null)
            return;
        glBindVertexArray(vaoId);
        FloatBuffer buffer = null;
        try {
            var id = glGenBuffers();
            vboIds.put(mask, id);
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

    protected void indexVbo(int @Nullable [] indices, int usage) {
        if (indices == null)
            return;
        glBindVertexArray(vaoId);
        IntBuffer buffer = null;
        try {
            var id = glGenBuffers();
            vboIds.put(INDICES_MASK, id);
            buffer = MemoryUtil.memAllocInt(indices.length).put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, usage);
        } finally {
            MemoryUtil.memFree(buffer);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getDrawingMode() {
        return drawingMode;
    }

    public void setDrawingMode(int drawingMode) {
        this.drawingMode = drawingMode;
    }

    public void attach() {
        synchronized (mutex) {
            meshUseCount++;
        }
    }

    public boolean isCleaned() {
        return isCleaned;
    }

    @Override
    public void render() {
        update();

        var material = getMaterial();
        if (material != null)
            material.activateTexture();

        glBindVertexArray(vaoId);

        glDrawElements(getDrawingMode(), vertexCount, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }

    @Override
    public final void cleanup() {
        synchronized (mutex) {
            if (--meshUseCount > 0)
                return;
            glDisableVertexAttribArray(0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            vboIds.values().forEach(GL15::glDeleteBuffers);

            var material = getMaterial();
            if (material != null)
                material.cleanup();

            glBindVertexArray(0);
            glDeleteVertexArrays(vaoId);

            isCleaned = true;
        }
    }
}
