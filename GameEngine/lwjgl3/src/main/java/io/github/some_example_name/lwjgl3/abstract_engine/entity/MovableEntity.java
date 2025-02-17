package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.IMovable;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementComponent;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class MovableEntity extends Entity implements Collidable, IMovable {
    protected float velocityX, velocityY;
    private float direction;
    private List<StaticEntity> components;
    private MovementComponent movementComponent;

    public MovableEntity(String entityName, float positionX, float positionY, String texturePath, StaticEntity linkedStatic, MovementComponent movementComponent) {
        super(entityName, positionX, positionY, texturePath);
        this.velocityX = 0;
        this.velocityY = 0;
        this.movementComponent = movementComponent;
        this.components = new ArrayList<>();
    }

    public MovementComponent getMovementComponent() {
        return movementComponent;
    }

    public void setMovementComponent(MovementComponent movementComponent) {
        this.movementComponent = movementComponent;
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
        Vector2 updatedPosition = getPosition();
        // Adjust scaling as needed (change 0.2f to 1.0f for testing visibility)
        batch.draw(texture, updatedPosition.x, updatedPosition.y, getWidth() * 1.0f, getHeight() * 1.0f);
    }

    @Override
    public int getWidth() {
        return texture.getWidth();
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }

    // Updated move method with two arguments
    @Override
    public void move(float forceX, float forceY) {
        if (movementComponent != null) {
            movementComponent.move(forceX, forceY);
        }
    }

    @Override
    public void stop() {
        if (movementComponent != null) {
            movementComponent.stop();
        }
    }
    
    @Override
    public void addComponent(String key, String value) {
        // Not used
    }

    public void addComponent(StaticEntity component) {
        components.add(component);
    }
    
    @Override
    public String getComponent(String key){
    	return null;
    }

    public StaticEntity getStaticComponent(String name) {
        for (StaticEntity comp : components) {
            if (comp.getEntityName().equals(name)) {
                return comp;
            }
        }
        return null;
    }

    public void removeComponent(String name) {
        components.removeIf(comp -> comp.getEntityName().equals(name));
    }
}