package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;

public class StaticEntity extends Entity implements Collidable {
    private Map<String, String> componentData;

    public StaticEntity(String entityName, float positionX, float positionY, String texturePath) {
        super(entityName, positionX, positionY, texturePath);
        this.componentData = new HashMap<>();
    }
    
    public StaticEntity(String entityName) {
        super(entityName);
        this.componentData = new HashMap<>();
    }

    @Override
    public void addComponent(String key, String value) {
        componentData.put(key, value);
    }
    
    @Override
    public String getComponent(String key) {
        if (componentData.containsKey(key)) {
            return componentData.get(key);
        } else if (componentData.containsKey("Value")) {
            return componentData.get("Value");
        }
        return null;
    }

    public void removeComponent(String key) {
        componentData.remove(key);
    }


    @Override
    public void update(float deltaTime) {
        // Placeholder for future logic
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, positionX, positionY, getWidth(), getHeight());
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
