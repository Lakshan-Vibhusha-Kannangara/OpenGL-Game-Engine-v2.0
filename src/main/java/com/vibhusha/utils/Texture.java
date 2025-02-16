package com.vibhusha.utils;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Texture {
    private int textureId;

    public Texture(String resourcePath) {
        try {
            // Load image using getResourceAsStream
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                throw new IOException("Texture file not found: " + resourcePath);
            }

            // Decode image using Apache Commons Imaging
            BufferedImage image = Imaging.getBufferedImage(inputStream);

            // Ensure image is in RGBA format and convert if necessary
            BufferedImage convertedImage = new BufferedImage(
                    image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g = convertedImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();

            int width = convertedImage.getWidth();
            int height = convertedImage.getHeight();
            ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);

            // Extract pixel data
            byte[] pixels = ((DataBufferByte) convertedImage.getRaster().getDataBuffer()).getData();
            buffer.put(pixels);
            buffer.flip(); // Prepare buffer for OpenGL usage

            // OpenGL texture setup
            textureId = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            // Close InputStream
            inputStream.close();
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
