package com.vibhusha.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera {
    private Vector3f position;
    private Matrix4f viewMatrix;

    public Camera() {
        this.position = new Vector3f(0, 0, 3);
        this.viewMatrix = new Matrix4f();
    }

    public void processInput(long window) {
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            position.z -= 0.1f;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            position.z += 0.1f;
        }
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix.identity().translate(-position.x, -position.y, -position.z);
    }

    public Matrix4f getProjectionMatrix(int width, int height) {
        return new Matrix4f().perspective((float) Math.toRadians(45.0), (float) width / height, 0.1f, 100.0f);
    }

}
