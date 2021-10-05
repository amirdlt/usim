package ahd.usim.engine.internal.renderer;

import ahd.ulib.utils.Utils;
import ahd.usim.engine.entity.Entity;
import ahd.usim.engine.internal.Camera;
import ahd.usim.engine.internal.light.PointLight;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static ahd.usim.engine.Constants.*;

public class DefaultRenderer {
    private final Transformation transformation;
    private Shader shader;

    public DefaultRenderer() {
        transformation = new Transformation();
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
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void render(Entity @NotNull [] entities, Camera camera, Vector3f ambientLight, PointLight pointLight) {
        clear();

        shader.bind();

        var projectionMatrix = transformation.getProjectionMatrix();
        shader.setUniform("projectionMatrix", projectionMatrix);

        var viewMatrix = transformation.getViewMatrix(camera);

        // Update Light Uniforms
        shader.setUniform("ambientLight", ambientLight);
        shader.setUniform("specularPower", 1f);
        // Get a copy of the light object and transform its position to view coordinates
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos, 1).mul(viewMatrix);
        currPointLight.getPosition().set(aux.x, aux.y, aux.z);
        shader.setUniform("pointLight", currPointLight);

        shader.setUniform(TEXTURE_SAMPLER_UNIFORM_NAME, 0);
        for (var entity : entities) {
            shader.setUniform(MODEL_VIEW_MATRIX_UNIFORM_NAME, transformation.getModelViewMatrix(entity, viewMatrix));
            shader.setUniform("material", entity.getMesh().getMaterial());
            entity.render();
        }
        shader.unbind();
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
}
