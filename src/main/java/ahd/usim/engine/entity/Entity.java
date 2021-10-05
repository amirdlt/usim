package ahd.usim.engine.entity;

import ahd.ulib.utils.annotation.ChangeReference;
import ahd.usim.engine.entity.mesh.AbstractMesh;
import ahd.usim.engine.internal.api.Cleanable;
import ahd.usim.engine.internal.api.Updatable;
import ahd.usim.engine.internal.api.Visible;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Entity implements Visible, Cleanable, Updatable {

    private AbstractMesh mesh;
    private Vector3f position;
    private float scale;
    private Vector3f rotation;

    public Entity(@NotNull AbstractMesh mesh) {
        this.mesh = mesh;
        mesh.attach();
        position = new Vector3f();
        scale = 1;
        rotation = new Vector3f();
    }

    @Override
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

    @ChangeReference
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    @ChangeReference
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
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

    public void setMesh(@NotNull AbstractMesh mesh) {
        this.mesh = mesh;
    }

    public AbstractMesh getMesh() {
        return mesh;
    }

    @Override
    public void cleanup() {
        mesh.cleanup();
    }

    @Override
    public boolean isCleaned() {
        return mesh.isCleaned();
    }

    @Override
    public void update() {
        // empty body by default
    }
}
