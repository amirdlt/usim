package com.usim.engine.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static com.usim.engine.Constants.GL_LOG_MAX_LENGTH;
import static org.lwjgl.opengl.GL20.*;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public class Shader {
    private final int programId;

    private int vertexShaderId;
    private int fragmentShaderId;

    private final Map<String, Integer> uniforms;

    public Shader() {
        programId = glCreateProgram();
        if (programId == 0)
            throw new RuntimeException("AHD:: Could not create Shader");
        uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0)
            throw new RuntimeException("AHD:: Could not find uniform:" + uniformName);
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, @NotNull Matrix4f value) {
        try (var stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void createVertexShader(String shaderCode) {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0)
            throw new RuntimeException("AHD:: Error creating shader. Type: " + shaderType);

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("AHD:: Error compiling Shader code: " + glGetShaderInfoLog(shaderId, GL_LOG_MAX_LENGTH));

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0)
            throw new RuntimeException("AHD:: Error linking Shader code: " + glGetProgramInfoLog(programId, GL_LOG_MAX_LENGTH));

        if (vertexShaderId != 0)
            glDetachShader(programId, vertexShaderId);

        if (fragmentShaderId != 0)
            glDetachShader(programId, fragmentShaderId);

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, GL_LOG_MAX_LENGTH));
        }

    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
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
}
