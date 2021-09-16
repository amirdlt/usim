package ahd.usim.engine.internal;

import ahd.usim.engine.entity.Entity;
import ahd.usim.engine.internal.light.PointLight;
import ahd.usim.ulib.utils.Utils;
import org.jetbrains.annotations.TestOnly;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static ahd.usim.engine.Constants.*;

public class Renderer {
    private final Transformation transformation;
    private Shader shader;
    private final Window window;

    public Renderer() {
        transformation = new Transformation();
        window = Engine.getEngine().getWindow();
    }

    public void init() {
        shader = new Shader();
        shader.createVertexShader(Utils.getFileAsStringElseEmpty(VERTEX_SHADER_FILE_RESOURCE_PATH));
        shader.createFragmentShader(Utils.getFileAsStringElseEmpty(FRAGMENT_SHADER_FILE_RESOURCE_PATH));
        shader.link();

        shader.createUniform(PROJECTION_MATRIX_UNIFORM_NAME);
        shader.createUniform(MODEL_VIEW_MATRIX_UNIFORM_NAME);
        shader.createUniform(TEXTURE_SAMPLER_UNIFORM_NAME);

        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createUniform("ambientLight");
        shader.createPointLightUniform("pointLight");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Entity[] entities, Camera camera, Vector3f ambientLight, PointLight pointLight) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shader.bind();

        var projectionMatrix = transformation.getProjectionMatrix(DEFAULT_FIELD_OF_VIEW, window.getWidth(), window.getHeight(),
                DEFAULT_Z_NEAR, DEFAULT_Z_FAR);
        shader.setUniform("projectionMatrix", projectionMatrix);

        var viewMatrix = transformation.getViewMatrix(camera);

        // Update Light Uniforms
        shader.setUniform("ambientLight", ambientLight);
        shader.setUniform("specularPower", 10f);
        // Get a copy of the light object and transform its position to view coordinates
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos, 1);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        shader.setUniform("pointLight", currPointLight);

        shader.setUniform(TEXTURE_SAMPLER_UNIFORM_NAME, 0);
        for (var entity : entities) {
            shader.setUniform(MODEL_VIEW_MATRIX_UNIFORM_NAME, transformation.getModelViewMatrix(entity, viewMatrix));
            shader.setUniform("material", entity.getMesh().getMaterial());

            entity.render();
        }

        shader.unbind();

        drawCurve();
    }

    public void cleanup() {
        if (shader != null)
            shader.cleanup();
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    @TestOnly
    private void drawCurve() {
//        glEnableClientState(GL_VERTEX_ARRAY);
//        glVertexPointer(3, GL_FLOAT, 0, MemoryUtil.memAllocFloat(15).put(new float[] {
//                1, 1, 0,
//                1, 0, 0,
//                0, 0, 0,
//                0, 1, 0,
//                1, 0, 0
//        }).flip());
//        glDrawArrays(GL_LINE_STRIP, 0, 5);
//        glDisableClientState(GL_VERTEX_ARRAY);
    }
}
