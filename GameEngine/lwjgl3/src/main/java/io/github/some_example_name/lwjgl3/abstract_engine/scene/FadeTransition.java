package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A scene transition that fades between scenes.
 * Can be configured for fade-in, fade-out, or crossfade effects.
 */
public class FadeTransition implements SceneTransition {
    private Scene fromScene;
    private Scene toScene;
    private float duration;
    private Color fadeColor;
    private FadeMode mode;
    private Texture fadeTexture;
    private Viewport viewport;
    
    /**
     * Create a new fade transition
     * @param duration Duration in seconds
     * @param fadeColor Color to fade through
     * @param mode Fade mode (fade in, fade out, or crossfade)
     */
    public FadeTransition(float duration, Color fadeColor, FadeMode mode) {
        this.duration = duration;
        this.fadeColor = fadeColor;
        this.mode = mode;
        
        // Create a white 1x1 texture for full-screen rendering
        this.fadeTexture = new Texture(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.fadeTexture.draw(pixmap, 0, 0);
        pixmap.dispose();
    }
    
    @Override
    public void initialize(Scene fromScene, Scene toScene) {
        this.fromScene = fromScene;
        this.toScene = toScene;
    }
    
    @Override
    public float getDuration() {
        return duration;
    }
    
    @Override
    public void render(SpriteBatch batch, float progress) {
        // Enable alpha blending
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        batch.begin();
        
        // Render the appropriate scenes based on the fade mode
        switch (mode) {
            case FADE_IN:
                renderFadeIn(batch, progress);
                break;
            case FADE_OUT:
                renderFadeOut(batch, progress);
                break;
            case CROSSFADE:
                renderCrossfade(batch, progress);
                break;
        }
        
        batch.end();
    }
    
    /**
     * Render a fade-in transition (from solid color to new scene)
     */
    private void renderFadeIn(SpriteBatch batch, float progress) {
        // Render target scene
        toScene.render(batch);
        
        // Render overlay with fading opacity
        Color overlayColor = new Color(fadeColor);
        overlayColor.a = 1.0f - progress; // Fade from opaque to transparent
        
        batch.setColor(overlayColor);
        batch.draw(fadeTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);
    }
    
    /**
     * Render a fade-out transition (from old scene to solid color)
     */
    private void renderFadeOut(SpriteBatch batch, float progress) {
        // Render source scene
        fromScene.render(batch);
        
        // Render overlay with increasing opacity
        Color overlayColor = new Color(fadeColor);
        overlayColor.a = progress; // Fade from transparent to opaque
        
        batch.setColor(overlayColor);
        batch.draw(fadeTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);
    }
    
    /**
     * Render a crossfade transition (blend between scenes)
     */
    private void renderCrossfade(SpriteBatch batch, float progress) {
        // First render the source scene
        fromScene.render(batch);
        
        // Reset the batch to allow proper alpha blending
        batch.end();
        batch.begin();
        
        // Then render the target scene with increasing opacity
        batch.setColor(1, 1, 1, progress);
        
        // This is a bit of a hack because we can't easily render a scene with alpha
        // In a real implementation, this might require rendering to framebuffers
        toScene.render(batch);
        
        batch.setColor(Color.WHITE);
    }
    
    /**
     * Set the viewport for rendering
     * @param viewport The viewport to use
     */
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }
    
    /**
     * Dispose of resources
     */
    public void dispose() {
        if (fadeTexture != null) {
            fadeTexture.dispose();
            fadeTexture = null;
        }
    }
    
    /**
     * Enum for different fade modes
     */
    public enum FadeMode {
        /**
         * Fade from a solid color to the new scene
         */
        FADE_IN,
        
        /**
         * Fade from the old scene to a solid color
         */
        FADE_OUT,
        
        /**
         * Crossfade directly between scenes
         */
        CROSSFADE
    }
}