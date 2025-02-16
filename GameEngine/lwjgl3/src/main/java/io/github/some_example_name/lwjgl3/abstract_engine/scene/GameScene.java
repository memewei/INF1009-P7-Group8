package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class GameScene extends Scene {
    private List<Entity> entityComponents;
    private Texture backgroundTexture;

    public GameScene(int sceneID, String backgrounTexturePath) {
        super(sceneID);
        this.entityComponents = new ArrayList<>();
        this.backgroundTexture = new Texture(backgrounTexturePath);
    }

    @Override
    public void update() {
        for (Entity entity : entityComponents) {
            entity.update();
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0.4f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.graphics.begin();
        Gdx.graphics.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.graphics.end();

        for (Entity entity : entityComponents) {
            entity.render();
        }    
    }

    @Override
    public void handleInput(String userInput) {
        // Handle input for game entities
    }

    @Override
    public void dispose() {
        for (Entity entity : entityComponents) {
            entity.dispose();
        }
        backgroundTexture.dispose();
    }

    public void addEntity(Entity entity) {
        entityComponents.add(entity);
    }
}
