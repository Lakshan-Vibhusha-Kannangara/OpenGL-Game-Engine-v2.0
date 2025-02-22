package com.vibhusha.renderEngine;

import com.vibhusha.utils.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;

public class GameEngine {
    private long window;
    private int width = 800, height = 600;
    private boolean running = true;
    private Camera camera;
    private Model model;
    private Shader shader;
    private Texture texture;
    private long lastFrameTime;

    public void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Set OpenGL version hints for macOS
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE); // Required for macOS
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE); // Use core profile

        window = GLFW.glfwCreateWindow(width, height, "OpenGL 3D Engine", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwShowWindow(window);
        GL.createCapabilities();

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        // Ensure a VAO is bound (required in OpenGL Core profile)
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        camera = new Camera(0, 0, 3);
        lastFrameTime = System.nanoTime();

        try {
            model = ModelLoader.loadOBJ("assets/obj/cube.obj");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        shader = new Shader("shaders/vertex.glsl", "shaders/fragment.glsl");
        texture = new Texture("assets/textures/texture.png");
    }

    public void loop() {
        while (running && !GLFW.glfwWindowShouldClose(window)) {
            update();
            render();
        }
    }

    private void update() {
        GLFW.glfwPollEvents();
        float deltaTime = getDeltaTime();
        camera.processInput(window, deltaTime);
        camera.processMouseMovement(window);
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            running = false;
        }
    }

    private float getDeltaTime() {
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f;
        lastFrameTime = currentTime;
        return deltaTime;
    }

    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        shader.bind();

        // Convert the matrices to float arrays and set them as uniforms
        float[] viewMatrixData = new float[16];
        camera.getViewMatrix().get(viewMatrixData);  // Fill the array with the view matrix data
        shader.setUniform("viewMatrix", viewMatrixData);  // Set the uniform

        float[] projectionMatrixData = new float[16];
        camera.getProjectionMatrix(width, height).get(projectionMatrixData);  // Fill the array with the projection matrix data
        shader.setUniform("projectionMatrix", projectionMatrixData);  // Set the uniform

        texture.bind();
        model.render();
        shader.unbind();

        GLFW.glfwSwapBuffers(window);
    }

    public void cleanup() {
        model.cleanup();
        shader.cleanup();
        texture.cleanup();
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public static void main(String[] args) {
        GameEngine engine = new GameEngine();
        engine.init();
        engine.loop();
        engine.cleanup();
    }
}
