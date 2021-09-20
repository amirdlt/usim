package ahd.usim.engine.entity.material;

import ahd.usim.engine.internal.api.Cleanable;
import org.joml.Vector3f;

import static ahd.usim.engine.Constants.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Material implements Cleanable {
    private Vector3f diffuseColor;
    private Vector3f specularColor;
    private Vector3f ambientColor;

    private float reflectance;
    private Texture texture;

    private boolean isCleaned;

    private final Object mutex = new Object();
    private volatile int materialUseCount;

    public Material() {
        this(0, null);
    }

    public Material(Texture texture) {
        this(0, texture);
    }

    public Material(float reflectance, Texture texture) {
        this(DEFAULT_DIFFUSE_COLOR, DEFAULT_SPECULAR_COLOR, DEFAULT_AMBIENT_COLOR, reflectance, texture);
    }

    public Material(Vector3f diffuseColor, Vector3f specularColor, Vector3f ambientColor, float reflectance, Texture texture) {
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.ambientColor = ambientColor;
        this.reflectance = reflectance;
        this.texture = texture;
        materialUseCount = 0;
        isCleaned = false;
    }

    public Vector3f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Vector3f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public Vector3f getSpecularColor() {
        return specularColor;
    }

    public void setSpecularColor(Vector3f specularColor) {
        this.specularColor = specularColor;
    }

    public Vector3f getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(Vector3f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean isTextured() {
        return texture != null;
    }

    public void activateTexture() {
        if (texture == null || texture.isCleaned())
            return;
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getId());
    }

    public void attach() {
        synchronized (mutex) {
            materialUseCount++;
        }
    }

    public void detach() {
        synchronized (mutex) {
            materialUseCount--;
        }
    }

    @Override
    public final void cleanup() {
        synchronized (mutex) {
            if (--materialUseCount > 0 || isCleaned)
                return;
            if (texture != null)
                texture.cleanup();
            isCleaned = true;
        }
    }

    @Override
    public boolean isCleaned() {
        return isCleaned;
    }
}
