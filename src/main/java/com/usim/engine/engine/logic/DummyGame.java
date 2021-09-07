package com.usim.engine.engine.logic;

import com.usim.engine.engine.entity.Entity;
import com.usim.engine.engine.internal.*;
import com.usim.engine.engine.graph.Mesh;
import com.usim.engine.engine.graph.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static com.usim.engine.engine.Constants.DEFAULT_CAMERA_MOVEMENT_SENSITIVITY;
import static com.usim.engine.engine.Constants.DEFAULT_MOUSE_MOVEMENT_SENSITIVITY;
import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements Logic {

    private final Renderer renderer;
    private Entity[] entities;
    private final Window window;
    private final Camera camera;
    private final Vector3f cameraInc;
    private final Input input;


    public DummyGame() {
        renderer = new Renderer();
        window = Engine.window();
        camera = new Camera();
        cameraInc = new Vector3f();
        input = Engine.input();
    }

    @Override
    public void init() {
        renderer.init();
        float[] positions = new float[] { -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, };
        float[] textCoords = new float[] { 0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
                0.0f, 0.5f, 0.5f, 1.0f, 0.5f, };
        int[] indices = new int[] { 0, 1, 3, 3, 1, 2, 8, 10, 11, 9, 8, 11, 12, 13, 7, 5, 12, 7, 14, 15, 6, 4, 14, 6, 16, 18, 19, 17,
                16, 19, 4, 6, 7, 5, 4, 7, };
        Texture texture = new Texture("textures/grassblock.png");
        Mesh mesh = new Mesh(positions, textCoords, indices, texture);
        var gameItem1 = new Entity(mesh);
        gameItem1.setScale(0.5f);
        gameItem1.setPosition(0, 0, -2);
        var gameItem2 = new Entity(mesh);
        gameItem2.setScale(0.5f);
        gameItem2.setPosition(0.5f, 0.5f, -2);
        var gameItem3 = new Entity(mesh);
        gameItem3.setScale(0.5f);
        gameItem3.setPosition(0, 0, -2.5f);
        var gameItem4 = new Entity(mesh);
        gameItem4.setScale(0.5f);
        gameItem4.setPosition(0.5f, 0, -2.5f);
        entities = new Entity[] { gameItem1, gameItem2, gameItem3, gameItem4 };
    }

    @Override
    public void input() {
        cameraInc.set(
                input.isKeyPressed(GLFW_KEY_A) ? 1 : input.isKeyPressed(GLFW_KEY_D) ? -1 : 0,
                input.isKeyPressed(GLFW_KEY_Z) ? 1 : input.isKeyPressed(GLFW_KEY_X) ? -1 : 0,
                input.isKeyPressed(GLFW_KEY_W) ? 1 : input.isKeyPressed(GLFW_KEY_S) ? -1 : 0
        );
    }

    @Override
    public void update() {
        camera.move(cameraInc.x * DEFAULT_CAMERA_MOVEMENT_SENSITIVITY, cameraInc.y * DEFAULT_CAMERA_MOVEMENT_SENSITIVITY,
                cameraInc.z * DEFAULT_CAMERA_MOVEMENT_SENSITIVITY);
        if (input.isMouseLeftButtonPressed()) {
            Vector2f rotVec = input.getDisplayVector();
            camera.rotate(rotVec.x * DEFAULT_MOUSE_MOVEMENT_SENSITIVITY, rotVec.y * DEFAULT_MOUSE_MOVEMENT_SENSITIVITY, 0);
        }
    }

    @Override
    public void render() {
        renderer.render(entities, camera);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (var entity : entities)
            entity.getMesh().cleanUp();
    }

    @Override
    public Camera camera() {
        return camera;
    }
}
