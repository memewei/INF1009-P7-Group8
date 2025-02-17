package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.utils.Disposable;

public class SceneManager implements Disposable {
    private Scene currentScene;

    public void update(Scene scene) {
        if (this.currentScene != null) {
            this.currentScene.dispose();
        }
        this.currentScene = scene;
    }

    public void render(Scene scene) {
        if (currentScene != null) {
            currentScene.render();
        }
    }

    public void dispose() {
        if (currentScene != null) {
            currentScene.dispose();
        }
    }
}