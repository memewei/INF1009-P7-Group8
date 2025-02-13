package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.utils.ScreenUtils;

public class GameScene extends Scene {
    public GameScene(int sceneID) {
        super(sceneID);
    }

    @Override
    public void update() {
        // Update logic for gameplay scene
        for (Entity entity : entityComponents) {
            entity.update();
        }
    }

    @Override
    public void render() {
        // Render logic for gameplay scene
        for (Entity entity : entityComponents) {
            entity.render();
        }
    }

    @Override
    public void handleInput(String userInput) {
        // Handle input in gameplay scene
    }

    @Override
    public void dispose() {
        // Dispose resources used by the gameplay scene
        for (Entity entity : entityComponents) {
            entity.dispose();
        }
    }
}