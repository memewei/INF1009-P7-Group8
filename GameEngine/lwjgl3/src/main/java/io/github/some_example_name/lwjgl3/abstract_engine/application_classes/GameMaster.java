package io.github.some_example_name.lwjgl3.abstract_engine.application_classes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
//import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameScene;
//import io.github.some_example_name.lwjgl3.abstract_engine.scene.MenuScene;
//import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManagement;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementComponent;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class GameMaster extends ApplicationAdapter {
//    private SceneManagement sceneManager;
    private MovementManager movementManager;
    private World world;
    private MovableEntity movableEntity;
    private SpriteBatch batch;

    public GameMaster() {
//        this.sceneManager = new SceneManagement();
        Box2D.init();
        this.world = new World(new com.badlogic.gdx.math.Vector2(0,-9.8f),true);
        this.movementManager = new MovementManager(world);
    }

//    public void manageGameScenes() {
//        Player player = new Player("Hero");
//        Enemy enemy1 = new Enemy("Goblin");
//        Enemy enemy2 = new Enemy("Orc");
//
//        GameScene gameScene = new GameScene("MainGame", player);
//        gameScene.addEnemy(enemy1);
//        gameScene.addEnemy(enemy2);
//
//        MenuScene menuScene = new MenuScene("MainMenu", Arrays.asList("Start", "Load", "Exit"));
//
//        sceneManager.addScene(gameScene.getName(), gameScene);
//        sceneManager.addScene(menuScene.getName(), menuScene);
//
//        sceneManager.switchScene(gameScene.getName());
//        sceneManager.updateCurrentScene();
//        sceneManager.renderCurrentScene();
//
//        menuScene.handleInput("Start");
//    }

    public void setupGame() {
        movableEntity = new MovableEntity("Player", 0, 0, "player.png", null, new MovementComponent(world, 0, 0)) {
            @Override
            public void onCollision(Entity other) {

            }
        };
        movementManager.addEntity(movableEntity);
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        IOManager.getInstance();// initializes the IOManager and sets up input
        Gdx.input.setInputProcessor(IOManager.getInstance().getDynamicInput());
        setupGame();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0, 0.2f, 1);

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
