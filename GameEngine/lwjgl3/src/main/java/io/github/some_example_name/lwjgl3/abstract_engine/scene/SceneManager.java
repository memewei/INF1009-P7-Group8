// SceneManager.java
package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import java.util.Stack;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

public class SceneManager {
    private Stack<Scene> sceneStack = new Stack<>();
    private Scene currentScene;
    private World world;
    private GameState gameState;
    private Texture transitionTexture;

    public SceneManager(World world) {
        this.world = world;
        this.gameState = GameState.MAIN_MENU;
        transitionTexture = new Texture(Gdx.files.internal("transitionFade.png"));
    }

    public World getWorld() {
        return world;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState newState) {
        this.gameState = newState;
    }

    public void pushScene(Scene scene, GameState newState) {
        if (currentScene != null) {
            sceneStack.push(currentScene);
        }
        currentScene = scene;
        currentScene.initialize();
        gameState = newState;
    }

    public void popScene() {
        if (!sceneStack.isEmpty()) {
            currentScene.dispose();
            currentScene = sceneStack.pop();
            // For a paused/resumed game, do not reinitialize the game scene.
        }
    }

    public void changeScene(Scene newScene, GameState newState) {
        if (currentScene != null) {
            currentScene.dispose();
        }
        newScene.initialize();
        currentScene = newScene;
        gameState = newState;
    }

    public void update(float deltaTime) {
        if (currentScene != null) {
            currentScene.update(deltaTime);
        }
    }

    public void render(SpriteBatch batch) {
        if (currentScene != null) {
            currentScene.render(batch);
        }
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
    
    public Scene getSceneBelow() {
        if (sceneStack.size() >= 2) {
            return sceneStack.get(sceneStack.size() - 2);
        }
        return null;
    }

    public void dispose() {
        if (currentScene != null) {
            currentScene.dispose();
        }
        if (transitionTexture != null) {
            transitionTexture.dispose();
        }
    }
}