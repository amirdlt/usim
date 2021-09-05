package com.usim.engine.engine.internal;

import org.joml.Vector3f;

public class Camera {
    private final Vector3f position;
    private final Vector3f rotation;

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Camera() {
        this(new Vector3f(), new Vector3f());
    }

    public Vector3f move(float dx, float dy, float dz) {
        return position.add(dx, dy, dz);
    }

    public Vector3f setPosition(float x, float y, float z) {
        return position.set(x, y, z);
    }

    public Vector3f rotate(float dRoll, float dYaw, float dPitch) {
        return rotation.add(dRoll, dYaw, dPitch);
    }

    public Vector3f setRotation(float roll, float yaw, float pitch) {
        return rotation.set(roll, yaw, pitch);
    }

    public Vector3f setPosition(Vector3f position) {
        return this.position.set(position);
    }

    public Vector3f setRotation(Vector3f rotation) {
        return this.rotation.set(rotation);
    }

    public Vector3f rotation() {
        return rotation;
    }

    public Vector3f position() {
        return position;
    }
}
