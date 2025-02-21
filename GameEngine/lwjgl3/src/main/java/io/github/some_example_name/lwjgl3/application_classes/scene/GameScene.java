package io.github.some_example_name.lwjgl3.application_classes.scene;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameState;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;
import io.github.some_example_name.lwjgl3.application_classes.entity.PlayerEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
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
    private boolean hasPrintedDebug = false;

    public GameScene(SpriteBatch batch, EntityManager entityManager, MovementManager movementManager, World world,
            SceneManager sceneManager) {
        this.batch = batch;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.world = world;
        this.sceneManager = sceneManager; // Store SceneManager
    }

    @Override
    public void initialize() {
        System.out.println("[GameScene] Initializing...");

        entityManager.clearEntities();

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
            demon.addComponent("Type", "Enemy");
            demon.addComponent("Health", "100");
            demon.addComponent("AI", "Aggressive");
            entityManager.addEntity(demon);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (sceneManager == null) {
            System.err.println("[GameScene] sceneManager is NULL! Ensure it is initialized properly.");
            return;
        }
        // Ensure ESC key pauses the game only if sceneManager is available
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("[GameScene] Pausing game...");
            sceneManager.pushScene(new PauseScene(batch, sceneManager), GameState.PAUSED);
        }

        if (sceneManager.getGameState() == GameState.RUNNING) {
            movementManager.updateMovement(deltaTime);
            entityManager.updateEntities(deltaTime);
        } else {
            System.out.println("[GameScene] Game is paused");
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

        if (!hasPrintedDebug) {
            debugEntityComponents();
            hasPrintedDebug = true; // Prevent further prints
        }
    }

    private void debugEntityComponents() {
        System.out.println("\n=== Entity Component Debugging ===");
        for (Entity entity : entityManager.getAllEntities()) {
            System.out.println("Entity: " + entity.getEntityName() + " (ID: " + entity.getEntityID() + ")");
            Map<String, Object> components = entity.getAllComponents();

            if (components.isEmpty()) {
                System.out.println("  - No components assigned.");
            } else {
                for (Map.Entry<String, Object> entry : components.entrySet()) {
                    System.out.println("  - " + entry.getKey() + ": " + entry.getValue());
                }
            }
        }
        System.out.println("==================================\n");
    }

    @Override
    public void dispose() {
        System.out.println("[GameScene] Disposing of game entities.");
        entityManager.clearEntities(); // no duplicates for next scene created
    }
}
