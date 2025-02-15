package com.vibhusha.renderEngine;

import com.vibhusha.utils.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class GameEngine {
    private long window;
    private int width = 800, height = 600;
    private boolean running = true;
    private Camera camera;
    private Model model;
    private Shader shader;
    private Texture texture;

    public void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

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

        camera = new Camera();
        model = ModelLoader.loadOBJ("assets/cube.obj");
        shader = new Shader("shaders/vertex.glsl", "shaders/fragment.glsl");
        texture = new Texture("assets/texture.png");
    }

    public void loop() {
        while (running && !GLFW.glfwWindowShouldClose(window)) {
            update();
            render();
        }
    }

    private void update() {
        GLFW.glfwPollEvents();
        camera.processInput(window);
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            running = false;
        }
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
