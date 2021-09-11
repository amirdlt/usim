package com.usim.engine.internal;

import com.usim.engine.Constants;
import com.usim.engine.entity.Entity;
import com.usim.engine.util.Utils;
import com.usim.engine.graph.Shader;
import com.usim.engine.graph.Transformation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private final Transformation transformation;
    private Shader shader;
    private final Map<Shader, List<Entity>> shaders;
    private final Window window;

    public Renderer() {
        transformation = new Transformation();
        window = Engine.getEngine().getWindow();
        shaders = new HashMap<>();
    }

    public void init() {
        // Create shader
        shader = new Shader();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shader.link();

        // Create uniforms for modelView and projection matrices and texture
        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("texture_sampler");

        shader.createUniform("colour");
        shader.createUniform("useColour");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Entity[] entities, Camera camera) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shader.bind();

        var projectionMatrix = transformation.getProjectionMatrix(Constants.DEFAULT_FIELD_OF_VIEW, window.getWidth(), window.getHeight(),
                Constants.DEFAULT_Z_NEAR, Constants.DEFAULT_Z_FAR);
        shader.setUniform("projectionMatrix", projectionMatrix);

        var viewMatrix = transformation.getViewMatrix(camera);

        shader.setUniform("texture_sampler", 0);
        for (var entity : entities) {
            shader.setUniform("modelViewMatrix", transformation.getModelViewMatrix(entity, viewMatrix));
            shader.setUniform("colour", entity.getMesh().getColor());
            shader.setUniform("useColour", entity.getMesh().isTextured() ? 0 : 1);
            entity.render();
        }

        shader.unbind();
    }

    public void importShader(String vertexShaderCode, String fragmentShaderCode, @NotNull List<String> uniforms) {
        shaders.put(new Shader() {{
            createVertexShader(vertexShaderCode);
            createFragmentShader(fragmentShaderCode);
            link();
            uniforms.forEach(this::createUniform);
        }}, new ArrayList<>());
    }

    public void cleanup() {
        if (shader != null)
            shader.cleanup();
    }

    @FunctionalInterface
    public interface UniformSetter {
        Object valueOf(String uniformName);
    }
}
