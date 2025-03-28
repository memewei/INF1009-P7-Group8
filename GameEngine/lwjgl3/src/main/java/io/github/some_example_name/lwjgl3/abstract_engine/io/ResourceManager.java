package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized resource manager for all game assets.
 * Provides efficient loading, caching, and disposal of resources.
 */
public class ResourceManager implements Disposable {
    private static final String TAG = "ResourceManager";
    private static ResourceManager instance;
    
    private final AssetManager assetManager;
    private final Map<String, BitmapFont> fontCache;
    private boolean initialized = false;
    
    /**
     * Private constructor for singleton
     */
    private ResourceManager() {
        this.assetManager = new AssetManager();
        this.fontCache = new HashMap<>();
        assetManager.getLogger().setLevel(Logger.DEBUG);
    }
    
    /**
     * Get singleton instance
     */
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }
    
    /**
     * Initialize the resource manager
     */
    public void initialize() {
        if (initialized) return;
        initialized = true;
    }
    
    /**
     * Load a texture
     * @param filePath Path to the texture file
     */
    public void loadTexture(String filePath) {
        if (!assetManager.isLoaded(filePath, Texture.class)) {
            try {
                assetManager.load(filePath, Texture.class);
            } catch (GdxRuntimeException e) {
                Gdx.app.error(TAG, "Failed to load texture: " + filePath, e);
            }
        }
    }
    
    /**
     * Get a loaded texture
     * @param filePath Path to the texture file
     * @return The texture, or null if not loaded
     */
    public Texture getTexture(String filePath) {
        if (assetManager.isLoaded(filePath, Texture.class)) {
            return assetManager.get(filePath, Texture.class);
        } else {
            Gdx.app.debug(TAG, "Texture not loaded: " + filePath + ", attempting to load now");
            loadTexture(filePath);
            assetManager.finishLoadingAsset(filePath);
            return assetManager.get(filePath, Texture.class);
        }
    }
    
    /**
     * Load a sound
     * @param filePath Path to the sound file
     */
    public void loadSound(String filePath) {
        if (!assetManager.isLoaded(filePath, Sound.class)) {
            try {
                assetManager.load(filePath, Sound.class);
            } catch (GdxRuntimeException e) {
                Gdx.app.error(TAG, "Failed to load sound: " + filePath, e);
            }
        }
    }
    
    /**
     * Get a loaded sound
     * @param filePath Path to the sound file
     * @return The sound, or null if not loaded
     */
    public Sound getSound(String filePath) {
        if (assetManager.isLoaded(filePath, Sound.class)) {
            return assetManager.get(filePath, Sound.class);
        } else {
            Gdx.app.debug(TAG, "Sound not loaded: " + filePath + ", attempting to load now");
            loadSound(filePath);
            assetManager.finishLoadingAsset(filePath);
            return assetManager.get(filePath, Sound.class);
        }
    }
    
    /**
     * Load a music track
     * @param filePath Path to the music file
     */
    public void loadMusic(String filePath) {
        if (!assetManager.isLoaded(filePath, Music.class)) {
            try {
                assetManager.load(filePath, Music.class);
            } catch (GdxRuntimeException e) {
                Gdx.app.error(TAG, "Failed to load music: " + filePath, e);
            }
        }
    }
    
    /**
     * Get a loaded music track
     * @param filePath Path to the music file
     * @return The music, or null if not loaded
     */
    public Music getMusic(String filePath) {
        if (assetManager.isLoaded(filePath, Music.class)) {
            return assetManager.get(filePath, Music.class);
        } else {
            Gdx.app.debug(TAG, "Music not loaded: " + filePath + ", attempting to load now");
            loadMusic(filePath);
            assetManager.finishLoadingAsset(filePath);
            return assetManager.get(filePath, Music.class);
        }
    }
    
    /**
     * Load a bitmap font
     * @param filePath Path to the font file
     */
    public void loadFont(String filePath) {
        if (!assetManager.isLoaded(filePath, BitmapFont.class)) {
            try {
                assetManager.load(filePath, BitmapFont.class);
            } catch (GdxRuntimeException e) {
                Gdx.app.error(TAG, "Failed to load font: " + filePath, e);
            }
        }
    }
    
    /**
     * Get a loaded bitmap font
     * @param filePath Path to the font file
     * @return The font, or null if not loaded
     */
    public BitmapFont getFont(String filePath) {
        if (assetManager.isLoaded(filePath, BitmapFont.class)) {
            return assetManager.get(filePath, BitmapFont.class);
        } else {
            Gdx.app.debug(TAG, "Font not loaded: " + filePath + ", attempting to load now");
            loadFont(filePath);
            assetManager.finishLoadingAsset(filePath);
            return assetManager.get(filePath, BitmapFont.class);
        }
    }
    
    /**
     * Load a texture atlas
     * @param filePath Path to the atlas file
     */
    public void loadTextureAtlas(String filePath) {
        if (!assetManager.isLoaded(filePath, TextureAtlas.class)) {
            try {
                assetManager.load(filePath, TextureAtlas.class);
            } catch (GdxRuntimeException e) {
                Gdx.app.error(TAG, "Failed to load texture atlas: " + filePath, e);
            }
        }
    }
    
    /**
     * Get a loaded texture atlas
     * @param filePath Path to the atlas file
     * @return The texture atlas, or null if not loaded
     */
    public TextureAtlas getTextureAtlas(String filePath) {
        if (assetManager.isLoaded(filePath, TextureAtlas.class)) {
            return assetManager.get(filePath, TextureAtlas.class);
        } else {
            Gdx.app.debug(TAG, "Texture atlas not loaded: " + filePath + ", attempting to load now");
            loadTextureAtlas(filePath);
            assetManager.finishLoadingAsset(filePath);
            return assetManager.get(filePath, TextureAtlas.class);
        }
    }
    
    /**
     * Update the asset manager to continue loading assets
     * @param deltaTime Time since last update
     * @return true if all assets are loaded
     */
    public boolean update(float deltaTime) {
        return assetManager.update();
    }
    
    /**
     * Get the current loading progress (0-1)
     */
    public float getProgress() {
        return assetManager.getProgress();
    }
    
    /**
     * Finish loading all queued assets (blocking)
     */
    public void finishLoading() {
        assetManager.finishLoading();
    }
    
    /**
     * Preload common assets in bulk
     * @param texturePaths Array of texture paths
     * @param soundPaths Array of sound paths
     * @param musicPaths Array of music paths
     * @param fontPaths Array of font paths
     */
    public void preloadAssets(String[] texturePaths, String[] soundPaths, String[] musicPaths, String[] fontPaths) {
        if (texturePaths != null) {
            for (String path : texturePaths) {
                loadTexture(path);
            }
        }
        
        if (soundPaths != null) {
            for (String path : soundPaths) {
                loadSound(path);
            }
        }
        
        if (musicPaths != null) {
            for (String path : musicPaths) {
                loadMusic(path);
            }
        }
        
        if (fontPaths != null) {
            for (String path : fontPaths) {
                loadFont(path);
            }
        }
    }
    
    /**
     * Unload a specific asset
     * @param filePath Path to the asset
     */
    public void unloadAsset(String filePath) {
        if (assetManager.isLoaded(filePath)) {
            assetManager.unload(filePath);
        }
    }
    
    /**
     * Clear all loaded assets
     */
    public void clearAll() {
        assetManager.clear();
        fontCache.clear();
    }
    
    /**
     * Check if the asset manager contains an asset
     */
    public boolean isLoaded(String filePath) {
        return assetManager.isLoaded(filePath);
    }
    
    @Override
    public void dispose() {
        assetManager.dispose();
        fontCache.clear();
        Gdx.app.debug(TAG, "ResourceManager disposed");
    }
}