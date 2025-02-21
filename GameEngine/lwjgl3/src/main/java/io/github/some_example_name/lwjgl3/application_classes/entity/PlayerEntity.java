// PlayerEntity.java
package io.github.some_example_name.lwjgl3.application_classes.entity;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;

public class PlayerEntity extends MovableEntity {
    public PlayerEntity(String entityName, float positionX, float positionY, String texturePath) {
        super(entityName, positionX, positionY, texturePath);
    }

    @Override
    public void move(float forceX, float forceY) {
        if (movementComponent != null) {
            movementComponent.move(forceX, forceY);
            // System.out.println(getEntityName() + " is moving with force (" + forceX + ", " + forceY + ")");
        }
    }

    @Override
    public void stop() {
        if (movementComponent != null) {
            movementComponent.stop();
            // System.out.println(getEntityName() + " has stopped moving.");
        }
    }
}