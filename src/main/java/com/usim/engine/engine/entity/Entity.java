package com.usim.engine.engine.entity;

import com.usim.engine.engine.graph.Mesh;
import com.usim.engine.engine.graph.Shader;
import org.jetbrains.annotations.NotNull;
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
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
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
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    
    public Mesh getMesh() {
        return mesh;
    }
}
