package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class SceneManager implements Disposable {
    private Stack<Scene> sceneStack = new Stack<>();
    private List<SceneEventListener> eventListeners = new ArrayList<>();
    private float transitionAlpha = 0f;
    private boolean transitioning = false;
    private Scene currentScene;
    private Texture transitionTexture;

    public SceneManager(){
        transitionTexture = new Texture(Gdx.files.internal("transitionFade.png"));
    }

    public void pushScene(Scene scene) {
        if (currentScene != null) {
            sceneStack.push(currentScene); // Save the current scene
        }
        currentScene = scene;
        triggerSceneChange(scene);
    }

    public void popScene() {
        if (!sceneStack.isEmpty()) {
            currentScene.dispose();
            currentScene = sceneStack.pop();
            triggerSceneChange(currentScene);
        }
    }

    public void update(float deltaTime) {
        if (transitioning) {
            transitionAlpha += deltaTime;
            if (transitionAlpha > 1f) {
                transitioning = false;
                transitionAlpha = 1f;
            }
        }
        // Update the current scene if it has an update method (optional)
        if (currentScene != null) {
            currentScene.update(deltaTime); // If scenes have an update method
        }
    }

    public void changeScene(Scene newScene) {
        if (currentScene != null) {
            currentScene.dispose(); // Dispose of the old scene if needed
        }
        currentScene = newScene;
    }

    public void render(SpriteBatch batch) {
        if (currentScene != null) {
            currentScene.render(batch);
        }
        if (transitioning) {
            // Render fading sequence
            batch.begin();
            batch.setColor(0, 0, 0, transitionAlpha);
            batch.draw(transitionTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        }
    }
    public interface SceneEventListener {
        void onSceneChange(Scene newScene);
    }

    public void addSceneEventListener(SceneEventListener listener) {
        eventListeners.add(listener);
    }

    public void triggerSceneChange(Scene newScene) {
        transitioning = true; // Start the transition
        for (SceneEventListener listener : eventListeners) {
            listener.onSceneChange(newScene);
        }
    }

    @Override
    public void dispose() {
        if (currentScene != null) {
            currentScene.dispose();
        }
    }
}