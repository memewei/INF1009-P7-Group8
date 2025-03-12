package io.github.some_example_name.lwjgl3.application_classes.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameState;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;

public class PauseScene extends Scene {
    private Texture pauseBackground;
    private SpriteBatch batch;
    private SceneManager sceneManager;
    private EntityManager entityManager;
    private MovementManager movementManager;

    public PauseScene(SpriteBatch batch, SceneManager sceneManager) {
        this.batch = batch;
        this.sceneManager = sceneManager;
    }
    
    // Overloaded constructor that accepts managers
    public PauseScene(SpriteBatch batch, SceneManager sceneManager, EntityManager entityManager, MovementManager movementManager) {
        this.batch = batch;
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
    }

    @Override
    public void initialize() {
        System.out.println("[PauseScene] Initializing...");
        
        loadPauseMenuTexture();
    }

    private void loadPauseMenuTexture() {
        if (pauseBackground == null) {
            try {
                pauseBackground = new Texture(Gdx.files.internal("pause_menu.png"));
                System.out.println("[PauseScene] Pause menu loaded.");
            } catch (Exception e) {
                System.err.println("[PauseScene] Error loading background: " + e.getMessage());
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("[PauseScene] Resuming game...");
            
            if (sceneManager != null) {
                sceneManager.popScene();
                sceneManager.setGameState(GameState.RUNNING);
            } else {
                System.err.println("[PauseScene] sceneManager is NULL! Cannot resume.");
            }
        } else if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.Q)) {
            System.out.println("[PauseScene] Exiting to main menu...");

            if (sceneManager != null && entityManager != null && movementManager != null) {
                sceneManager.changeScene(new MenuScene(batch, sceneManager, entityManager, movementManager), GameState.MAIN_MENU);
            } else if (sceneManager != null) {
                // Fallback if managers aren't available
                sceneManager.changeScene(new MenuScene(batch, sceneManager, null, null), GameState.MAIN_MENU);
                System.err.println("[PauseScene] Warning: Unable to pass entity or movement managers to MenuScene.");
            } else {
                System.err.println("[PauseScene] sceneManager is NULL! Cannot change to main menu.");
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        if (pauseBackground != null) {
            batch.draw(pauseBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        batch.end();
    }

    @Override
    public void dispose() {
        if (pauseBackground != null) {
            pauseBackground.dispose();
            pauseBackground = null;
            System.out.println("[PauseScene] Background texture disposed.");
        }
    }
}