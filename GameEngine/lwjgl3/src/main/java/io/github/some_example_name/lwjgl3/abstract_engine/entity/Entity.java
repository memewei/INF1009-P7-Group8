package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public abstract class Entity {
    protected final String entityID;
    protected String entityName;
    protected float positionX;
    protected float positionY;
    protected Texture texture;
    private Map<String, Object> components;  //Stores components

    public Entity(String entityName, float positionX, float positionY, String texturePath) {
        this.entityID = UUID.randomUUID().toString();
        this.entityName = entityName;
        this.positionX = positionX;
        this.positionY = positionY;
        this.components = new HashMap<>();
        this.texture = new Texture(Gdx.files.internal(texturePath)); // Load texture
    }
    
    public Entity(String entityName) {
        this.entityID = UUID.randomUUID().toString();
        this.entityName = entityName;
    }

    public void addComponent(String key, Object value) {
        components.put(key, value);
    }

    public Object getComponent(String key) {
        return components.getOrDefault(key, null);
    }

    public void removeComponent(String key) {
        components.remove(key);
    }

    public Map<String, Object> getAllComponents() {
        return components;
    }

    public abstract void update(float deltaTime);
    public abstract void render(SpriteBatch batch);

    // Return texture width and height instead of hardcoded values
    public int getWidth() {
        return texture.getWidth();
    }

    public int getHeight() {
        return texture.getHeight();
    }

    public String getEntityID() {
        return entityID;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Vector2 getPosition() {
        return new Vector2(positionX, positionY);
    }

    public void setPosition(float x, float y) {
        this.positionX = x;
        this.positionY = y;
    }

    public abstract void onCollision(Entity other);

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
