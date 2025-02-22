package com.vibhusha.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;

public class YamlLoader {
    public static ModelConfig load(String resourcePath) throws IOException {
        // Use getResourceAsStream to load the file from the classpath
        InputStream inputStream = YamlLoader.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(inputStream, ModelConfig.class);
    }
}
