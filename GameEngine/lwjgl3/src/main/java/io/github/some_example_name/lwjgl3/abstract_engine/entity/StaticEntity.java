package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;

public class StaticEntity extends Entity implements Collidable {
    public StaticEntity(String entityName, float positionX, float positionY, String texturePath) {
        super(entityName, positionX, positionY, texturePath);
    }

    public StaticEntity(String entityName, String texturePath) {
        super(entityName, 0, 0, texturePath);  // Temporary (0,0) position
        System.err.println("[WARNING] StaticEntity `" + entityName + "` initialized without a position. Call setPosition()!");
    }

    @Override
    public void update(float deltaTime) {
        // Placeholder for future logic
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {  //Prevent null reference crashes
            batch.draw(texture, positionX, positionY, getWidth(), getHeight());
        }
    }

    @Override
    public int getWidth() {
        return texture.getWidth();
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }

    @Override
    public void onCollision(Entity other) {
        System.out.println("Static object collided with: " + other.getClass().getSimpleName());
    }
}
