package io.github.some_example_name.lwjgl3.abstract_engine.application_classes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
//import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameScene;
//import io.github.some_example_name.lwjgl3.abstract_engine.scene.MenuScene;
//import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManagement;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementComponent;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.StaticEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Box2DCollisionListener;

public class GameMaster extends ApplicationAdapter {
    // Uncomment if you want to use your scene management:
    // private SceneManagement sceneManager;
    private MovementManager movementManager;
    private MovementComponent movementComponent;
    private World world;
    private MovableEntity movableEntity;
    private SpriteBatch batch;
    private EntityManager entityManager; // Now manages both dynamic and static entities

    public GameMaster() {
        // Uncomment if you want to initialize scene management:
        // this.sceneManager = new SceneManagement();
        Box2D.init();
        this.world = new World(new com.badlogic.gdx.math.Vector2(0, 0f), true);
        // Attach the Box2D collision listener for built-in collision detection.
        world.setContactListener(new Box2DCollisionListener());
        // Initialize the unified EntityManager with the Box2D world
        this.entityManager = new EntityManager(world);
        // You can still initialize the MovementManager if needed for dynamic entities
        this.movementManager = new MovementManager(world);
    }

    // Uncomment and modify this method if you decide to implement scene management.
    /*
     * public void manageGameScenes() {
     * // Scene management logic here...
     * }
     */

    public void setupGame() {
        // Create the player's MovementComponent
        MovementComponent playerMovement = new MovementComponent(world, 0, 0);

        // Create the player entity using that MovementComponent
        movableEntity = new MovableEntity("Player", 0, 0, "player.png", null, playerMovement) {
            @Override
            public void onCollision(Entity other) {
                System.out.println("Player collided with " + other.getEntityName());
            }
        };

        // Add the player to both managers
        entityManager.addEntity(movableEntity);
        movementManager.addEntity(movableEntity); // This is key!

        // Create a static entity (e.g., a Demon)
        StaticEntity demon = new StaticEntity("Demon", 0, Gdx.graphics.getHeight() / 2f, "demon.png");
        entityManager.addEntity(demon);
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        IOManager.getInstance(); // initializes the IOManager and sets up input
        Gdx.input.setInputProcessor(IOManager.getInstance().getDynamicInput());
        setupGame();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0, 0.2f, 1);

        // Update Box2D physics
        world.step(1 / 60f, 6, 2);
        float deltaTime = Gdx.graphics.getDeltaTime();
        // Update dynamic entities (if needed) via movementManager or EntityManager
        movementManager.updateMovement(deltaTime);
        entityManager.updateEntities(deltaTime);

        batch.begin();
        // Render all entities via the EntityManager
        entityManager.render(batch);
        batch.end();

        IOManager.getInstance().getDynamicInput().drawInputText();
        IOManager.getInstance().getAudio().playMusic("BgMusic.mp3");

        // Uncomment these if you re-enable scene management:
        // sceneManager.switchScene("MainGame");
        // sceneManager.updateCurrentScene();
        // sceneManager.renderCurrentScene();

        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        IOManager.getInstance().dispose();
        world.dispose();
        // Dispose each entity if necessary (or let EntityManager handle it)
    }
}
