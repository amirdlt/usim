package ahd.usim.engine.entity;

import ahd.usim.engine.graph.Mesh;
import org.joml.Vector3f;

public class Entity {

    private final Mesh mesh;
    private final Vector3f position;
    private float scale;
    private final Vector3f rotation;

    public Entity(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f();
        scale = 1;
        rotation = new Vector3f();
    }

    public void render() {
        mesh.render();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }
    
    public Mesh getMesh() {
        return mesh;
    }
}
