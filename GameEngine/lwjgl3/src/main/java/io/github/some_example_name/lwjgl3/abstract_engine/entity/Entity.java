package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.UUID;

public abstract class Entity {
    protected String entityID;
    protected String entityName;
    protected float positionX;
    protected float positionY;
    protected Texture texture; // Add texture field

    public Entity(String entityName, float positionX, float positionY, String texturePath) {
        this.entityID = UUID.randomUUID().toString();
        this.entityName = entityName;
        this.positionX = positionX;
        this.positionY = positionY;
        this.texture = new Texture(Gdx.files.internal(texturePath)); // Load texture
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

    public void dispose() {
    }
}
