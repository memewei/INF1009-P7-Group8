package io.github.some_example_name.lwjgl3.abstract_engine.movement;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.World;
import io.github.some_example_name.lwjgl3.abstract_engine.io.InputHandler;
import java.util.List;
import java.util.ArrayList;

public class MovementManager {
    private World world;
    private List<IMovable> entities;
    private InputHandler inputHandler;  // InputHandler to be injected

    // Constructor now accepts an InputHandler
    public MovementManager(World world, InputHandler inputHandler) {
        this.world = world;
        this.entities = new ArrayList<>();
        this.inputHandler = inputHandler;  // Set the inputHandler
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

        // Use the inputHandler to check for key presses
        if (inputHandler.isKeyPressed(Input.Keys.LEFT) || inputHandler.isKeyPressed(Input.Keys.A)) {
            forceX = -50f;
            // System.out.println("Moving Left");
        }
        if (!inputHandler.isKeyPressed(Input.Keys.RIGHT) && !inputHandler.isKeyPressed(Input.Keys.D)) {
            // no movement
        } else {
            forceX = 50f;
            // System.out.println("Moving Right");
        }
        if (inputHandler.isKeyPressed(Input.Keys.UP) || inputHandler.isKeyPressed(Input.Keys.W)) {
            forceY = 50f;
            // System.out.println("Moving Up");
        }
        if (inputHandler.isKeyPressed(Input.Keys.DOWN) || inputHandler.isKeyPressed(Input.Keys.S)) {
            forceY = -50f;
            // System.out.println("Moving Down");
        }

        // Apply movement
        if (forceX == 0 && forceY == 0) {
            entity.stop();
        } else {
            entity.move(forceX, forceY);
        }
    }
}
