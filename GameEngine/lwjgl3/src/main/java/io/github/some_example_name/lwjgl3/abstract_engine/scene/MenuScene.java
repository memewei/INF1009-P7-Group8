package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MenuScene extends Scene {
    private Texture backgroundTexture;
    private SpriteBatch batch;

    public MenuScene() {
        super();
    }

    @Override
    public void initialize() {
        batch = new SpriteBatch();
        try {
            backgroundTexture = new Texture(Gdx.files.internal("menuScene1.png"));
        } catch (Exception e) {
            System.err.println("Error loading menu texture: " + e.getMessage());
            // Default texture in case of failure
            backgroundTexture = new Texture(Gdx.files.internal("defaultMenu.png"));
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
        if (batch != null) {
            batch.dispose();
        }
    }

    @Override
    public void update(float deltaTime){};
}
