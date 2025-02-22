package com.vibhusha.utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader {

    public static Model loadOBJ(String filePath) throws IOException {
        InputStream inputStream = ModelLoader.class.getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) {
            throw new IOException("OBJ file not found: " + filePath);
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            List<float[]> vertices = new ArrayList<>();
            List<float[]> textures = new ArrayList<>();
            List<float[]> normals = new ArrayList<>();
            List<int[]> faces = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length < 2) continue;

                switch (parts[0]) {
                    case "v" -> // Vertex position
                            vertices.add(new float[]{Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])});
                    case "vt" -> // Texture coordinates
                            textures.add(new float[]{Float.parseFloat(parts[1]), Float.parseFloat(parts[2])});
                    case "vn" -> // Vertex normals
                            normals.add(new float[]{Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])});
                    case "f" -> { // Face definition
                        int[] face = new int[3]; // Only handles triangles
                        face[0] = Integer.parseInt(parts[1].split("/")[0]) - 1;
                        face[1] = Integer.parseInt(parts[2].split("/")[0]) - 1;
                        face[2] = Integer.parseInt(parts[3].split("/")[0]) - 1;
                        faces.add(face);
                    }
                }
            }

            // Prepare vertex, texture, and normal data for OpenGL
            float[] vertexData = new float[vertices.size() * 3];
            float[] textureData = new float[textures.size() * 2];
            float[] normalData = new float[normals.size() * 3];
            int[] indices = new int[faces.size() * 3];

            int vertexIndex = 0;
            int textureIndex = 0;
            int normalIndex = 0;
            int indexCount = 0;

            for (float[] vertex : vertices) {
                vertexData[vertexIndex++] = vertex[0];
                vertexData[vertexIndex++] = vertex[1];
                vertexData[vertexIndex++] = vertex[2];
            }

            for (float[] texture : textures) {
                textureData[textureIndex++] = texture[0];
                textureData[textureIndex++] = texture[1];
            }

            for (float[] normal : normals) {
                normalData[normalIndex++] = normal[0];
                normalData[normalIndex++] = normal[1];
                normalData[normalIndex++] = normal[2];
            }

            for (int[] face : faces) {
                indices[indexCount++] = face[0];
                indices[indexCount++] = face[1];
                indices[indexCount++] = face[2];
            }

            // Create OpenGL buffers
            int vaoId = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoId);

            int vboId = createVBO(vertexData, GL15.GL_ARRAY_BUFFER, 0, 3);
            int vboTexId = createVBO(textureData, GL15.GL_ARRAY_BUFFER, 1, 2);
            int vboNormalId = createVBO(normalData, GL15.GL_ARRAY_BUFFER, 2, 3);
            int eboId = createEBO(indices);

            GL30.glBindVertexArray(0);

            // Return the model with position as (0, 0, 0) or a custom position
            return new Model(vaoId, vboId, eboId, indices.length, new Vector3f(0, 0, 0)); // or pass a custom position if needed

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace(); // Log error while closing the reader
                }
            }
        }
    }

    // Helper method to create and return VBO
    private static int createVBO(float[] data, int bufferType, int attributeIndex, int attributeSize) {
        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(bufferType, vboId);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data).flip();
        GL15.glBufferData(bufferType, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeIndex, attributeSize, GL20.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(attributeIndex);
        return vboId;
    }

    // Helper method to create and return EBO
    private static int createEBO(int[] indices) {
        int eboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(indices.length);
        elementBuffer.put(indices).flip();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL15.GL_STATIC_DRAW);
        return eboId;
    }
}
