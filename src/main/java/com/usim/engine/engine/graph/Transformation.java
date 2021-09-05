package com.usim.engine.engine.graph;

import com.usim.engine.engine.entity.Entity;
import com.usim.engine.engine.internal.Camera;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Transformation {
    private final Matrix4f projectionMatrix;
    private final Matrix4f worldMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f modelViewMatrix;

    public Transformation() {
        worldMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
    }

    public Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        return projectionMatrix.setPerspective(fov, width / height, zNear, zFar);
    }

    public Matrix4f getViewMatrix(@NotNull Camera camera) {
        var rot = camera.rotation();
        var pos = camera.position();
        return viewMatrix.identity().rotate(rot.x, 1, 0, 0).rotate(rot.y, 0, 1, 0).translate(-pos.x, -pos.y, -pos.z);
    }

    public Matrix4f getModelViewMatrix(@NotNull Entity entity, Matrix4f viewMatrix) {
        var rot = entity.getRotation();
        return modelViewMatrix.set(viewMatrix).translate(entity.getPosition()).rotateX(-rot.x).rotateY(-rot.y).rotateZ(-rot.z)
                .scale(entity.getScale());
    }

    public Matrix4f getWorldMatrix(Vector3f offset, @NotNull Vector3f rotation, float scale) {
        return worldMatrix.translation(offset).rotateX((float) Math.toRadians(rotation.x)).rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z)).scale(scale);
    }
}
