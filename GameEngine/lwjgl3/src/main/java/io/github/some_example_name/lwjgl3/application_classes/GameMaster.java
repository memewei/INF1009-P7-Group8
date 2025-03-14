package io.github.some_example_name.lwjgl3.application_classes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.Box2DCollisionListener;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameState;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;
import io.github.some_example_name.lwjgl3.application_classes.scene.HealthSnakeMenuScene;

public class GameMaster extends ApplicationAdapter {
    private SceneManager sceneManager;
    private MovementManager movementManager;
    private World world;
    private SpriteBatch batch;
    private EntityManager entityManager;

    public GameMaster() {
        Box2D.init();
        this.world = new World(new com.badlogic.gdx.math.Vector2(0, 0f), true);
        world.setContactListener(new Box2DCollisionListener());

        this.entityManager = new EntityManager(world);
        this.movementManager = new MovementManager(world);
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Ensure IOManager is initialized
        IOManager.getInstance().init();

        // Initialize scene manager
        sceneManager = new SceneManager(world);
        
        // Load our health snake menu scene
        sceneManager.pushScene(new HealthSnakeMenuScene(
                batch, 
                sceneManager, 
                entityManager, 
                movementManager), 
            GameState.MAIN_MENU);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Update world physics if the game is running
        if (sceneManager.getGameState() == GameState.RUNNING) {
            world.step(1 / 60f, 6, 2);
        }

        // Scene updates and rendering are handled by the SceneManager
        sceneManager.update(deltaTime);
        sceneManager.render(batch);
        
        // Show input feedback
        IOManager.getInstance().getDynamicInput().drawInputText();

        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        sceneManager.dispose();
        IOManager.getInstance().dispose();
        world.dispose();
    }
}