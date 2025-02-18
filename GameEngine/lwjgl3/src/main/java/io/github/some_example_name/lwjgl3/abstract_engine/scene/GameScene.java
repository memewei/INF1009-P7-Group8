package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementComponent;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.StaticEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

public class GameScene extends Scene {
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private World world; // ✅ FIX: Store World reference
    private MovableEntity player;

    public GameScene(SpriteBatch batch, EntityManager entityManager, MovementManager movementManager, World world) {
        this.batch = batch;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.world = world; // ✅ FIX: Store World reference
    }

    @Override
    public void initialize() {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("gameScene1.png"));
        } catch (Exception e) {
            System.err.println("Error loading texture for GameScene: " + e.getMessage());
        }

        player = new MovableEntity("Player", 100, 100, "player.png", null, null) {
            @Override
            public void onCollision(Entity other) {
                System.out.println("Player collided with " + other.getEntityName());
            }
        };

        // ✅ FIX: Use the World instance from the constructor
        MovementComponent playerMovement = new MovementComponent(world, 100, 100, player);
        player.setMovementComponent(playerMovement);

        entityManager.addEntity(player);
        movementManager.addEntity(player);

        StaticEntity demon = new StaticEntity("Demon", 0, Gdx.graphics.getHeight() / 2f, "demon.png");
        entityManager.addEntity(demon);
    }

    @Override
    public void update(float deltaTime) {
        movementManager.updateMovement(deltaTime);
        entityManager.updateEntities(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        entityManager.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
