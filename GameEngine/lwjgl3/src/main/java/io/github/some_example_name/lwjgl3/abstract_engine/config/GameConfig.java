package io.github.some_example_name.lwjgl3.abstract_engine.config;

import java.util.HashMap;
import java.util.Map;

import io.github.some_example_name.lwjgl3.application_classes.game.SnakeColor;

/**
 * Simplified game configuration manager with in-memory storage
 */
public class GameConfig {
    // Singleton instance
    private static GameConfig instance;

    // Default configuration values
    private static final float DEFAULT_MUSIC_VOLUME = 0.7f;
    private static final float DEFAULT_SOUND_VOLUME = 0.8f;
    private static final String DEFAULT_CONTROL_MODE = "KEYBOARD";
    private static final SnakeColor DEFAULT_SNAKE_COLOR = SnakeColor.GREEN;


    // Configuration storage
    private Map<String, Object> configMap;

    // Private constructor for singleton pattern
    private GameConfig() {
        configMap = new HashMap<>();
        
        // Set default values
        configMap.put("musicVolume", DEFAULT_MUSIC_VOLUME);
        configMap.put("soundVolume", DEFAULT_SOUND_VOLUME);
        configMap.put("controlMode", DEFAULT_CONTROL_MODE);
        configMap.put("snakeColor", DEFAULT_SNAKE_COLOR);
    }

    /**
     * Get the singleton instance of GameConfig
     */
    public static synchronized GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }

    /**
     * Get music volume
     */
    public float getMusicVolume() {
        return getFloatValue("musicVolume", DEFAULT_MUSIC_VOLUME);
    }

    /**
     * Set music volume
     */
    public void setMusicVolume(float volume) {
        configMap.put("musicVolume", volume);
    }

    /**
     * Get sound volume
     */
    public float getSoundVolume() {
        return getFloatValue("soundVolume", DEFAULT_SOUND_VOLUME);
    }

    /**
     * Set sound volume
     */
    public void setSoundVolume(float volume) {
        configMap.put("soundVolume", volume);
    }

    /**
     * Get control mode
     */
    public String getControlMode() {
        return getStringValue("controlMode", DEFAULT_CONTROL_MODE);
    }

    /**
     * Set control mode
     */
    public void setControlMode(String mode) {
        configMap.put("controlMode", mode);
    }

    /**
     * Get snake color
     */
    public SnakeColor getSnakeColor() {
    	Object value = configMap.get("snakeColor");
        return (value instanceof SnakeColor) ? (SnakeColor) value : SnakeColor.GREEN;
    }

    /**
     * Set snake color
     */
    public void setSnakeColor(SnakeColor color) {
        configMap.put("snakeColor", color);
    }

    /**
     * Generic method to get float value with default
     */
    private float getFloatValue(String key, float defaultValue) {
        Object value = configMap.get(key);
        return (value instanceof Float) ? (Float) value : defaultValue;
    }

    /**
     * Generic method to get string value with default
     */
    private String getStringValue(String key, String defaultValue) {
        Object value = configMap.get(key);
        return (value instanceof String) ? (String) value : defaultValue;
    }

    /**
     * Reset all settings to default
     */
    public void resetToDefaults() {
        configMap.put("musicVolume", DEFAULT_MUSIC_VOLUME);
        configMap.put("soundVolume", DEFAULT_SOUND_VOLUME);
        configMap.put("controlMode", DEFAULT_CONTROL_MODE);
        configMap.put("snakeColor", DEFAULT_SNAKE_COLOR);
    }
}