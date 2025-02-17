package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class Scene implements Disposable {
    // Method for initializing scene-specific resources
    public abstract void initialize();

    // Method to update the scene 
    public abstract void update(float deltaTime);

    // Method for rendering the scene
    public abstract void render(SpriteBatch batch);

    @Override
    public abstract void dispose();
}