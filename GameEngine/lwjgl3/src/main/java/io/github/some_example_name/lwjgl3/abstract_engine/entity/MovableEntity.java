package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.IMovable;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementComponent;

public abstract class MovableEntity extends Entity implements Collidable, IMovable {
	private List<StaticEntity> components;
    protected MovementComponent movementComponent;

    public MovableEntity(String entityName, float positionX, float positionY, String texturePath) {
        super(entityName, positionX, positionY, texturePath);
        this.components = new ArrayList<>();
    }
    
    public void setMovementComponent(MovementComponent movementComponent) {
        this.movementComponent = movementComponent;
    }

    public MovementComponent getMovementComponent() {
        return movementComponent;
    }

    @Override
    public Vector2 getPosition() {
        return movementComponent != null ? movementComponent.getPosition() : new Vector2(positionX, positionY);
    }

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
        batch.draw(texture, positionX, positionY, getWidth(), getHeight());
    }

    // Abstract so that concrete subclasses must implement their own move logic.
    public abstract void move(float forceX, float forceY);

    @Override
    public void onCollision(Entity other) {
        System.out.println(getEntityName() + " collided with " + other.getEntityName());
    }
    @Override
    public void addComponent(String key, String value) {
        for (StaticEntity comp : components) {
            if (comp.getEntityName().equals(key)) {
                comp.addComponent("Value", value);
                return;
            }
        }
        StaticEntity component = new StaticEntity(key);
        component.addComponent("Value", value);
        components.add(component);
    }
    
    @Override
    public String getComponent(String key) {
        for (StaticEntity comp : components) {
            if (comp.getEntityName().equals(key)) {
                return comp.getComponent(key);
            }
        }
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
