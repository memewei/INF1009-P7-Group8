package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.IMovable;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementComponent;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class MovableEntity extends Entity implements Collidable, IMovable {
    protected float velocityX, velocityY;
    private float direction;
    private StaticEntity linkedStatic;
    private MovementComponent movementComponent;

    public MovableEntity(String entityName, float positionX, float positionY, String texturePath, StaticEntity linkedStatic, MovementComponent movementComponent) {
        super(entityName, positionX, positionY, texturePath);
        this.velocityX = 0;
        this.velocityY = 0;
        this.linkedStatic = linkedStatic;
        this.movementComponent = movementComponent;
    }

    public MovementComponent getMovementComponent() {
        return movementComponent;
    }

    @Override
    public Vector2 getPosition() {
        return movementComponent != null ? movementComponent.getPosition() : new Vector2(positionX, positionY);
    }

    public abstract void onCollision(Entity other);

    @Override
    public void update(float deltaTime) {
        if (movementComponent != null) {
            movementComponent.update(deltaTime);
            Vector2 updatedPosition = movementComponent.getPosition();
            this.positionX = updatedPosition.x;
            this.positionY = updatedPosition.y;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Vector2 updatedPosition = getPosition(); // Get updated position
        batch.draw(texture, updatedPosition.x, updatedPosition.y, getWidth() * 0.2f, getHeight() * 0.2f);
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
    public void move(float forceX) {
        if (movementComponent != null) {
            movementComponent.move(forceX);
        }
    }

    @Override
    public void jump() {
        if (movementComponent != null) {
            movementComponent.jump();
        }
    }

    @Override
    public void stop() {
        if (movementComponent != null) {
            movementComponent.stop();
        }
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
