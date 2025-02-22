package com.vibhusha.renderEngine;

import com.vibhusha.utils.*;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameEngine {
    private long window;
    private final int width = 800, height = 600;
    private boolean running = true;
    private Camera camera;
    private List<Model> models;
    private Shader shader;
    private Texture texture;
    private long lastFrameTime;
    private ExecutorService executorService;

    public void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

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

        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        camera = new Camera(0, 0, 3);
        lastFrameTime = System.nanoTime();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            ModelConfig config = YamlLoader.load("assets/models.yaml");
            models = new ArrayList<>();

            for (ModelConfig.ModelEntry entry : config.models) {
                Model model = ModelLoader.loadOBJ(entry.path);
                model.setPosition(new Vector3f(entry.position[0], entry.position[1], entry.position[2]));
                models.add(model);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load models from YAML", e);
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

        float[] viewMatrixData = new float[16];
        camera.getViewMatrix().get(viewMatrixData);
        shader.setUniform("viewMatrix", viewMatrixData);

        float[] projectionMatrixData = new float[16];
        camera.getProjectionMatrix(width, height).get(projectionMatrixData);
        shader.setUniform("projectionMatrix", projectionMatrixData);

        texture.bind();

        for (Model model : models) {
            model.render(shader);
        }

        shader.unbind();
        GLFW.glfwSwapBuffers(window);
    }



    public void cleanup() {
        models.forEach(Model::cleanup);
        shader.cleanup();
        texture.cleanup();
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        executorService.shutdown();
    }

    public static void main(String[] args) {
        GameEngine engine = new GameEngine();
        engine.init();
        engine.loop();
        engine.cleanup();
    }
}
