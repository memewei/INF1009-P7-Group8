package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MenuScene extends Scene {
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private SceneManager sceneManager;

    public MenuScene(SpriteBatch batch, SceneManager sceneManager) {
        this.batch = batch;
        this.sceneManager = sceneManager;
    }

    @Override
    public void initialize() {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("menuScene1.png"));
        } catch (Exception e) {
            System.err.println("Error loading menu texture: " + e.getMessage());
            backgroundTexture = new Texture(Gdx.files.internal("defaultMenu.png"));
        }
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
            System.out.println("Starting game...");

            // ✅ FIX: Use SceneManager to pass World correctly
            sceneManager.changeScene(new GameScene(
                    batch,
                    sceneManager.getEntityManager(),
                    sceneManager.getMovementManager(),
                    sceneManager.getWorld() // ✅ Now correctly retrieves World
            ));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        batch.end();
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
