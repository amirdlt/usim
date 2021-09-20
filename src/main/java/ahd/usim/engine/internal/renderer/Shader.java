package ahd.usim.engine.internal.renderer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ahd.usim.engine.Constants;
import ahd.usim.engine.entity.material.Material;
import ahd.usim.engine.internal.api.Rebuild;
import ahd.usim.engine.internal.light.PointLight;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL20.*;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

public class Shader implements Rebuild {
    private int programId;

    private int vertexShaderId;
    private int fragmentShaderId;

    private Map<String, Integer> uniforms;

    private boolean isCleaned;

    public Shader() {
        initialize();
    }

    public void createUniform(String uniformName) {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0)
            throw new RuntimeException("AHD:: Could not find uniform: " + uniformName);
        uniforms.put(uniformName, uniformLocation);
    }

    public void createPointLightUniform(String uniformName) {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".attenuation.constant");
        createUniform(uniformName + ".attenuation.linear");
        createUniform(uniformName + ".attenuation.exponent");
    }

    public void createMaterialUniform(String uniformName) {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }

    public void setUniform(String uniformName, @NotNull Matrix4f value) {
        try (var stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, @NotNull Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, @NotNull Vector4f value) {
        glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, @NotNull PointLight pointLight) {
        setUniform(uniformName + ".color", pointLight.getColor());
        setUniform(uniformName + ".position", pointLight.getPosition());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation att = pointLight.getAttenuation();
        setUniform(uniformName + ".attenuation.constant", att.constant());
        setUniform(uniformName + ".attenuation.linear", att.linear());
        setUniform(uniformName + ".attenuation.exponent", att.exponent());
    }

    public void setUniform(String uniformName, @NotNull Material material) {
        setUniform(uniformName + ".ambient", material.getAmbientColor());
        setUniform(uniformName + ".diffuse", material.getDiffuseColor());
        setUniform(uniformName + ".specular", material.getSpecularColor());
        setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    public void createVertexShader(String shaderCode) {
        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
            glDeleteShader(vertexShaderId);
            vertexShaderId = 0;
        }
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) {
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
            glDeleteShader(fragmentShaderId);
            fragmentShaderId = 0;
        }
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    private int createShader(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0)
            throw new RuntimeException("AHD:: Error creating shader. Type: " + shaderType);

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("AHD:: Error compiling Shader code: " + glGetShaderInfoLog(shaderId, Constants.GL_LOG_MAX_LENGTH));

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0)
            throw new RuntimeException("AHD:: Error linking Shader code: " + glGetProgramInfoLog(programId, Constants.GL_LOG_MAX_LENGTH));

        if (vertexShaderId != 0)
            glDetachShader(programId, vertexShaderId);

        if (fragmentShaderId != 0)
            glDetachShader(programId, fragmentShaderId);

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0)
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, Constants.GL_LOG_MAX_LENGTH));
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    @Override
    public void cleanup() {
        unbind();
        if (isCleaned)
            return;
        if (programId != 0)
            glDeleteProgram(programId);
        isCleaned = true;
    }

    @Override
    public boolean isCleaned() {
        return isCleaned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Shader shader = (Shader) o;
        return programId == shader.programId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(programId);
    }

    @Override
    public void initialize() {
        vertexShaderId = fragmentShaderId = 0;
        programId = glCreateProgram();
        if (programId == 0)
            throw new RuntimeException("AHD:: Could not create Shader");
        uniforms = new HashMap<>();
        isCleaned = false;
    }
}
