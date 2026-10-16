package com.example.roguelike;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Roguelike");
        config.setWindowedMode(960, 640);
        config.useVsync(true);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new RoguelikeGame(), config);
    }
}
