package ahd.usim.engine.logic;

import ahd.usim.engine.Constants;
import ahd.usim.engine.entity.Entity;
import ahd.usim.engine.entity.loader.ModelLoader;
import ahd.usim.engine.graph.Mesh;
import ahd.usim.engine.graph.Texture;
import ahd.usim.engine.internal.Camera;
import ahd.usim.engine.internal.Engine;
import ahd.usim.engine.internal.Input;
import ahd.usim.engine.internal.Renderer;
import ahd.usim.engine.util.Sampling;
import ahd.usim.ulib.jmath.datatypes.functions.Surface;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;

import static java.lang.Math.*;
import static org.lwjgl.glfw.GLFW.*;

public class SampleLogic implements Logic {

    private final Renderer renderer;
    private Entity[] entities;
    private final Camera camera;
    private final Vector3f cameraInc;
    private final Input input;


    public SampleLogic() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f();
        input = Engine.getEngine().getInput();
    }

    @Override
    public void init() {
        renderer.init();
        //Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        Mesh mesh = null;
        try {
            mesh = ModelLoader.loadMesh("E:\\Programming Projects\\Java Projects\\usim\\src\\main\\resources\\models\\bunny.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Texture texture = new Texture("textures/grassblock.png");
        assert mesh != null;
        mesh.setTexture(texture);
        var gameItem = new Entity(mesh);
        gameItem.setScale(0.5f);
        gameItem.setPosition(0, 0, -2);
        gameItem.getMesh().setColor(new Vector3f(1, 0, 1));

        var sample = Sampling.sample(-PI / 2, PI / 2, -PI, PI, 0.01, 0.1, Surface.kleinBottle());

                entities = new Entity[] { new Entity(new Mesh(sample.vertices(), sample.colors(), sample.indices())) };
//        entities = new Entity[] {};
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
        camera.move(cameraInc.x * Constants.DEFAULT_CAMERA_MOVEMENT_SENSITIVITY, cameraInc.y * Constants.DEFAULT_CAMERA_MOVEMENT_SENSITIVITY,
                cameraInc.z * Constants.DEFAULT_CAMERA_MOVEMENT_SENSITIVITY);
        if (input.isMouseLeftButtonPressed()) {
            Vector2f rotVec = input.getDisplayVector();
            camera.rotate(rotVec.x * Constants.DEFAULT_MOUSE_MOVEMENT_SENSITIVITY, rotVec.y * Constants.DEFAULT_MOUSE_MOVEMENT_SENSITIVITY, 0);
        }
        for (Entity entity : entities) {
            entity.getRotation().add(0.001f, 0.001f, 0.001f);
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
            entity.getMesh().cleanup();
    }

    @Override
    public void saveState() {

    }

    @Override
    public Camera camera() {
        return camera;
    }
}
