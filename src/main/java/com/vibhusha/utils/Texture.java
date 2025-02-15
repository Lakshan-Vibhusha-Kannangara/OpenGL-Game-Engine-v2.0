package com.vibhusha.utils;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Texture {
    private int textureId;

    public Texture(String filePath) {
        try {
            // Load the image using Apache Commons Imaging
            File file = new File(filePath);
            BufferedImage image = Imaging.getBufferedImage(file);

            // Get image dimensions and create a ByteBuffer for RGBA data
            int width = image.getWidth();
            int height = image.getHeight();
            ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);

            // Extract the pixel data from the BufferedImage into the ByteBuffer
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            for (int i = 0; i < pixels.length; i += 3) {
                buffer.put(pixels[i + 2]); // Red
                buffer.put(pixels[i + 1]); // Green
                buffer.put(pixels[i]);     // Blue
                buffer.put((byte) 255);     // Alpha
            }
            buffer.flip(); // Prepare buffer for OpenGL usage

            // OpenGL texture creation
            textureId = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        } catch (IOException | ImageReadException e) {
            e.printStackTrace();
        }
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    }

    public void cleanup() {
        GL11.glDeleteTextures(textureId);
    }
}
