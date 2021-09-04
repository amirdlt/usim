package com.usim.engine.game;

import com.usim.engine.engine.GameItem;
import com.usim.engine.engine.internal.Engine;
import com.usim.engine.engine.util.Utils;
import com.usim.engine.engine.internal.Window;
import com.usim.engine.engine.graph.ShaderProgram;
import com.usim.engine.engine.graph.Transformation;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL11.*;
import static com.usim.engine.engine.Constants.*;

public class Renderer {
    private final Transformation transformation;
    private ShaderProgram shaderProgram;
    private final Window window;

    public Renderer() {
        transformation = new Transformation();
        window = Engine.getWindow();
    }

    public void init() throws Exception {
        // Create shader
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shaderProgram.link();
        
        // Create uniforms for world and projection matrices and texture
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("worldMatrix");
        shaderProgram.createUniform("texture_sampler");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(GameItem[] gameItems) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();
        
        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(DEFAULT_FIELD_OF_VIEW, window.getWidth(), window.getHeight(), DEFAULT_Z_NEAR, DEFAULT_Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);
        
        shaderProgram.setUniform("texture_sampler", 0);
        // Render each gameItem
        for (GameItem gameItem : gameItems) {
            // Set world matrix for this item
            Matrix4f worldMatrix = transformation.getWorldMatrix(
                    gameItem.getPosition(),
                    gameItem.getRotation(),
                    gameItem.getScale());
            shaderProgram.setUniform("worldMatrix", worldMatrix);
            // Render the mes for this game item
            gameItem.getMesh().render();
        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}
