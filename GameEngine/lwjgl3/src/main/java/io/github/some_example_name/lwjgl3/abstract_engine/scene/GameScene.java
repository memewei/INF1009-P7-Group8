package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.PlayerEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementComponent;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.StaticEntity;
// import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

public class GameScene extends Scene {
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private World world;
    private MovableEntity player;
    private SceneManager sceneManager;

    public GameScene(SpriteBatch batch, EntityManager entityManager, MovementManager movementManager, World world,
            SceneManager sceneManager) {
        this.batch = batch;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.world = world;
        this.sceneManager = sceneManager; //Store SceneManager
    }

    @Override
public void initialize() {
    System.out.println("[GameScene] Initializing...");

    if (backgroundTexture == null) {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("gameScene1.png"));
            System.out.println("[GameScene] Background loaded.");
        } catch (Exception e) {
            System.err.println("[GameScene] Error loading background: " + e.getMessage());
        }
    }

    // Check if player exists by a unique ID, for example "Player"
    player = (PlayerEntity) entityManager.getEntityByID("Player");
    if (player == null) {
        System.out.println("[GameScene] Creating new Player entity...");
        player = new PlayerEntity("Player", 100, 100, "player.png");
        MovementComponent playerMovement = new MovementComponent(world, 100, 100, player);
        player.setMovementComponent(playerMovement);
        entityManager.addEntity(player);
        movementManager.addEntity(player);
    } else {
        System.out.println("[GameScene] Player already exists. Resuming game...");
    }

    if (!entityManager.hasEntity("Demon")) {
        System.out.println("[GameScene] Adding static entity (Demon)...");
        StaticEntity demon = new StaticEntity("Demon", 0, Gdx.graphics.getHeight() / 2f, "demon.png");
        entityManager.addEntity(demon);
    }
}

    @Override
    public void update(float deltaTime) {
        // ✅ FIX: Now sceneManager is accessible
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("Pausing game...");
            sceneManager.pushScene(new PauseScene(batch, sceneManager), GameState.PAUSED);
        }

        if (sceneManager.getGameState() == GameState.RUNNING) {
            movementManager.updateMovement(deltaTime);
            entityManager.updateEntities(deltaTime);
        }
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
