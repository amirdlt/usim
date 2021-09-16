package ahd.usim.engine.internal;

import ahd.usim.engine.internal.Camera;
import ahd.usim.engine.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public final class Transformation {
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f modelViewMatrix;

    public Transformation() {
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
    }

    public Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        return projectionMatrix.setPerspective(fov, width / height, zNear, zFar);
    }

    public Matrix4f getViewMatrix(@NotNull Camera camera) {
        var rot = camera.getRotation();
        return viewMatrix.identity().rotate(rot.x, 1, 0, 0).rotate(rot.y, 0, 1, 0).translate(camera.getPosition());
    }

    public Matrix4f getModelViewMatrix(@NotNull Entity entity, Matrix4f viewMatrix) {
        var rot = entity.getRotation();
        return modelViewMatrix.set(viewMatrix).translate(entity.getPosition()).rotateX(-rot.x).rotateY(-rot.y).rotateZ(-rot.z)
                .scale(entity.getScale());
//        return new Matrix4f(viewMatrix).mul(
//                modelViewMatrix.identity().translate(entity.getPosition()).rotateX(-rot.x).rotateY(-rot.y).rotateZ(-rot.z)
//                        .scale(entity.getScale()));
    }
}
