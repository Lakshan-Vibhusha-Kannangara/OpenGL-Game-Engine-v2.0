package com.vibhusha.utils;

import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Shader {
    private int programId;

    public Shader(String vertexPath, String fragmentPath) {
        int vertexShader = loadShader(vertexPath, GL20.GL_VERTEX_SHADER);
        int fragmentShader = loadShader(fragmentPath, GL20.GL_FRAGMENT_SHADER);

        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vertexShader);
        GL20.glAttachShader(programId, fragmentShader);
        GL20.glLinkProgram(programId);

        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL20.GL_FALSE) {
            throw new RuntimeException("Error linking shader program: " + GL20.glGetProgramInfoLog(programId));
        }

        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
    }

    private int loadShader(String filePath, int type) {
        try {
            String source = loadShaderSource(filePath);  // Directly load from resources
            int shaderId = GL20.glCreateShader(type);
            GL20.glShaderSource(shaderId, source);
            GL20.glCompileShader(shaderId);

            if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
                throw new RuntimeException("Error compiling shader: " + GL20.glGetShaderInfoLog(shaderId));
            }

            return shaderId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load shader file: " + filePath, e);
        }
    }

    private String loadShaderSource(String filePath) throws IOException {
        // Try loading the shader directly from the resources folder
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) {
            throw new IOException("Shader file not found: " + filePath);
        }
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public void bind() {
        GL20.glUseProgram(programId);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void cleanup() {
        GL20.glDeleteProgram(programId);
    }

    // Set a float uniform
    public void setUniform(String name, float value) {
        int location = glGetUniformLocation(programId, name);
        if (location == -1) {
            System.err.println("Warning: uniform '" + name + "' not found in shader program.");
        }
        glUniform1f(location, value);
    }

    // Set a matrix uniform (4x4 matrix)
    public void setUniform(String name, float[] matrix) {
        int location = glGetUniformLocation(programId, name);
        if (location == -1) {
            System.err.println("Warning: uniform '" + name + "' not found in shader program.");
        }
        glUniformMatrix4fv(location, false, matrix);
    }

    // Set a 3D vector uniform (x, y, z)
    public void setUniform(String name, float x, float y, float z) {
        int location = glGetUniformLocation(programId, name);
        if (location == -1) {
            System.err.println("Warning: uniform '" + name + "' not found in shader program.");
        }
        glUniform3f(location, x, y, z);
    }

    // Set a 4D vector uniform (x, y, z, w)
    public void setUniform(String name, float x, float y, float z, float w) {
        int location = glGetUniformLocation(programId, name);
        if (location == -1) {
            System.err.println("Warning: uniform '" + name + "' not found in shader program.");
        }
        glUniform4f(location, x, y, z, w);
    }

    // Set a matrix uniform using MemoryStack for better performance
    public void setUniformMatrix4f(String name, float[] matrix) {
        int location = glGetUniformLocation(programId, name);
        if (location == -1) {
            System.err.println("Warning: uniform '" + name + "' not found in shader program.");
        }

        try (MemoryStack stack = stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16); // 4x4 matrix
            buffer.put(matrix).flip();
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    // Reload shaders at runtime
    public void reloadShaders(String vertexPath, String fragmentPath) {
        int vertexShader = loadShader(vertexPath, GL20.GL_VERTEX_SHADER);
        int fragmentShader = loadShader(fragmentPath, GL20.GL_FRAGMENT_SHADER);

        GL20.glDetachShader(programId, vertexShader);
        GL20.glDetachShader(programId, fragmentShader);
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);

        vertexShader = loadShader(vertexPath, GL20.GL_VERTEX_SHADER);
        fragmentShader = loadShader(fragmentPath, GL20.GL_FRAGMENT_SHADER);

        GL20.glAttachShader(programId, vertexShader);
        GL20.glAttachShader(programId, fragmentShader);
        GL20.glLinkProgram(programId);

        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL20.GL_FALSE) {
            throw new RuntimeException("Error linking shader program: " + GL20.glGetProgramInfoLog(programId));
        }
    }
}
