package com.usim.engine.engine.logic;

import com.usim.engine.engine.entity.Entity;
import com.usim.engine.engine.entity.loader.ModelLoader;
import com.usim.engine.engine.internal.*;
import com.usim.engine.engine.graph.Mesh;
import com.usim.engine.engine.graph.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

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
        //Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        Mesh mesh = null;
        try {
            mesh = ModelLoader.loadMesh("E:\\Programming Projects\\Java Projects\\usim\\src\\main\\resources\\models\\cube.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Texture texture = new Texture("textures/grassblock.png");
        assert mesh != null;
        mesh.setTexture(texture);
        var gameItem = new Entity(mesh);
        gameItem.setScale(0.5f);
        gameItem.setPosition(0, 0, -2);
        var es = new ArrayList<Entity>();
        int count = 0;
        while (count++ < 1000)
            es.add(new Entity(mesh) {{
                setScale((float) Math.random());
                setPosition((float) Math.random() * 30, (float) Math.random() * 30, -(float) Math.random() * 30);
            }});
        entities = es.toArray(Entity[]::new);
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
        for (Entity entity : entities) {
            entity.getRotation().add(0.01f, 0.01f, 0.01f);
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
