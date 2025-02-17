package io.github.some_example_name.lwjgl3.abstract_engine.application_classes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameScene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.MenuScene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementComponent;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.StaticEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Box2DCollisionListener;

public class GameMaster extends ApplicationAdapter {
    // Uncomment if you want to use your scene management:
    private SceneManager sceneManager;
    private MovementManager movementManager;
    private World world;
    private MovableEntity movableEntity;
    private SpriteBatch batch;
    private EntityManager entityManager; // Manages both dynamic and static entities

    public GameMaster() {
        // Initialize SceneManager
        this.sceneManager = new SceneManager();
        sceneManager.update(new MenuScene());
        Box2D.init();
        // Using (0,0) gravity for a top-down style; adjust if needed.
        this.world = new World(new com.badlogic.gdx.math.Vector2(0, 0f), true);
        // Attach the Box2D collision listener
        world.setContactListener(new Box2DCollisionListener());
        // Initialize the unified EntityManager with the world
        this.entityManager = new EntityManager(world);
        // Initialize the MovementManager for dynamic entities
        this.movementManager = new MovementManager(world);
    }

    public void setupGame() {
        // Create the player MovableEntity.
        // First, create the entity without a MovementComponent.
        movableEntity = new MovableEntity("Player", 100, 100, "player.png", null, null) {
            @Override
            public void onCollision(Entity other) {
                System.out.println("Player collided with " + other.getEntityName());
            }
        };
        // Now create the MovementComponent for the player,
        // passing the world, initial position, and the movableEntity as owner.
        MovementComponent playerMovement = new MovementComponent(world, 100, 100, movableEntity);
        // Set the movement component on the player (requires a setter in MovableEntity)
        movableEntity.setMovementComponent(playerMovement);

        // Add the player to both managers
        entityManager.addEntity(movableEntity);
        movementManager.addEntity(movableEntity);

        // Create a static entity (e.g., a Demon) positioned at x=0 and vertically
        // centered.
        StaticEntity demon = new StaticEntity("Demon", 0, Gdx.graphics.getHeight() / 2f, "demon.png");
        entityManager.addEntity(demon);
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        IOManager.getInstance(); // Initializes the IOManager and sets up input
        Gdx.input.setInputProcessor(IOManager.getInstance().getDynamicInput());
        setupGame();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0, 0.2f, 1);
        // Step the Box2D world
        world.step(1 / 60f, 6, 2);
        float deltaTime = Gdx.graphics.getDeltaTime();
        // Update input/movement for dynamic entities
        movementManager.updateMovement(deltaTime);
        // Update all entities (e.g., for position syncing)
        entityManager.updateEntities(deltaTime);

        batch.begin();
        // Render all entities via the EntityManager
        entityManager.render(batch);
        batch.end();

        IOManager.getInstance().getDynamicInput().drawInputText();
        IOManager.getInstance().getAudio().playMusic("BgMusic.mp3");

        sceneManager.update(new GameScene());

        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        sceneManager.dispose();
        IOManager.getInstance().dispose();
        world.dispose();
        // Dispose of entities if needed
    }
}
