package io.github.some_example_name.lwjgl3.abstract_engine.movement;

import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementComponent;
import com.badlogic.gdx.physics.box2d.World;
import java.util.List;
import java.util.ArrayList;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;


public class MovementManager {
    private World world;
    private List<MovableEntity> Entities;

    public MovementManager(World world) {
        this.world = world;
        this.Entities = new ArrayList<>();
    }

    public void addEntity(MovableEntity entity) {
        Entities.add(entity);
    }

    public void updateMovement(float deltaTime) {
        for (MovableEntity entity : Entities) {
            MovementComponent movement = entity.getMovementComponent();
            if (movement != null) {
                handlePlayerInput(movement);
                movement.update(deltaTime);
                System.out.println(entity.getEntityName() + " Position: " + movement.getPosition());
            }
        }
    }

    private void handlePlayerInput(MovementComponent movement) {
        float moveForceX = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveForceX = -10f;
            System.out.println("Moving Left");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveForceX = 10f;
            System.out.println("Moving Right");
        }

        System.out.println("Applied Force: " + moveForceX); // Debugging Log

        if (moveForceX == 0) {
            movement.stop();
        } else {
            movement.move(moveForceX);
        }

        if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) && movement.isGrounded()) {
            System.out.println("Jump Triggered!");
            movement.jump();
        }
    }
}
