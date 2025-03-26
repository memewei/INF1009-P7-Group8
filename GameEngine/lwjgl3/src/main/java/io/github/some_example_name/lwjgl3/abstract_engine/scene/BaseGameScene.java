package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.ui.AssetPaths;

/**
 * Base abstract class for all game scenes to reduce duplication
 * and improve OOP structure.
 */
public abstract class BaseGameScene extends Scene {
    // Common components for all scenes
    protected SpriteBatch batch;
    protected SceneManager sceneManager;
    protected EntityManager entityManager;
    protected MovementManager movementManager;
    protected IOManager ioManager;
    protected BitmapFont font;
    
    // Common resources
    protected Texture backgroundTexture;
    protected float timeElapsed = 0;
    
    /**
     * Constructor for BaseGameScene
     */
    public BaseGameScene(SpriteBatch batch, SceneManager sceneManager,
                      EntityManager entityManager, MovementManager movementManager,
                      IOManager ioManager) {
        this.batch = batch;
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.ioManager = ioManager;
        
        // Initialize common font
        font = new BitmapFont(Gdx.files.internal(AssetPaths.GAME_FONT));
        font.setColor(Color.WHITE);
        font.getData().setScale(0.3f);
    }
    
    @Override
    public void initialize() {
        try {
            // Load common background texture
            backgroundTexture = new Texture(Gdx.files.internal(AssetPaths.BACKGROUND));
            
            // Let subclasses initialize their specific resources
            initializeResources();
        } catch (Exception e) {
            System.err.println("[BaseGameScene] Error loading common resources: " + e.getMessage());
        }
    }
    
    /**
     * Initialize scene-specific resources
     */
    protected abstract void initializeResources();
    
    @Override
    public void update(float deltaTime) {
        timeElapsed += deltaTime;
        
        // Common update logic
        
        // Call scene-specific update logic
        updateScene(deltaTime);
    }
    
    /**
     * Update scene-specific logic
     */
    protected abstract void updateScene(float deltaTime);
    
    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        
        // Render common background
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        
        // Call scene-specific render logic
        renderScene(batch);
        
        batch.end();
    }
    
    /**
     * Render scene-specific elements
     */
    protected abstract void renderScene(SpriteBatch batch);
    
    /**
     * Helper method to draw centered text
     */
    protected void drawCenteredText(SpriteBatch batch, String text, float y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        
        // Calculate text width for centering
        GlyphLayout layout = new GlyphLayout(font, text);
        float textWidth = layout.width;
        
        // Draw centered text
        font.draw(batch, text, (Gdx.graphics.getWidth() - textWidth) / 2f, y);
    }
    
    /**
     * Play a background music track
     */
    protected void playMusic(String musicFile) {
        ioManager.getAudio().playMusic(musicFile);
    }
    
    /**
     * Stop background music
     */
    protected void stopMusic() {
        ioManager.getAudio().stopMusic();
    }
    
    /**
     * Play a sound effect
     */
    protected void playSound(String soundFile) {
        ioManager.getAudio().playSound(soundFile);
    }
    
    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        
        if (font != null) {
            font.dispose();
        }
        
        // Call scene-specific disposal logic
        disposeResources();
    }
    
    /**
     * Dispose scene-specific resources
     */
    protected abstract void disposeResources();
}