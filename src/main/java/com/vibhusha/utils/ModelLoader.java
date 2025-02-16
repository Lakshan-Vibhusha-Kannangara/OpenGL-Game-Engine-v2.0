package com.vibhusha.utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

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

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        List<float[]> vertices = new ArrayList<>();
        List<float[]> textures = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length < 2) continue;

            switch (parts[0]) {
                case "v": // Vertex position
                    vertices.add(new float[]{Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])});
                    break;
                case "vt": // Texture coordinates
                    textures.add(new float[]{Float.parseFloat(parts[1]), Float.parseFloat(parts[2])});
                    break;
                case "vn": // Vertex normals
                    normals.add(new float[]{Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])});
                    break;
                case "f": // Face
                    int[] face = new int[9];
                    for (int i = 0; i < 3; i++) {
                        String[] vertexData = parts[i + 1].split("/");
                        face[i * 3] = Integer.parseInt(vertexData[0]) - 1;  // Vertex index
                        face[i * 3 + 1] = Integer.parseInt(vertexData[1]) - 1;  // Texture index
                        face[i * 3 + 2] = Integer.parseInt(vertexData[2]) - 1;  // Normal index
                    }
                    faces.add(face);
                    break;
            }
        }
        reader.close();

        return processModelData(vertices, textures, normals, faces);
    }

    private static Model processModelData(List<float[]> vertices, List<float[]> textures, List<float[]> normals, List<int[]> faces) {
        List<Float> vertexData = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (int[] face : faces) {
            for (int i = 0; i < 3; i++) {
                int vertexIndex = face[i * 3];
                int textureIndex = face[i * 3 + 1];
                int normalIndex = face[i * 3 + 2];

                float[] vertex = vertices.get(vertexIndex);
                float[] texture = textures.get(textureIndex);
                float[] normal = normals.get(normalIndex);

                vertexData.add(vertex[0]);
                vertexData.add(vertex[1]);
                vertexData.add(vertex[2]);
                vertexData.add(texture[0]);
                vertexData.add(texture[1]);
                vertexData.add(normal[0]);
                vertexData.add(normal[1]);
                vertexData.add(normal[2]);

                indices.add(indices.size());
            }
        }

        return createOpenGLModel(vertexData, indices);
    }

    private static Model createOpenGLModel(List<Float> vertexData, List<Integer> indices) {
        int vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        // Convert lists to buffers
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexData.size());
        vertexData.forEach(vertexBuffer::put);
        vertexBuffer.flip();

        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.size());
        indices.forEach(indexBuffer::put);
        indexBuffer.flip();

        // Create and bind VBO
        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        // Create and bind EBO (index buffer)
        int eboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        // Set vertex attribute pointers
        int stride = 8 * Float.BYTES;
        GL20.glVertexAttribPointer(0, 3, GL15.GL_FLOAT, false, stride, 0); // Positions
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(1, 2, GL15.GL_FLOAT, false, stride, 3 * Float.BYTES); // Texture Coords
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(2, 3, GL15.GL_FLOAT, false, stride, 5 * Float.BYTES); // Normals
        GL20.glEnableVertexAttribArray(2);

        // Unbind buffers
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        return new Model(vaoId, vboId, eboId, indices.size());
    }
}
