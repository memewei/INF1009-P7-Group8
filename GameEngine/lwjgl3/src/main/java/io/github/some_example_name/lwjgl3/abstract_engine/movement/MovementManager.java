package io.github.some_example_name.lwjgl3.abstract_engine.movement;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.World;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import java.util.List;
import java.util.ArrayList;

public class MovementManager {
    private World world;
    private List<IMovable> entities;
    private IOManager ioManager;

    public MovementManager(World world) {
        this.world = world;
        this.entities = new ArrayList<>();
        this.ioManager = IOManager.getInstance(); // Cache IOManager
    }

    public void addEntity(IMovable entity) {
        entities.add(entity);
    }

    public void updateMovement(float deltaTime) {
        for (IMovable entity : entities) {
            handlePlayerInput(entity);

            if (entity instanceof MovementComponent) {
                MovementComponent movement = (MovementComponent) entity;
                movement.update(deltaTime);
            }
        }
    }

    private void handlePlayerInput(IMovable entity) {
        float forceX = 0;
        float forceY = 0;

        if (ioManager.getDynamicInput().isKeyPressed(Input.Keys.LEFT) ||
                ioManager.getDynamicInput().isKeyPressed(Input.Keys.A)) {
            forceX = -50f;
        }
        if (ioManager.getDynamicInput().isKeyPressed(Input.Keys.RIGHT) ||
                ioManager.getDynamicInput().isKeyPressed(Input.Keys.D)) {
            forceX = 50f;
        }
        if (ioManager.getDynamicInput().isKeyPressed(Input.Keys.UP) ||
                ioManager.getDynamicInput().isKeyPressed(Input.Keys.W)) {
            forceY = 50f;
        }
        if (ioManager.getDynamicInput().isKeyPressed(Input.Keys.DOWN) ||
                ioManager.getDynamicInput().isKeyPressed(Input.Keys.S)) {
            forceY = -50f;
        }

        // System.out.println("Applied Force: (" + forceX + ", " + forceY + ")");

        if (forceX == 0 && forceY == 0) {
            entity.stop();
        } else {
            entity.move(forceX, forceY);
        }
    }
}
