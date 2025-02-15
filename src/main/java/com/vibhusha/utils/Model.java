package com.vibhusha.utils;


import org.lwjgl.opengl.GL30;

public class Model {
    private int vaoId;
    private int vertexCount;

    public Model(int vaoId, int vertexCount) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
    }

    public void render() {
        GL30.glBindVertexArray(vaoId);
        GL30.glEnableVertexAttribArray(0);
        GL30.glDrawElements(GL30.GL_TRIANGLES, vertexCount, GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        GL30.glDeleteVertexArrays(vaoId);
    }
}
