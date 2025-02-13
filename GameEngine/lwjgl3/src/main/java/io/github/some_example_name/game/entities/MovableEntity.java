package io.github.some_example_name.game.entities;

import io.github.some_example_name.game.physics.Collidable;
import com.badlogic.gdx.graphics.Texture;

public abstract class MovableEntity extends Entity implements Collidable {
    protected float velocityX, velocityY;
    private float direction;
    private StaticEntity linkedStatic;

    public MovableEntity(String entityName, float positionX, float positionY, String texturePath, StaticEntity linkedStatic) {
        super(entityName, positionX, positionY, texturePath);
        this.velocityX = 0;
        this.velocityY = 0;
        this.linkedStatic = linkedStatic;
    }

    public void move(float dx, float dy) {
        this.positionX += dx;
        this.positionY += dy;
    }

    public abstract void onCollision(Entity other);

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

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public StaticEntity getLinkedImmovable() {
        return linkedStatic;
    }
}
