package com.vibhusha.utils;


import java.util.List;

public class ModelConfig {
    public List<ModelEntry> models;

    public static class ModelEntry {
        public String path;
        public float[] position; // [x, y, z]
    }
}
