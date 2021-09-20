package ahd.usim.engine.internal.light;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class PointLight {
    public record Attenuation(float constant, float linear, float exponent) {}

    private Vector3f color;
    private Vector3f position;
    private float intensity;

    private Attenuation attenuation;

    public PointLight(Vector3f color, Vector3f position, float intensity) {
        this(color, position, intensity, new Attenuation(1, 0, 0));
    }

    public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation) {
        this.color = color;
        this.position = position;
        this.intensity = intensity;
        this.attenuation = attenuation;
    }

    @SuppressWarnings("CopyConstructorMissesField")
    @Contract(pure = true)
    public PointLight(@NotNull PointLight pointLight) {
        this(new Vector3f(pointLight.color), new Vector3f(pointLight.position), pointLight.intensity, pointLight.attenuation);
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Attenuation getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
    }

    @Contract("_, _, _ -> new")
    public static @NotNull Attenuation attenuation(float constant, float linear, float exponent) {
        return new Attenuation(constant, linear, exponent);
    }
}
