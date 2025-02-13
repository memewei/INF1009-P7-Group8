package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.utils.ScreenUtils;

public class GameScene extends Scene {
    public GameScene(int sceneID) {
        super(sceneID);
    }

    @Override
    public void update() {
        for (Entity entity : entityComponents) {
            entity.update();
        }
    }

    @Override
    public void render() {
        for (Entity entity : entityComponents) {
            entity.render();
        }
    }

    @Override
    public void handleInput(String userInput)

    @Override
    public void dispose() {
        for (Entity entity : entityComponents) {
            entity.dispose();
        }
    }
}