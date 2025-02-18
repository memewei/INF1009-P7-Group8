package io.github.some_example_name.lwjgl3.abstract_engine.application_classes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.Box2DCollisionListener;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.StaticEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementComponent;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.MenuScene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameScene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;

public class GameMaster extends ApplicationAdapter {
    private SceneManager sceneManager;
    private MovementManager movementManager;
    private World world;
    private Texture backgroundTexture;
    private MovableEntity movableEntity;
    private SpriteBatch batch;
    private EntityManager entityManager;

    public GameMaster() {
        Box2D.init();
        this.world = new World(new com.badlogic.gdx.math.Vector2(0, 0f), true);
        world.setContactListener(new Box2DCollisionListener());

        this.entityManager = new EntityManager(world);
        this.movementManager = new MovementManager(world);
    }

    public void setupGame() {
        movableEntity = new MovableEntity("Player", 100, 100, "player.png", null, null) {
            @Override
            public void onCollision(Entity other) {
                System.out.println("Player collided with " + other.getEntityName());
                IOManager.getInstance().getAudio().playSound("hit_sound.mp3");
            }
        };

        MovementComponent playerMovement = new MovementComponent(world, 100, 100, movableEntity);
        movableEntity.setMovementComponent(playerMovement);

        entityManager.addEntity(movableEntity);
        movementManager.addEntity(movableEntity);

        StaticEntity demon = new StaticEntity("Demon", 0, Gdx.graphics.getHeight() / 2f, "demon.png");
        entityManager.addEntity(demon);
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        // ✅ FIX: Pass World to SceneManager
        sceneManager = new SceneManager(entityManager, movementManager, world);

        // ✅ FIX: Pass SceneManager to MenuScene
        sceneManager.pushScene(new MenuScene(batch, sceneManager));
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0, 0.2f, 1);
        float deltaTime = Gdx.graphics.getDeltaTime();
        world.step(1 / 60f, 6, 2);

        movementManager.updateMovement(deltaTime);
        entityManager.updateEntities(deltaTime);

        batch.begin();
        // ✅ FIX: Only render background if GameScene is active
        if (sceneManager.getCurrentScene() instanceof GameScene && backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        entityManager.render(batch);
        batch.end();

        sceneManager.update(deltaTime);
        sceneManager.render(batch);

        IOManager.getInstance().getDynamicInput().drawInputText();
        IOManager.getInstance().getAudio().playMusic("BgMusic.mp3");

        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        sceneManager.dispose();
        IOManager.getInstance().dispose();
        world.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
