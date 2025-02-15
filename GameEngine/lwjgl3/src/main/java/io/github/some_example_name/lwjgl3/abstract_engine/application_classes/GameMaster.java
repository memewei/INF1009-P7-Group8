package io.github.some_example_name.lwjgl3.abstract_engine.application_classes;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameScene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.MenuScene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManagement;
//import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import java.util.Arrays;


public class GameMaster extends ApplicationAdapter {
    private SceneManagement sceneManager;
//    private MovementManager movementManager;
//    private World world;
//    private MovableEntity movableEntity;

    public GameMaster() {
        this.sceneManager = new SceneManagement();
//        Box2D.init();
//        this.world = new World(new com.badlogic.gdx.math.Vector2(0,-9.8f),true);
//        this.movementManager = new MovementManager(world);
    }

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

    @Override
    public void create() {
        IOManager.getInstance();// initializes the IOManager and sets up input

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0, 0.2f, 1);

//        world.step(1/ 60f,6,2);
//        float deltaTime = com.badlogic.gdx.Gdx.graphics.getDeltaTime();
//        movementManager.updateMovement(deltaTime);

//        playerEntity.printPosition();

        // to display inputs at top right corner
        IOManager.getInstance().getDynamicInput().drawInputText();
        //play music when opened
        IOManager.getInstance().getAudio().playMusic("BgMusic.mp3");
        super.render();
    }

    @Override
    public void dispose() {
        IOManager.getInstance().dispose();
//        world.dispose();
    }
}
