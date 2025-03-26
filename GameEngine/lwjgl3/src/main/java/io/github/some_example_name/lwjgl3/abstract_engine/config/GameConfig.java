package io.github.some_example_name.lwjgl3.abstract_engine.config;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Gdx;

/**
 * Game configuration manager to handle persistent game settings
 * and provide default values when needed.
 */
public class GameConfig {
    private static final String PREFS_NAME = "HealthSnake_Preferences";
    
    // Preference keys
    private static final String KEY_MUSIC_VOLUME = "musicVolume";
    private static final String KEY_SOUND_VOLUME = "soundVolume";
    private static final String KEY_CONTROL_MODE = "controlMode";
    private static final String KEY_SNAKE_COLOR = "snakeColor";
    
    // Default values
    private static final float DEFAULT_MUSIC_VOLUME = 0.7f;
    private static final float DEFAULT_SOUND_VOLUME = 0.8f;
    private static final String DEFAULT_CONTROL_MODE = "KEYBOARD";
    private static final String DEFAULT_SNAKE_COLOR = "green";
    
    // Singleton instance
    private static GameConfig instance;
    
    private Preferences prefs;
    
    // Private constructor for singleton pattern
    private GameConfig() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }
    
    /**
     * Gets the singleton instance of the GameConfig
     */
    public static GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }
    
    /**
     * Save the current settings
     */
    public void saveSettings() {
        prefs.flush();
    }
    
    // Music volume
    public float getMusicVolume() {
        return prefs.getFloat(KEY_MUSIC_VOLUME, DEFAULT_MUSIC_VOLUME);
    }
    
    public void setMusicVolume(float volume) {
        prefs.putFloat(KEY_MUSIC_VOLUME, volume);
        saveSettings();
    }
    
    // Sound volume
    public float getSoundVolume() {
        return prefs.getFloat(KEY_SOUND_VOLUME, DEFAULT_SOUND_VOLUME);
    }
    
    public void setSoundVolume(float volume) {
        prefs.putFloat(KEY_SOUND_VOLUME, volume);
        saveSettings();
    }
    
    // Control mode
    public String getControlMode() {
        return prefs.getString(KEY_CONTROL_MODE, DEFAULT_CONTROL_MODE);
    }
    
    public void setControlMode(String mode) {
        prefs.putString(KEY_CONTROL_MODE, mode);
        saveSettings();
    }
    
    // Snake color
    public String getSnakeColor() {
        return prefs.getString(KEY_SNAKE_COLOR, DEFAULT_SNAKE_COLOR);
    }
    
    public void setSnakeColor(String color) {
        prefs.putString(KEY_SNAKE_COLOR, color);
        saveSettings();
    }
    
    // Game world settings (these could be saved to preferences too)
    public int getWorldWidth() {
        return 2000;
    }
    
    public int getWorldHeight() {
        return 2000;
    }
    
    public int getMaxFood() {
        return 30;
    }
    
    public float getFoodSpawnInterval() {
        return 2.0f;
    }
    
    public float getTransitionDuration() {
        return 2.0f;
    }
}