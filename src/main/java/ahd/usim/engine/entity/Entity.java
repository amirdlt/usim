package ahd.usim.engine.entity;

import ahd.usim.engine.entity.mesh.AbstractMesh;
import ahd.usim.ulib.utils.annotation.ChangeReference;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Entity {

    private final AbstractMesh mesh;
    private final Vector3f position;
    private float scale;
    private final Vector3f rotation;

    public Entity(@NotNull AbstractMesh mesh) {
        this.mesh = mesh;
        mesh.attach();
        position = new Vector3f();
        scale = 1;
        rotation = new Vector3f();
    }

    public void render() {
        mesh.render();
    }

    @ChangeReference(change = false)
    public Vector3f getPosition() {
        return position;
    }

    @ChangeReference(change = false)
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @ChangeReference(change = false)
    public Vector3f getRotation() {
        return rotation;
    }

    @ChangeReference(change = false)
    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }
    
    public AbstractMesh getMesh() {
        return mesh;
    }
}
