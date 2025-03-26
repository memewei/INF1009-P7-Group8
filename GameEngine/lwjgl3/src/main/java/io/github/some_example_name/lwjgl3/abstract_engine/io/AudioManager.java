package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import io.github.some_example_name.lwjgl3.abstract_engine.ui.AssetPaths;
import io.github.some_example_name.lwjgl3.abstract_engine.config.GameConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced AudioManager to replace the original AudioOutput class.
 * This implementation adds better resource management and error handling.
 */
public class AudioManager {
    private final Map<String, Sound> soundEffects = new HashMap<>();
    private Music backgroundMusic;
    private float musicVolume;
    private float soundVolume;
    private boolean isMusicPlaying = false;
    private String currentMusicFile = "";
    
    /**
     * Create a new AudioManager
     */
    public AudioManager() {
        // Initialize volumes from game configuration
        GameConfig config = GameConfig.getInstance();
        musicVolume = config.getMusicVolume();
        soundVolume = config.getSoundVolume();
    }
    
    /**
     * Play a sound effect
     * @param file The sound file to play
     */
    public void playSound(String file) {
        try {
            Sound sound = getSound(file);
            if (sound != null) {
                sound.stop(); // Stop the sound if it's already playing
                sound.play(soundVolume);
            }
        } catch (Exception e) {
            System.err.println("[AudioManager] Error playing sound: " + e.getMessage());
        }
    }
    
    /**
     * Get a sound from the cache or load it if not present
     */
    private Sound getSound(String file) {
        // Check if sound exists in the cache
        if (!soundEffects.containsKey(file)) {
            try {
                Sound sound = Gdx.audio.newSound(Gdx.files.internal(file));
                soundEffects.put(file, sound);
                return sound;
            } catch (Exception e) {
                System.err.println("[AudioManager] Error loading sound " + file + ": " + e.getMessage());
                return null;
            }
        }
        return soundEffects.get(file);
    }
    
    /**
     * Play music, looping continuously
     * @param file The music file to play
     */
    public void playMusic(String file) {
        // Don't restart the same music that's already playing
        if (isMusicPlaying && file.equals(currentMusicFile)) {
            return;
        }
        
        // Stop any currently playing music
        stopMusic();
        
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(file));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(musicVolume);
            backgroundMusic.play();
            
            isMusicPlaying = true;
            currentMusicFile = file;
        } catch (Exception e) {
            System.err.println("[AudioManager] Error playing music " + file + ": " + e.getMessage());
        }
    }
    
    /**
     * Stop the currently playing music
     */
    public void stopMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            isMusicPlaying = false;
            currentMusicFile = "";
        }
    }
    
    /**
     * Pause the currently playing music
     */
    public void pauseMusic() {
        if (backgroundMusic != null && isMusicPlaying) {
            backgroundMusic.pause();
        }
    }
    
    /**
     * Resume paused music
     */
    public void resumeMusic() {
        if (backgroundMusic != null && !isMusicPlaying) {
            backgroundMusic.play();
            isMusicPlaying = true;
        }
    }
    
    /**
     * Get the current music volume (0-100 scale)
     */
    public float getMusicVolume() {
        return musicVolume * 100; // Convert to 0-100 scale
    }
    
    /**
     * Set the music volume (0-1 scale)
     */
    public void setMusicVolume(float volume) {
        musicVolume = volume;
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(musicVolume);
        }
        
        // Update saved configuration
        GameConfig.getInstance().setMusicVolume(volume);
    }
    
    /**
     * Get the current sound effect volume (0-100 scale)
     */
    public float getSoundVolume() {
        return soundVolume * 100; // Convert to 0-100 scale
    }
    
    /**
     * Set the sound effect volume (0-1 scale)
     */
    public void setSoundVolume(float volume) {
        soundVolume = volume;
        
        // Update saved configuration
        GameConfig.getInstance().setSoundVolume(volume);
    }
    
    /**
     * Check if music is currently playing
     */
    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }
    
    /**
     * Dispose of all audio resources
     */
    public void dispose() {
        for (Sound sound : soundEffects.values()) {
            sound.dispose();
        }
        soundEffects.clear();
        
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
    }
}