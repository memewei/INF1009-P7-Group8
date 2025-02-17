package io.github.some_example_name.lwjgl3.abstract_engine.movement;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import java.util.List;
import java.util.ArrayList;

public class MovementManager {
    private World world;
    private List<IMovable> entities;

    public MovementManager(World world) {
        this.world = world;
        this.entities = new ArrayList<>();
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

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            forceX = -50f;
            // System.out.println("Moving Left");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            forceX = 50f;
            // System.out.println("Moving Right");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            forceY = 50f;
            // System.out.println("Moving Up");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            forceY = -50f;
            // System.out.println("Moving Down");
        }

        // System.out.println("Applied Force: (" + forceX + ", " + forceY + ")");

        if (forceX == 0 && forceY == 0) {
            entity.stop();
        } else {
            entity.move(forceX, forceY);
        }
    }
}
