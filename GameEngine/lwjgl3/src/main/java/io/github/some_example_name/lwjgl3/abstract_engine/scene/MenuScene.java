package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MenuScene extends Scene {
    private Texture backgroundTexture;
    private Texture pressEnterTexture; // "Press ENTER to Start"
    private SpriteBatch batch;
    private SceneManager sceneManager;
    private float textY;
    private float timeElapsed;

    public MenuScene(SpriteBatch batch, SceneManager sceneManager) {
        this.batch = batch;
        this.sceneManager = sceneManager;
        this.textY = Gdx.graphics.getHeight() / 4f; // Initial Y position for floating effect
        this.timeElapsed = 0;
    }

    @Override
    public void initialize() {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("menuScene1.png"));
            pressEnterTexture = new Texture(Gdx.files.internal("press_enter.png")); //Load image
        } catch (Exception e) {
            System.err.println("Error loading menu texture: " + e.getMessage());
            backgroundTexture = new Texture(Gdx.files.internal("menuScene1.png"));
        }
    }

    @Override
    public void update(float deltaTime) {
        timeElapsed += deltaTime;
        textY = (Gdx.graphics.getHeight() / 4f) + (float) Math.sin(timeElapsed * 2) * 10; // Floating effect

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
            System.out.println("Starting game...");
            sceneManager.changeScene(new GameScene(
                    batch,
                    sceneManager.getEntityManager(),
                    sceneManager.getMovementManager(),
                    sceneManager.getWorld(),
                    sceneManager // ✅ FIX: Pass SceneManager
            ), GameState.RUNNING);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        // "Press ENTER to Start" image with floating effect
        if (pressEnterTexture != null) {
            float textX = (Gdx.graphics.getWidth() - pressEnterTexture.getWidth()) / 2; // Center horizontally
            batch.draw(pressEnterTexture, textX, textY);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (pressEnterTexture != null) {
            pressEnterTexture.dispose();
        }
    }
}
