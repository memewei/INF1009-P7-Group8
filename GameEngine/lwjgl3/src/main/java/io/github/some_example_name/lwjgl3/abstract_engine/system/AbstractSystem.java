package io.github.some_example_name.lwjgl3.abstract_engine.system;

import com.badlogic.gdx.utils.Disposable;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Base abstract class for all game systems.
 * Systems provide functionality that operates on entities with specific components.
 * This implements a basic Entity-Component-System (ECS) architecture.
 */
public abstract class AbstractSystem implements Disposable {
    protected EntityManager entityManager;
    protected boolean enabled = true;
    protected List<Class<?>> requiredComponents;
    
    /**
     * Create a new system with the specified entity manager
     * @param entityManager The entity manager to use
     */
    public AbstractSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.requiredComponents = new ArrayList<>();
        initialize();
    }
    
    /**
     * Initialize the system and set up required components
     */
    protected abstract void initialize();
    
    /**
     * Update the system
     * @param deltaTime Time since last update
     */
    public abstract void update(float deltaTime);
    
    /**
     * Get entities that have all required components
     * @return List of matching entities
     */
    protected List<Entity> getRelevantEntities() {
        List<Entity> entities = new ArrayList<>();
        
        if (requiredComponents.isEmpty()) {
            return new ArrayList<>(entityManager.getAllEntities());
        }
        
        // Get entities with the first required component
        Class<?> firstComponent = requiredComponents.get(0);
        
        // If using the improved entity class with the generic getEntitiesByType method
        for (Entity entity : entityManager.getAllEntities()) {
            boolean hasAllComponents = true;
            
            // Check if the entity has all required components
            for (Class<?> componentClass : requiredComponents) {
                if (!entity.hasComponent(componentClass)) {
                    hasAllComponents = false;
                    break;
                }
            }
            
            if (hasAllComponents) {
                entities.add(entity);
            }
        }
        
        return entities;
    }
    
    /**
     * Add a required component type
     * @param componentType Class of the required component
     */
    protected void addRequiredComponent(Class<?> componentType) {
        if (!requiredComponents.contains(componentType)) {
            requiredComponents.add(componentType);
        }
    }
    
    /**
     * Enable or disable the system
     * @param enabled Whether the system should be enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Check if the system is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Dispose system resources
     */
    @Override
    public void dispose() {
        // Base implementation does nothing
    }
}