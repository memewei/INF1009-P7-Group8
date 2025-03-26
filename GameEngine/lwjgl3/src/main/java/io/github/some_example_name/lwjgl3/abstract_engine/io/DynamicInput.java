package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.lwjgl3.abstract_engine.ui.AssetPaths;

/**
 * Improved DynamicInput class that handles keyboard and mouse input.
 * This class follows better OOP principles by:
 * - Encapsulating input state
 * - Using constants instead of hardcoded values
 * - Providing clean APIs for input checking
 */
public class DynamicInput extends InputAdapter {
    // Input state tracking
    private String currentInput = "";
    private final Vector2 mousePosition = new Vector2();
    private BitmapFont debugFont;
    private SpriteBatch debugBatch;
    private boolean debugMode = false;
    
    // Constants
    private static final int DEBUG_TEXT_X_OFFSET = 120;
    private static final int DEBUG_TEXT_Y_OFFSET = 20;
    private static final float DEBUG_FONT_SCALE = 0.2f;
    
    /**
     * Create a new DynamicInput handler
     */
    public DynamicInput() {
        // Only initialize debug resources if in debug mode
        if (debugMode) {
            debugBatch = new SpriteBatch();
            debugFont = new BitmapFont(Gdx.files.internal(AssetPaths.GAME_FONT));
            debugFont.getData().setScale(DEBUG_FONT_SCALE);
        }
    }
    
    @Override
    public boolean keyDown(int keycode) {
        currentInput = "Key: " + Input.Keys.toString(keycode);
        return true;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        currentInput = "";
        return true;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mousePosition.set(screenX, screenY);
        currentInput = "Mouse: Button " + button;
        return true;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        currentInput = "";
        return true;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mousePosition.set(screenX, screenY);
        return true;
    }
    
    /**
     * Check if a key is currently pressed
     * @param keycode The key code to check
     * @return true if the key is pressed
     */
    public boolean isKeyPressed(int keycode) {
        return Gdx.input.isKeyPressed(keycode);
    }
    
    /**
     * Check if a key was just pressed this frame
     * @param keycode The key code to check
     * @return true if the key was just pressed
     */
    public boolean isKeyJustPressed(int keycode) {
        return Gdx.input.isKeyJustPressed(keycode);
    }
    
    /**
     * Get the current mouse X position
     */
    public int getMouseX() {
        return Gdx.input.getX();
    }
    
    /**
     * Get the current mouse Y position
     */
    public int getMouseY() {
        return Gdx.input.getY();
    }
    
    /**
     * Get the mouse position as a Vector2
     */
    public Vector2 getMousePosition() {
        // Returns a copy to prevent the internal vector from being modified
        return new Vector2(mousePosition);
    }
    
    /**
     * Get the mouse position in world coordinates (Y is flipped)
     */
    public Vector2 getMousePositionInWorld() {
        return new Vector2(getMouseX(), Gdx.graphics.getHeight() - getMouseY());
    }
    
    /**
     * Enable or disable debug mode
     */
    public void setDebugMode(boolean enabled) {
        // Only initialize debug resources when first enabled
        if (enabled && !debugMode && debugBatch == null) {
            debugBatch = new SpriteBatch();
            debugFont = new BitmapFont(Gdx.files.internal(AssetPaths.GAME_FONT));
            debugFont.getData().setScale(DEBUG_FONT_SCALE);
        }
        debugMode = enabled;
    }
    
    /**
     * Draw input debug info
     */
    public void drawInputText() {
        if (!debugMode || debugBatch == null || debugFont == null) {
            return;
        }
        
        String input = currentInput;
        if (!input.isEmpty()) {
            float x = Gdx.graphics.getWidth() - DEBUG_TEXT_X_OFFSET;
            float y = Gdx.graphics.getHeight() - DEBUG_TEXT_Y_OFFSET;
            
            debugBatch.begin();
            debugFont.draw(debugBatch, input, x, y);
            debugBatch.end();
        }
    }
    
    /**
     * Dispose resources
     */
    public void dispose() {
        if (debugBatch != null) {
            debugBatch.dispose();
            debugBatch = null;
        }
        
        if (debugFont != null) {
            debugFont.dispose();
            debugFont = null;
        }
    }
}