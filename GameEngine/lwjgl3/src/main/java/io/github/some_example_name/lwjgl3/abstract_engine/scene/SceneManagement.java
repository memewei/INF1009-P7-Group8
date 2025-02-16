package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.utils.ScreenUtils;

public class SceneManagement {
    private Map<String, Scene> scenes;
    private Scene currentScene;

    public SceneManagement() {
        this.scenes = new HashMap<>();
    }

    public void addScene(int sceneID, Scene scene) {
        scenes.put(sceneID, scene);
    }

    public void removeScene(int sceneID) {
        scenes.remove(sceneID);
    }

    public void switchScene(int sceneID) {
        if (scenes.containsKey(sceneID)) {
            currentScene = scenes.get(sceneID);
            currentScene.initialize();
        } else {
            System.out.println("No scene found with ID: " + sceneID);
        }
    }

    public void updateCurrentScene() {
        if (currentScene != null) {
            currentScene.update();
        }
    }

    public void renderCurrentScene() {
        if (currentScene != null) {
            currentScene.render();
        }
    }

    private void handleInput(String input) {
        if (currentScene != null) {
            currentScene.handleInput(input);
        }
    }

    public void disposeCurrentScene() {
        if (currentScene != null) {
            currentScene.dispose();
        }
    }
}
