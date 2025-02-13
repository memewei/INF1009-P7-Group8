package io.github.some_example_name.game.entities;

import java.util.HashMap;
import java.util.Map;

import io.github.some_example_name.game.physics.Collidable;
import com.badlogic.gdx.graphics.Texture;

public class StaticEntity extends Entity implements Collidable {
    private Map<String, String> componentData;

    public StaticEntity(String entityName, float positionX, float positionY, String texturePath) {
        super(entityName, positionX, positionY, texturePath);
        this.componentData = new HashMap<>();
    }

    public void setComponent(String key, String value) {
        componentData.put(key, value);
    }

    public String getComponent(String key) {
        return componentData.getOrDefault(key, null);
    }

    public void removeComponent(String key) {
        componentData.remove(key);
    }

    @Override
    public void update(float deltaTime) {
        // Placeholder for future logic
    }

    @Override
    public void render() {
        // Placeholder for future logic
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
