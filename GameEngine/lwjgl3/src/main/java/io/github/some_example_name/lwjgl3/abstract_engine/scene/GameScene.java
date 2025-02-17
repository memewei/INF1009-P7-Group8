package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScene extends Scene {
    private Texture backgroundTexture;
    private SpriteBatch batch;

    public GameScene() {
        backgroundTexture = new Texture("gameScene1.png");
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(backgroundTexture, 0, 0);
        batch.end();
        System.out.println("Rendering Game Scene");
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        batch.dispose();
    }
}