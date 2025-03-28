package io.github.some_example_name.lwjgl3.abstract_engine.io;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

import io.github.some_example_name.lwjgl3.abstract_engine.config.GameConfig;

/**
 * AudioManager handles all audio playback in the game.
 * Manages sound effects and background music with better resource management.
 */
public class AudioManager implements Disposable {
    private static final Logger LOGGER = Logger.getLogger(AudioManager.class.getName());
    
    // Audio resource caches
    private final Map<String, Sound> soundEffects = new HashMap<>();
    private final Map<String, Music> musicTracks = new HashMap<>();
    
    // Current state
    private Music currentMusic;
    private float musicVolume;
    private float soundVolume;
    private boolean isMusicPlaying = false;
    private boolean isMusicPaused = false;
    private String currentMusicFile = "";
    
    // Default values
    private static final float DEFAULT_MUSIC_VOLUME = 0.7f;
    private static final float DEFAULT_SOUND_VOLUME = 0.8f;
    
    /**
     * Create a new AudioManager
     */
    public AudioManager() {
        // Initialize volumes from game configuration
        GameConfig config = GameConfig.getInstance();
        musicVolume = config.getMusicVolume();
        soundVolume = config.getSoundVolume();
        
        LOGGER.log(Level.INFO, "AudioManager initialized with music volume: {0}, sound volume: {1}",
                new Object[]{musicVolume, soundVolume});
    }
    
    /**
     * Play a sound effect with default volume
     * @param file The sound file to play
     * @return The sound ID (can be used to stop or modify the sound)
     */
    public long playSound(String file) {
        return playSound(file, soundVolume);
    }
    
    /**
     * Play a sound effect with specific volume
     * @param file The sound file to play
     * @param volume Volume level (0.0 to 1.0)
     * @return The sound ID (can be used to stop or modify the sound)
     */
    public long playSound(String file, float volume) {
        if (file == null || file.isEmpty()) {
            LOGGER.log(Level.WARNING, "Attempted to play null or empty sound file");
            return -1;
        }
        
        try {
            Sound sound = getSound(file);
            if (sound != null) {
                // Stop the sound if it's already playing to prevent overlapping
                sound.stop();
                return sound.play(volume);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error playing sound: {0}", e.getMessage());
        }
        return -1;
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
                LOGGER.log(Level.SEVERE, "Error loading sound {0}: {1}", new Object[]{file, e.getMessage()});
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
        if (file == null || file.isEmpty()) {
            LOGGER.log(Level.WARNING, "Attempted to play null or empty music file");
            return;
        }
        
        // Don't restart the same music that's already playing
        if (isMusicPlaying && !isMusicPaused && file.equals(currentMusicFile)) {
            return;
        }
        
        // Stop any currently playing music
        stopMusic();
        
        try {
            currentMusic = getMusicTrack(file);
            if (currentMusic != null) {
                currentMusic.setLooping(true);
                currentMusic.setVolume(musicVolume);
                currentMusic.play();
                
                isMusicPlaying = true;
                isMusicPaused = false;
                currentMusicFile = file;
                
                LOGGER.log(Level.INFO, "Playing music: {0}", file);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error playing music {0}: {1}", new Object[]{file, e.getMessage()});
        }
    }
    
    /**
     * Get a music track from the cache or load it if not present
     */
    private Music getMusicTrack(String file) {
        // Check if music exists in the cache
        if (!musicTracks.containsKey(file)) {
            try {
                Music music = Gdx.audio.newMusic(Gdx.files.internal(file));
                musicTracks.put(file, music);
                return music;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading music {0}: {1}", new Object[]{file, e.getMessage()});
                return null;
            }
        }
        return musicTracks.get(file);
    }
    
    /**
     * Stop the currently playing music
     */
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            isMusicPlaying = false;
            isMusicPaused = false;
            currentMusicFile = "";
            LOGGER.log(Level.FINE, "Music stopped");
        }
    }
    
    /**
     * Pause the currently playing music
     */
    public void pauseMusic() {
        if (currentMusic != null && isMusicPlaying && !isMusicPaused) {
            currentMusic.pause();
            isMusicPaused = true;
            LOGGER.log(Level.FINE, "Music paused");
        }
    }
    
    /**
     * Resume paused music
     */
    public void resumeMusic() {
        if (currentMusic != null && isMusicPaused) {
            currentMusic.play();
            isMusicPaused = false;
            LOGGER.log(Level.FINE, "Music resumed");
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
        musicVolume = Math.max(0f, Math.min(1f, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
        
        // Update saved configuration
        GameConfig.getInstance().setMusicVolume(musicVolume);
        LOGGER.log(Level.FINE, "Music volume set to: {0}", musicVolume);
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
        soundVolume = Math.max(0f, Math.min(1f, volume));
        
        // Update saved configuration
        GameConfig.getInstance().setSoundVolume(soundVolume);
        LOGGER.log(Level.FINE, "Sound volume set to: {0}", soundVolume);
    }
    
    /**
     * Check if music is currently playing
     */
    public boolean isMusicPlaying() {
        return isMusicPlaying && !isMusicPaused;
    }
    
    /**
     * Check if music is currently paused
     */
    public boolean isMusicPaused() {
        return isMusicPaused;
    }
    
    /**
     * Get the filename of the currently playing music
     */
    public String getCurrentMusicFile() {
        return currentMusicFile;
    }
    
    /**
     * Stop and remove a specific sound from the cache
     */
    public void disposeSound(String file) {
        Sound sound = soundEffects.remove(file);
        if (sound != null) {
            sound.stop();
            sound.dispose();
            LOGGER.log(Level.FINE, "Disposed sound: {0}", file);
        }
    }
    
    /**
     * Stop and remove a specific music track from the cache
     */
    public void disposeMusic(String file) {
        Music music = musicTracks.remove(file);
        if (music != null) {
            if (currentMusic == music) {
                currentMusic = null;
                isMusicPlaying = false;
                isMusicPaused = false;
                currentMusicFile = "";
            }
            music.stop();
            music.dispose();
            LOGGER.log(Level.FINE, "Disposed music: {0}", file);
        }
    }
    
    /**
     * Dispose of all audio resources
     */
    @Override
    public void dispose() {
        LOGGER.log(Level.INFO, "Disposing AudioManager");
        
        // Stop current music
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
        
        // Dispose all sound effects
        for (Map.Entry<String, Sound> entry : soundEffects.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().dispose();
            }
        }
        soundEffects.clear();
        
        // Dispose all music tracks
        for (Map.Entry<String, Music> entry : musicTracks.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().dispose();
            }
        }
        musicTracks.clear();
        
        isMusicPlaying = false;
        isMusicPaused = false;
        currentMusicFile = "";
    }
}