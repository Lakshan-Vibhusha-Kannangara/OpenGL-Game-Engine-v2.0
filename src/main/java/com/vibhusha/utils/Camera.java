package com.vibhusha.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

public class Camera {
    private Vector3f position;
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private Vector3f worldUp;

    private float yaw;
    private float pitch;
    private float sensitivity = 0.1f;
    private boolean firstMouse = true;
    private double lastX, lastY;

    public Camera(float startX, float startY, float startZ) {
        this.position = new Vector3f(startX, startY, startZ);
        this.worldUp = new Vector3f(0, 1, 0);
        this.yaw = -90.0f;
        this.pitch = 0.0f;
        this.front = new Vector3f(0, 0, -1);
        this.right = new Vector3f();
        this.up = new Vector3f();
        updateCameraVectors();
    }

    public void processInput(long window, float deltaTime) {
        float speed = 2.5f * deltaTime;

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            position.add(front.mul(speed, new Vector3f()));
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            position.sub(front.mul(speed, new Vector3f()));
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            position.sub(right.mul(speed, new Vector3f()));
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            position.add(right.mul(speed, new Vector3f()));
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
            position.add(up.mul(speed, new Vector3f()));
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
            position.sub(up.mul(speed, new Vector3f()));
        }
    }

    public void processMouseMovement(long window) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer xPos = stack.mallocDouble(1);
            DoubleBuffer yPos = stack.mallocDouble(1);
            GLFW.glfwGetCursorPos(window, xPos, yPos);

            double x = xPos.get();
            double y = yPos.get();

            if (firstMouse) {
                lastX = x;
                lastY = y;
                firstMouse = false;
            }

            float xOffset = (float) (x - lastX) * sensitivity;
            float yOffset = (float) (lastY - y) * sensitivity; // Reversed since y-coordinates go from bottom to top
            lastX = x;
            lastY = y;

            yaw += xOffset;
            pitch += yOffset;

            // Limit the pitch to avoid flipping the camera
            if (pitch > 89.0f) pitch = 89.0f;
            if (pitch < -89.0f) pitch = -89.0f;

            updateCameraVectors();
        }
    }

    private void updateCameraVectors() {
        front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.normalize();

        right.set(front).cross(worldUp).normalize();
        up.set(right).cross(front).normalize();
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, position.add(front, new Vector3f()), up);
    }

    public Matrix4f getProjectionMatrix(int width, int height) {
        return new Matrix4f().perspective((float) Math.toRadians(45.0), (float) width / height, 0.1f, 100.0f);
    }
}
