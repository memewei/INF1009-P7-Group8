package io.github.some_example_name.lwjgl3.abstract_engine.application_classes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Box2DCollisionListener;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
//import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameScene;
//import io.github.some_example_name.lwjgl3.abstract_engine.scene.MenuScene;
//import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManagement;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementComponent;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

public class GameMaster extends ApplicationAdapter {
    // Uncomment these if you want to use your scene management later:
    // private SceneManagement sceneManager;
    private MovementManager movementManager;
    private MovementComponent movementComponent;
    private World world;
    private MovableEntity movableEntity;
    private SpriteBatch batch;

    public GameMaster() {
        // Uncomment if you want to initialize scene management:
        // this.sceneManager = new SceneManagement();
        Box2D.init();
        this.world = new World(new com.badlogic.gdx.math.Vector2(0, 0f), true);
        // Attach the Box2D collision listener for built-in collision detection.
        world.setContactListener(new Box2DCollisionListener());
        this.movementManager = new MovementManager(world);
    }

    // Uncomment and modify this method if you decide to implement scene management.
    /*
    public void manageGameScenes() {
        Player player = new Player("Hero");
        Enemy enemy1 = new Enemy("Goblin");
        Enemy enemy2 = new Enemy("Orc");

        GameScene gameScene = new GameScene("MainGame", player);
        gameScene.addEnemy(enemy1);
        gameScene.addEnemy(enemy2);

        MenuScene menuScene = new MenuScene("MainMenu", Arrays.asList("Start", "Load", "Exit"));

        sceneManager.addScene(gameScene.getName(), gameScene);
        sceneManager.addScene(menuScene.getName(), menuScene);

        sceneManager.switchScene(gameScene.getName());
        sceneManager.updateCurrentScene();
        sceneManager.renderCurrentScene();

        menuScene.handleInput("Start");
    }
    */

    public void setupGame() {
        movementComponent = new MovementComponent(world, 0, 0);
        movableEntity = new MovableEntity("Player", 0, 0, "player.png", null, new MovementComponent(world, 0, 0)) {
            @Override
            public void onCollision(Entity other) {
                System.out.println("Player collided with " + other.getEntityName());
            }
        };
        movementManager.addEntity(movableEntity);
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
        movementManager.updateMovement(deltaTime);

        batch.begin();
        if (movableEntity != null) {
            System.out.println("Player Position: " + movableEntity.getPosition());
            movableEntity.render(batch);
        } else {
            System.out.println("Warning: movableEntity is NULL!");
        }
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
        if (movableEntity != null) {
            movableEntity.dispose();
        }
    }
}
