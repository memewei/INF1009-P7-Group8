package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.physics.box2d.World;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;

public class SceneManager implements Disposable {
    private Stack<Scene> sceneStack = new Stack<>();
    private float transitionAlpha = 0f;
    private boolean transitioning = false;
    private Scene currentScene;
    private Texture transitionTexture;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private World world; // ✅ FIX: Store World reference

    // ✅ FIX: Update constructor to accept World
    public SceneManager(EntityManager entityManager, MovementManager movementManager, World world) {
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.world = world; // ✅ Store World instance
        transitionTexture = new Texture(Gdx.files.internal("transitionFade.png"));
    }

    // ✅ FIX: Provide access to World
    public World getWorld() {
        return world;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public MovementManager getMovementManager() {
        return movementManager;
    }

    public void update(float deltaTime) {
        if (currentScene != null) {
            currentScene.update(deltaTime);
        }
    }
    
    public void render(SpriteBatch batch) {
        if (currentScene != null) {
            currentScene.render(batch);
        }
    }    

    public void pushScene(Scene scene) {
        if (currentScene != null) {
            sceneStack.push(currentScene);
        }
        currentScene = scene;
        currentScene.initialize();
    }

    public void popScene() {
        if (!sceneStack.isEmpty()) {
            currentScene.dispose();
            currentScene = sceneStack.pop();
            currentScene.initialize();
        }
    }

    public void changeScene(Scene newScene) {
        if (currentScene != null) {
            currentScene.dispose();
        }
        newScene.initialize();
        currentScene = newScene;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    @Override
    public void dispose() {
        if (currentScene != null) {
            currentScene.dispose();
        }
        if (transitionTexture != null) {
            transitionTexture.dispose();
        }
    }
}
