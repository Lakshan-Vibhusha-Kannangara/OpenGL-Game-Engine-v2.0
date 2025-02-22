package com.vibhusha.utils;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Model {
    private final int vaoId;
    private final int vboId;
    private final int eboId;
    private final int vertexCount;
    @Getter @Setter
    private Vector3f position; // Store model's position

    public Model(int vaoId, int vboId, int eboId, int vertexCount, Vector3f position) {
        this.vaoId = vaoId;
        this.vboId = vboId;
        this.eboId = eboId;
        this.vertexCount = vertexCount;
        this.position = position != null ? position : new Vector3f(0, 0, 0);
    }




    public void render(Shader shader) {
        GL30.glBindVertexArray(vaoId);

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        // Set the model matrix uniform
        shader.setUniform("modelMatrix", getModelMatrix());

        // Draw the elements
        GL30.glDrawElements(GL30.GL_TRIANGLES, vertexCount, GL30.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private float[] getModelMatrix() {
        Matrix4f modelMatrix = new Matrix4f().identity().translate(position);
        float[] matrixData = new float[16];
        modelMatrix.get(matrixData);
        return matrixData;
    }

    public void cleanup() {
        // Delete VAO, VBO, and EBO
        GL30.glDeleteVertexArrays(vaoId);
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(eboId);
    }
}
