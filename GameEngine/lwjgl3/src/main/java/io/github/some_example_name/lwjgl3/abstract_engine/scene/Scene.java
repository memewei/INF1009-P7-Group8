package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import io.github.some_example_name.lwjgl3.abstract_engine.event.EventSystem;
import io.github.some_example_name.lwjgl3.abstract_engine.event.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base abstract class for all game scenes.
 * Provides common functionality and lifecycle management.
 */
public abstract class Scene implements Disposable {
    private static final Logger LOGGER = Logger.getLogger(Scene.class.getName());
    
    protected final List<Disposable> managedResources;
    private boolean initialized;
    private boolean active;
    private float timeElapsed;
    private String sceneId;
    
    /**
     * Create a new scene
     */
    public Scene() {
        this.managedResources = new ArrayList<>();
        this.initialized = false;
        this.active = false;
        this.timeElapsed = 0;
    }
    
    /**
     * Create a new scene with the specified ID
     * @param sceneId Unique identifier for the scene
     */
    public Scene(String sceneId) {
        this();
        this.sceneId = sceneId;
    }
    
    /**
     * Initialize the scene and its resources.
     * This is called once before the scene becomes active.
     */
    public abstract void initialize();
    
    /**
     * Notification that the scene is about to become active
     */
    public void onActivate() {
        active = true;
        LOGGER.log(Level.INFO, "Scene activated: {0}", sceneId);
        
        // Trigger scene activation event
        if (sceneId != null) {
            EventSystem.getInstance().triggerEvent(
                new GameEvent("scene_activated").setParameter("sceneId", sceneId)
            );
        }
    }
    
    /**
     * Notification that the scene is about to become inactive
     */
    public void onDeactivate() {
        active = false;
        LOGGER.log(Level.INFO, "Scene deactivated: {0}", sceneId);
        
        // Trigger scene deactivation event
        if (sceneId != null) {
            EventSystem.getInstance().triggerEvent(
                new GameEvent("scene_deactivated").setParameter("sceneId", sceneId)
            );
        }
    }
    
    /**
     * Update the scene
     * @param deltaTime Time since last update
     */
    public abstract void update(float deltaTime);
    
    /**
     * Pre-update hook called before the main update.
     * Default implementation updates the elapsed time.
     * @param deltaTime Time since last update
     */
    public void preUpdate(float deltaTime) {
        timeElapsed += deltaTime;
    }
    
    /**
     * Post-update hook called after the main update.
     * @param deltaTime Time since last update
     */
    public void postUpdate(float deltaTime) {
        // Default implementation does nothing
    }
    
    /**
     * Base implementation for the update cycle.
     * This calls preUpdate, update, and postUpdate.
     * @param deltaTime Time since last update
     */
    public final void updateScene(float deltaTime) {
        preUpdate(deltaTime);
        update(deltaTime);
        postUpdate(deltaTime);
    }
    
    /**
     * Render the scene
     * @param batch The SpriteBatch to render with
     */
    public abstract void render(SpriteBatch batch);
    
    /**
     * Pre-render hook called before the main render.
     * @param batch The SpriteBatch to render with
     */
    public void preRender(SpriteBatch batch) {
        // Default implementation does nothing
    }
    
    /**
     * Post-render hook called after the main render.
     * @param batch The SpriteBatch to render with
     */
    public void postRender(SpriteBatch batch) {
        // Default implementation does nothing
    }
    
    /**
     * Base implementation for the render cycle.
     * This calls preRender, render, and postRender.
     * @param batch The SpriteBatch to render with
     */
    public void renderScene(SpriteBatch batch) {
        preRender(batch);
        render(batch);
        postRender(batch);
    }
    
    /**
     * Handle user input.
     * This is called once per frame before update.
     */
    public void handleInput() {
        // Default implementation does nothing
    }
    
    /**
     * Register a resource to be automatically disposed
     * @param resource The resource to manage
     * @param <T> Type of the resource
     * @return The resource (for chaining)
     */
    protected <T extends Disposable> T manage(T resource) {
        if (resource != null) {
            managedResources.add(resource);
        }
        return resource;
    }
    
    /**
     * Set the scene ID
     * @param sceneId Unique identifier for the scene
     */
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    /**
     * Get the scene ID
     */
    public String getSceneId() {
        return sceneId;
    }
    
    /**
     * Get the time elapsed since the scene was initialized
     */
    public float getTimeElapsed() {
        return timeElapsed;
    }
    
    /**
     * Check if the scene has been initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Check if the scene is currently active
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Called when the screen size changes
     * @param width New width
     * @param height New height
     */
    public void resize(int width, int height) {
        LOGGER.log(Level.INFO, "Scene resized: {0}x{1}", new Object[]{width, height});
    }
    
    /**
     * Called when the scene is paused
     */
    public void pause() {
        LOGGER.log(Level.INFO, "Scene paused: {0}", sceneId);
    }
    
    /**
     * Called when the scene is resumed
     */
    public void resume() {
        LOGGER.log(Level.INFO, "Scene resumed: {0}", sceneId);
    }
    
    /**
     * Mark the scene as initialized.
     * This should be called at the end of the initialize method.
     */
    protected void setInitialized() {
        this.initialized = true;
    }
    
    /**
     * Dispose the scene and all its resources.
     * This automatically disposes all managed resources.
     */
    @Override
    public void dispose() {
        // Dispose all managed resources
        for (Disposable resource : managedResources) {
            if (resource != null) {
                try {
                    resource.dispose();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error disposing resource: " + e.getMessage(), e);
                }
            }
        }
        managedResources.clear();
        
        LOGGER.log(Level.INFO, "Scene disposed: {0}", sceneId);
    }
}