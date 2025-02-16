package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

public class GameScene extends Scene {
    private SpriteBatch batch;

    public GameScene(int sceneID) {
        super(sceneID);
        this.batch = new SpriteBatch();
    }

    @Override
    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        for (Entity entity : entityComponents) {
            entity.update(deltaTime);
        }
    }

    @Override
    public void render() {
        batch.begin();
        for (Entity entity : entityComponents) {
            entity.render(batch);
        }
        batch.end();
    }

    @Override
    public void handleInput(String userInput) {
        for (Entity entity : entityComponents) {}
    }

    @Override
    public void dispose() {
        for (Entity entity : entityComponents) {
            entity.dispose();
        }
        batch.dispose();
    }
}
