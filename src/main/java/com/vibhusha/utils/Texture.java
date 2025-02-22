package com.vibhusha.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Slf4j
public class Texture {
    private int textureId;
    private int width, height;

    public Texture(String resourcePath) {
        try {
            // Load image using getResourceAsStream
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                throw new IOException("Texture file not found: " + resourcePath);
            }

            // Decode image using Apache Commons Imaging
            BufferedImage image = Imaging.getBufferedImage(inputStream);
            width = image.getWidth();
            height = image.getHeight();

            System.out.println("Loaded Texture: " + resourcePath + " | Width: " + width + ", Height: " + height);

            // Convert BufferedImage to OpenGL-compatible format (RGBA)
            ByteBuffer buffer = convertToByteBuffer(image);

            // OpenGL texture setup
            textureId = GL11.glGenTextures();
            if (textureId == 0) {
                throw new RuntimeException("Failed to generate texture ID. OpenGL context may not be active.");
            }

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1); // Ensure proper byte alignment
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            // Set texture filtering options
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            // Free the buffer
            MemoryUtil.memFree(buffer);
            inputStream.close();
        } catch (IOException | ImageReadException e) {
            log.error(String.format("There was an error occured while initializing the texture in path %s Error %s", resourcePath, e));
        }
    }

    private ByteBuffer convertToByteBuffer(BufferedImage image) {
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];

                // Extract RGBA components
                byte a = (byte) ((pixel >> 24) & 0xFF);
                byte r = (byte) ((pixel >> 16) & 0xFF);
                byte g = (byte) ((pixel >> 8) & 0xFF);
                byte b = (byte) (pixel & 0xFF);

                buffer.put(r).put(g).put(b).put(a);
            }
        }
        buffer.flip();
        return buffer;
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    }

    public void cleanup() {
        GL11.glDeleteTextures(textureId);
    }

}
