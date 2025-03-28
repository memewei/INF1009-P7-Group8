package io.github.some_example_name.lwjgl3.abstract_engine.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A centralized event system that allows components to communicate
 * without direct dependencies, implementing the Observer pattern.
 */
public class EventSystem {
    private static EventSystem instance;
    
    private final Map<String, List<EventListener>> listeners;
    
    /**
     * Private constructor for singleton
     */
    private EventSystem() {
        listeners = new HashMap<>();
    }
    
    /**
     * Get the singleton instance
     */
    public static EventSystem getInstance() {
        if (instance == null) {
            instance = new EventSystem();
        }
        return instance;
    }
    
    /**
     * Register a listener for a specific event type
     * @param eventType The type of event to listen for
     * @param listener The listener to register
     */
    public void addEventListener(String eventType, EventListener listener) {
        if (eventType == null || listener == null) {
            return;
        }
        
        // Get or create listener list for this event type
        List<EventListener> eventListeners = listeners.computeIfAbsent(
            eventType, k -> new CopyOnWriteArrayList<>()
        );
        
        // Add listener if not already present
        if (!eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }
    
    /**
     * Remove a listener for a specific event type
     * @param eventType The type of event
     * @param listener The listener to remove
     */
    public void removeEventListener(String eventType, EventListener listener) {
        if (eventType == null || listener == null) {
            return;
        }
        
        List<EventListener> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }
    
    /**
     * Trigger an event, notifying all registered listeners
     * @param event The event to trigger
     */
    public void triggerEvent(GameEvent event) {
        if (event == null) {
            return;
        }
        
        // Notify listeners for this specific event type
        List<EventListener> eventListeners = listeners.get(event.getType());
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                listener.onEvent(event);
            }
        }
        
        // Also notify listeners for "all" events
        List<EventListener> allListeners = listeners.get("all");
        if (allListeners != null) {
            for (EventListener listener : allListeners) {
                listener.onEvent(event);
            }
        }
    }
    
    /**
     * Get all registered listeners for an event type
     * @param eventType The event type
     * @return List of listeners, or empty list if none
     */
    public List<EventListener> getListeners(String eventType) {
        List<EventListener> eventListeners = listeners.get(eventType);
        return eventListeners != null ? new ArrayList<>(eventListeners) : new ArrayList<>();
    }
    
    /**
     * Clear all listeners
     */
    public void clearListeners() {
        listeners.clear();
    }
    
    /**
     * Clear listeners for a specific event type
     * @param eventType The event type
     */
    public void clearListeners(String eventType) {
        listeners.remove(eventType);
    }
    
    /**
     * Interface for event listeners
     */
    public interface EventListener {
        /**
         * Called when an event is triggered
         * @param event The triggered event
         */
        void onEvent(GameEvent event);
    }
}