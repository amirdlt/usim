package ahd.usim.engine.logic;

import ahd.usim.engine.Constants;
import ahd.usim.engine.entity.Entity;
import ahd.usim.engine.entity.loader.ModelLoader;
import ahd.usim.engine.entity.material.Material;
import ahd.usim.engine.entity.material.Texture;
import ahd.usim.engine.entity.mesh.AbstractMesh;
import ahd.usim.engine.internal.Camera;
import ahd.usim.engine.internal.Engine;
import ahd.usim.engine.internal.Input;
import ahd.usim.engine.internal.DefaultRenderer;
import ahd.usim.engine.internal.light.PointLight;
import ahd.usim.engine.util.Sampling;
import ahd.usim.ulib.jmath.datatypes.functions.Surface;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static java.lang.Math.*;

public class SampleLogic extends AbstractLogic {

    private final DefaultRenderer renderer;
    private Entity[] entities;
    private final Camera camera;
    private final Vector3f cameraInc;
    private final Input input;

    private Vector3f ambientLight;

    private PointLight pointLight;

    public SampleLogic() {
        renderer = new DefaultRenderer();
        camera = new Camera();
        cameraInc = new Vector3f();
        input = Engine.getEngine().getInput();
    }

    @Override
    public void initialize() {
        renderer.init();
        //Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        AbstractMesh mesh = null;
        try {
            mesh = ModelLoader.loadMesh(Constants.DEFAULT_RESOURCE_ROOT_PATH + "models/cube.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Texture texture = new Texture("textures/grassblock.png");
        Material material = new Material(1f, texture);

        assert mesh != null;
        mesh.setMaterial(material);
        var gameItem = new Entity(mesh);
        gameItem.setScale(0.5f);
        gameItem.setPosition(0, 0, -2);
        var en = new ArrayList<Entity>();
        int count = 0;
//        while (count++ < 100)
//            en.add(new Entity(mesh) {{getPosition().set(Math.random() * 20, Math.random() * 20, Math.random() * -20); getMesh().setMaterial(material);}});
////        entities = new Entity[]{
////                gameItem
////        };
//        entities = en.toArray(Entity[]::new);

        var sample = Sampling.sample(-PI / 2, PI / 2, -PI, PI, 0.01, 0.1, Surface.kleinBottle());
//        var sample = Sampling.sample(0, .1, 0, .1, 0.01, 0.01, (x, y) -> Point3D.of(x, y, x + y));
//        var sample = Sampling.sample(-PI, PI, 0.01, t -> Point3D.of(sin(t * 5) + 1, cos(t * 10) + 1, -t));

//                entities = new Entity[] { new Entity(new Mesh(sample.vertices(), null, sample.colors(), sample.normals(), sample.indices())) };
//                entities = new Entity[] { new Entity(new ImmutableMesh(sample.vertices(), sample.colors(), null, sample.indices())) };
        entities = new Entity[] {gameItem};

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 0, 1);
        float lightIntensity = 1f;
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
    }

    @Override
    public void input() {
        cameraInc.set(
                input.isKeyPressed(GLFW_KEY_A) ? 1 : input.isKeyPressed(GLFW_KEY_D) ? -1 : 0,
                input.isKeyPressed(GLFW_KEY_Z) ? 1 : input.isKeyPressed(GLFW_KEY_X) ? -1 : 0,
                input.isKeyPressed(GLFW_KEY_W) ? 1 : input.isKeyPressed(GLFW_KEY_S) ? -1 : 0
        );
    }

    Vector3f tmp = new Vector3f();
    @Override
    public void update() {
        camera.move(cameraInc.x * Constants.DEFAULT_CAMERA_MOVEMENT_SENSITIVITY, cameraInc.y * Constants.DEFAULT_CAMERA_MOVEMENT_SENSITIVITY,
                cameraInc.z * Constants.DEFAULT_CAMERA_MOVEMENT_SENSITIVITY);
        if (input.isMouseLeftButtonPressed()) {
            Vector2f rotVec = input.getDisplayVector();
            camera.rotate(rotVec.x * Constants.DEFAULT_MOUSE_MOVEMENT_SENSITIVITY, rotVec.y * Constants.DEFAULT_MOUSE_MOVEMENT_SENSITIVITY, 0);
        }
        pointLight.getPosition().set(2  * (float) Math.sin(tmp.x), tmp.y, 2 * (float) Math.cos(tmp.z) - 2);
        camera.getPosition().set(2  * (float) Math.sin(tmp.x += 0.01), tmp.y, 2 * (float) Math.cos(tmp.z += 0.01) + 2);
//        for (Entity entity : entities) {
////            entity.getRotation().add(0.001f, 0.001f, 0.001f);
////            entity.getMesh().updateVertices(Mesh.ArrayTask.bouncing);
//
//            entity.getMesh().updateVerticesAndColors((vertices, colors, normals) -> {
//                for (int i = 0; i < colors.length; i++) {
//                    var rand = Math.random();
////                    colors[i] += (rand < 0.5 ? rand < 0.25 ? -1 : 1 : 0) * 0.1f;
//                    vertices[i] += (rand < 0.5 ? rand < 0.25 ? -1 : 1 : 0) * 0.001f;
//                }
//            });
//        }
    }

    @Override
    public void render() {
        renderer.render(entities, camera, ambientLight, pointLight);
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
