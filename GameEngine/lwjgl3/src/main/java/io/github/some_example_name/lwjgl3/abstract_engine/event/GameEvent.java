package io.github.some_example_name.lwjgl3.abstract_engine.event;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all game events that can be dispatched through the event system.
 * Implements a flexible data storage mechanism to pass parameters.
 */
public class GameEvent {
    // Common event types
    public static final String ENTITY_CREATED = "entity_created";
    public static final String ENTITY_DESTROYED = "entity_destroyed";
    public static final String COLLISION = "collision";
    public static final String HEALTH_CHANGED = "health_changed";
    public static final String GAME_STATE_CHANGED = "game_state_changed";
    public static final String LEVEL_LOADED = "level_loaded";
    public static final String PLAYER_DIED = "player_died";
    public static final String PLAYER_WON = "player_won";
    public static final String SCORE_CHANGED = "score_changed";
    public static final String ITEM_COLLECTED = "item_collected";
    public static final String ENEMY_DEFEATED = "enemy_defeated";
    
    private final String type;
    private final long timestamp;
    private final Map<String, Object> data;
    
    /**
     * Create a new event with the specified type
     * @param type The event type
     */
    public GameEvent(String type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.data = new HashMap<>();
    }
    
    /**
     * Get the event type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Get the timestamp when the event was created
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Set a data parameter
     * @param key Parameter name
     * @param value Parameter value
     * @return This event (for chaining)
     */
    public GameEvent setParameter(String key, Object value) {
        data.put(key, value);
        return this;
    }
    
    /**
     * Get a string parameter
     * @param key Parameter name
     * @param defaultValue Default value if parameter not found
     * @return The parameter value, or defaultValue if not found
     */
    public String getStringParameter(String key, String defaultValue) {
        Object value = data.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }
    
    /**
     * Get an integer parameter
     * @param key Parameter name
     * @param defaultValue Default value if parameter not found
     * @return The parameter value, or defaultValue if not found
     */
    public int getIntParameter(String key, int defaultValue) {
        Object value = data.get(key);
        return value instanceof Integer ? (Integer) value : defaultValue;
    }
    
    /**
     * Get a float parameter
     * @param key Parameter name
     * @param defaultValue Default value if parameter not found
     * @return The parameter value, or defaultValue if not found
     */
    public float getFloatParameter(String key, float defaultValue) {
        Object value = data.get(key);
        return value instanceof Float ? (Float) value : defaultValue;
    }
    
    /**
     * Get a boolean parameter
     * @param key Parameter name
     * @param defaultValue Default value if parameter not found
     * @return The parameter value, or defaultValue if not found
     */
    public boolean getBooleanParameter(String key, boolean defaultValue) {
        Object value = data.get(key);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }
    
    /**
     * Get an object parameter
     * @param key Parameter name
     * @param <T> Expected object type
     * @return The parameter value, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getObjectParameter(String key) {
        return (T) data.get(key);
    }
    
    /**
     * Check if a parameter exists
     * @param key Parameter name
     * @return true if the parameter exists
     */
    public boolean hasParameter(String key) {
        return data.containsKey(key);
    }
    
    /**
     * Get all parameters
     */
    public Map<String, Object> getAllParameters() {
        return new HashMap<>(data);
    }
    
    /**
     * Create a collision event
     * @param entityA First entity ID in the collision
     * @param entityB Second entity ID in the collision
     * @return The collision event
     */
    public static GameEvent createCollisionEvent(String entityA, String entityB) {
        return new GameEvent(COLLISION)
            .setParameter("entityA", entityA)
            .setParameter("entityB", entityB);
    }
    
    /**
     * Create a health changed event
     * @param entityId Entity ID whose health changed
     * @param currentHealth New health value
     * @param previousHealth Previous health value
     * @return The health changed event
     */
    public static GameEvent createHealthChangedEvent(String entityId, float currentHealth, float previousHealth) {
        return new GameEvent(HEALTH_CHANGED)
            .setParameter("entityId", entityId)
            .setParameter("currentHealth", currentHealth)
            .setParameter("previousHealth", previousHealth)
            .setParameter("delta", currentHealth - previousHealth);
    }
    
    /**
     * Create a game state changed event
     * @param newState New game state
     * @param oldState Previous game state
     * @return The game state changed event
     */
    public static GameEvent createGameStateChangedEvent(String newState, String oldState) {
        return new GameEvent(GAME_STATE_CHANGED)
            .setParameter("newState", newState)
            .setParameter("oldState", oldState);
    }
    
    /**
     * Create a level loaded event
     * @param levelId ID of the loaded level
     * @return The level loaded event
     */
    public static GameEvent createLevelLoadedEvent(String levelId) {
        return new GameEvent(LEVEL_LOADED)
            .setParameter("levelId", levelId);
    }
    
    /**
     * Create a player died event
     * @param cause Cause of death
     * @return The player died event
     */
    public static GameEvent createPlayerDiedEvent(String cause) {
        return new GameEvent(PLAYER_DIED)
            .setParameter("cause", cause);
    }
    
    /**
     * Create a score changed event
     * @param newScore New score value
     * @param oldScore Previous score value
     * @return The score changed event
     */
    public static GameEvent createScoreChangedEvent(int newScore, int oldScore) {
        return new GameEvent(SCORE_CHANGED)
            .setParameter("newScore", newScore)
            .setParameter("oldScore", oldScore)
            .setParameter("delta", newScore - oldScore);
    }
    
    /**
     * Create an item collected event
     * @param itemId ID of the collected item
     * @param itemType Type of the collected item
     * @return The item collected event
     */
    public static GameEvent createItemCollectedEvent(String itemId, String itemType) {
        return new GameEvent(ITEM_COLLECTED)
            .setParameter("itemId", itemId)
            .setParameter("itemType", itemType);
    }
}