package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;
import io.github.some_example_name.lwjgl3.abstract_engine.control.ControlMode;
import io.github.some_example_name.lwjgl3.abstract_engine.config.GameConfig;

/**
 * Improved IOManager with better OOP practices
 * - Uses composition over inheritance
 * - Properly manages dependencies
 * - Uses abstraction for audio
 */
public class IOManager {
    private static IOManager instance;
    private AudioManager audioManager;
    private DynamicInput dynamicInput;
    private ControlMode controlMode;
    
    /**
     * Private constructor for singleton pattern
     */
    private IOManager() {
        // Initialize non-Gdx components
        audioManager = new AudioManager();
        
        // Load control mode from saved configuration
        String savedMode = GameConfig.getInstance().getControlMode();
        controlMode = ControlMode.valueOf(savedMode);
    }
    
    /**
     * Get the singleton instance
     */
    public static IOManager getInstance() {
        if (instance == null) {
            instance = new IOManager();
        }
        return instance;
    }
    
    /**
     * Initialize Gdx-dependent components
     * Must be called after LibGDX is initialized
     */
    public void init() {
        dynamicInput = new DynamicInput();
        Gdx.input.setInputProcessor(dynamicInput);
    }
    
    /**
     * Get the audio manager
     */
    public AudioManager getAudio() {
        return audioManager;
    }
    
    /**
     * Get the input manager
     */
    public DynamicInput getDynamicInput() {
        return dynamicInput;
    }
    
    /**
     * Get the current control mode
     */
    public ControlMode getControlMode() {
        return controlMode;
    }
    
    /**
     * Set the control mode
     */
    public void setControlMode(ControlMode mode) {
        this.controlMode = mode;
        
        // Save to configuration
        GameConfig.getInstance().setControlMode(mode.toString());
    }
    
    /**
     * Toggle between keyboard and mouse control modes
     */
    public void toggleControlMode() {
        if (controlMode == ControlMode.KEYBOARD) {
            setControlMode(ControlMode.MOUSE);
        } else {
            setControlMode(ControlMode.KEYBOARD);
        }
    }
    
    /**
     * Dispose all resources
     */
    public void dispose() {
        if (audioManager != null) {
            audioManager.dispose();
        }
        
        // Input processor doesn't need to be disposed
        dynamicInput = null;
    }
}