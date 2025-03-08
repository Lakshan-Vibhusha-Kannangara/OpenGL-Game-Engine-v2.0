package com.vibhusha.renderEngine;

import com.vibhusha.utils.*;

public class GameEngine {
    public static void main(String[] args) {
        Window window = new Window("Game Engine Window");
        window.init();
        window.loop();
        window.cleanup();
    }
}
