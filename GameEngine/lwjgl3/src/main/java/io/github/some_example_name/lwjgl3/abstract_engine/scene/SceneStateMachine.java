package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import io.github.some_example_name.lwjgl3.abstract_engine.event.EventSystem;
import io.github.some_example_name.lwjgl3.abstract_engine.event.GameEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the State pattern for scene management.
 * Provides cleaner transitions and history tracking compared to stack-based approach.
 */
public class SceneStateMachine implements Disposable {
    private static final Logger LOGGER = Logger.getLogger(SceneStateMachine.class.getName());
    
    private final Map<String, Scene> scenes;
    private Scene currentScene;
    private Scene previousScene;
    private String currentSceneId;
    private String previousSceneId;
    private boolean transitioning;
    private float transitionTime;
    private SceneTransition currentTransition;
    
    /**
     * Create a new scene state machine
     */
    public SceneStateMachine() {
        scenes = new HashMap<>();
        transitioning = false;
    }
    
    /**
     * Register a scene with the state machine
     * @param sceneId Unique identifier for the scene
     * @param scene The scene to register
     */
    public void registerScene(String sceneId, Scene scene) {
        if (sceneId == null || scene == null) {
            return;
        }
        
        scenes.put(sceneId, scene);
        LOGGER.log(Level.INFO, "Scene registered: {0}", sceneId);
    }
    
    /**
     * Unregister a scene from the state machine
     * @param sceneId Identifier of the scene to unregister
     */
    public void unregisterScene(String sceneId) {
        if (sceneId == null) {
            return;
        }
        
        // Cannot unregister the current scene
        if (sceneId.equals(currentSceneId)) {
            LOGGER.log(Level.WARNING, "Cannot unregister the current scene: {0}", sceneId);
            return;
        }
        
        Scene scene = scenes.remove(sceneId);
        if (scene != null) {
            scene.dispose();
            LOGGER.log(Level.INFO, "Scene unregistered and disposed: {0}", sceneId);
        }
    }
    
    /**
     * Change to a different scene with optional transition
     * @param sceneId Identifier of the scene to change to
     * @param transition Optional transition effect (null for immediate change)
     * @return true if the change was successful
     */
    public boolean changeScene(String sceneId, SceneTransition transition) {
        if (sceneId == null || !scenes.containsKey(sceneId)) {
            LOGGER.log(Level.WARNING, "Cannot change to scene (not registered): {0}", sceneId);
            return false;
        }
        
        // Don't change if already transitioning
        if (transitioning) {
            LOGGER.log(Level.INFO, "Scene change ignored - already transitioning");
            return false;
        }
        
        // Don't change if already on this scene
        if (sceneId.equals(currentSceneId)) {
            LOGGER.log(Level.INFO, "Already on scene: {0}", sceneId);
            return true;
        }
        
        // Store previous scene
        previousScene = currentScene;
        previousSceneId = currentSceneId;
        
        // Set up new scene
        currentScene = scenes.get(sceneId);
        currentSceneId = sceneId;
        
        // Initialize new scene if needed
        if (currentScene != null && !currentScene.isInitialized()) {
            currentScene.initialize();
        }
        
        // Set up transition if provided
        if (transition != null) {
            transitioning = true;
            transitionTime = 0;
            currentTransition = transition;
            currentTransition.initialize(previousScene, currentScene);
        }
        
        // Notify scene change
        EventSystem.getInstance().triggerEvent(
            GameEvent.createGameStateChangedEvent(currentSceneId, previousSceneId)
        );
        
        LOGGER.log(Level.INFO, "Changed scene from {0} to {1}", new Object[]{previousSceneId, currentSceneId});
        return true;
    }
    
    /**
     * Change to a different scene immediately without transition
     * @param sceneId Identifier of the scene to change to
     * @return true if the change was successful
     */
    public boolean changeScene(String sceneId) {
        return changeScene(sceneId, null);
    }
    
    /**
     * Return to the previous scene
     * @param transition Optional transition effect
     * @return true if the change was successful
     */
    public boolean returnToPreviousScene(SceneTransition transition) {
        if (previousSceneId == null) {
            LOGGER.log(Level.INFO, "No previous scene to return to");
            return false;
        }
        
        return changeScene(previousSceneId, transition);
    }
    
    /**
     * Update the current scene and any active transition
     * @param deltaTime Time since last update
     */
    public void update(float deltaTime) {
        if (transitioning) {
            // Update transition
            transitionTime += deltaTime;
            if (transitionTime >= currentTransition.getDuration()) {
                // Transition complete
                transitioning = false;
                currentTransition = null;
            }
        }
        
        // Update current scene
        if (currentScene != null) {
            currentScene.update(deltaTime);
        }
    }
    
    /**
     * Render the current scene with any active transition
     * @param batch The sprite batch to render with
     */
    public void render(SpriteBatch batch) {
        if (transitioning && currentTransition != null) {
            // Let transition handle rendering
            float progress = transitionTime / currentTransition.getDuration();
            currentTransition.render(batch, progress);
        } else if (currentScene != null) {
            // Normal rendering
            currentScene.render(batch);
        }
    }
    
    /**
     * Get the current scene
     */
    public Scene getCurrentScene() {
        return currentScene;
    }
    
    /**
     * Get the ID of the current scene
     */
    public String getCurrentSceneId() {
        return currentSceneId;
    }
    
    /**
     * Get the previous scene
     */
    public Scene getPreviousScene() {
        return previousScene;
    }
    
    /**
     * Check if a scene transition is in progress
     */
    public boolean isTransitioning() {
        return transitioning;
    }
    
    /**
     * Dispose all scenes and resources
     */
    @Override
    public void dispose() {
        for (Scene scene : scenes.values()) {
            scene.dispose();
        }
        scenes.clear();
        currentScene = null;
        previousScene = null;
        currentSceneId = null;
        previousSceneId = null;
        
        LOGGER.log(Level.INFO, "SceneStateMachine disposed");
    }
}