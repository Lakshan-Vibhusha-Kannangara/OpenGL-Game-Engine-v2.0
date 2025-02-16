package com.vibhusha.utils;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Model {
    private final int vaoId;
    private final int vboId;
    private final int eboId;
    private final int vertexCount;

    public Model(int vaoId, int vboId, int eboId, int vertexCount) {
        this.vaoId = vaoId;
        this.vboId = vboId;
        this.eboId = eboId;
        this.vertexCount = vertexCount;
    }

    public void render() {
        GL30.glBindVertexArray(vaoId);

        // Enable all the attributes (Positions: 0, Textures: 1, Normals: 2)
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        // Draw the elements
        GL30.glDrawElements(GL30.GL_TRIANGLES, vertexCount, GL30.GL_UNSIGNED_INT, 0);

        // Disable attributes and unbind
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        // Delete VAO, VBO, and EBO
        GL30.glDeleteVertexArrays(vaoId);
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(eboId);
    }
}
