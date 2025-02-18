package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PauseScene extends Scene {
    private Texture pauseBackground;
    private SpriteBatch batch;
    private SceneManager sceneManager;

    public PauseScene(SpriteBatch batch, SceneManager sceneManager) {
        this.batch = batch;
        this.sceneManager = sceneManager;
    }

    @Override
    public void initialize() {
        try {
            pauseBackground = new Texture(Gdx.files.internal("pause_menu.png"));
        } catch (Exception e) {
            System.err.println("Error loading pause menu: " + e.getMessage());
        }
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            System.out.println("[PauseScene] Resuming game...");
            sceneManager.popScene(); // ✅ Demonstrates SceneStack working
            sceneManager.setGameState(GameState.RUNNING);
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
        }
    }
}
