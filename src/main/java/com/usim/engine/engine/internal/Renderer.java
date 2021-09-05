package com.usim.engine.engine.internal;

import com.usim.engine.engine.entity.Entity;
import com.usim.engine.engine.util.Utils;
import com.usim.engine.engine.graph.Shader;
import com.usim.engine.engine.graph.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static com.usim.engine.engine.Constants.*;

public class Renderer {
    private final Transformation transformation;
    private Shader shader;
    private final Map<String, Shader> shaders;
    private final Window window;

    public Renderer() {
        transformation = new Transformation();
        window = Engine.window();
        shaders = new HashMap<>();
    }

    public void init() throws Exception {
        // Create shader
        shader = new Shader();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shader.link();
        
        // Create uniforms for world and projection matrices and texture
        shader.createUniform("projectionMatrix");
        shader.createUniform("worldMatrix");
        shader.createUniform("texture_sampler");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Entity[] entities) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shader.bind();
        
        // Update projection Matrix
        shader.setUniform("projectionMatrix",
                transformation.getProjectionMatrix(DEFAULT_FIELD_OF_VIEW, window.getWidth(), window.getHeight(), DEFAULT_Z_NEAR,
                        DEFAULT_Z_FAR));
        
        shader.setUniform("texture_sampler", 0);
        // Render each gameItem
        for (var entity : entities) {
            // Set world matrix for this item
            shader.setUniform("worldMatrix", transformation.getWorldMatrix(
                    entity.getPosition(),
                    entity.getRotation(),
                    entity.getScale()));
            // Render the mes for this game item
            entity.getMesh().render();
        }

        shader.unbind();
    }

    public void importShader(String name, String vertexShaderCode, String fragmentShaderCode, @NotNull List<String> uniforms) {
        shaders.put(name, new Shader() {{
            createVertexShader(vertexShaderCode);
            createVertexShader(fragmentShaderCode);
            link();
            uniforms.forEach(this::createUniform);
        }});
    }

    public void cleanup() {
        if (shader != null) {
            shader.cleanup();
        }
    }
}
