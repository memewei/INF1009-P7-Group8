package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScene extends Scene {
    private Texture backgroundTexture;
    private SpriteBatch batch;

    public GameScene() {
        super();
        // Initialize resources here if needed
    }

    @Override
    public void initialize() {
        // Initialize resources specifically for this scene
        batch = new SpriteBatch();
        try {
            backgroundTexture = new Texture(Gdx.files.internal("gameScene1.png"));
        } catch (Exception e) {
            System.err.println("Error loading texture for GameScene: " + e.getMessage());
        }
    }

    @Override
    public void update(float deltaTime) {
        // Handle any dynamic updates, such as movement, input, etc.
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
        // Dispose resources for this scene
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }
}