package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Interface for scene transition effects.
 * Provides a clean way to transition between scenes with visual effects.
 */
public interface SceneTransition {
    
    /**
     * Initialize the transition with the source and target scenes
     * @param fromScene The scene transitioning from
     * @param toScene The scene transitioning to
     */
    void initialize(Scene fromScene, Scene toScene);
    
    /**
     * Get the duration of the transition in seconds
     * @return Duration in seconds
     */
    float getDuration();
    
    /**
     * Render the transition
     * @param batch The sprite batch to render with
     * @param progress Transition progress (0-1)
     */
    void render(SpriteBatch batch, float progress);
}