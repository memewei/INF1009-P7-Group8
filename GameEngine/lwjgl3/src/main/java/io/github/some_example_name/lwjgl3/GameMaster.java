package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.utils.ScreenUtils;

public class GameMaster extends ApplicationAdapter {
    private SceneManagement sceneManager;

    public SceneMaster() {
        this.sceneManager = new SceneManagement();
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
        IOManager.getInstance();  // initializes the IOManager and sets up input

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0, 0.2f, 1);
        // to display inputs at top right corner
        IOManager.getInstance().getDynamicInput().drawInputText();
        //play music when opened
        IOManager.getInstance().getAudio().playMusic("BgMusic.mp3");
        super.render();
    }

    @Override
    public void dispose() {
        IOManager.getInstance().dispose();
    }
}
