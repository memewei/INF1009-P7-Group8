package io.github.some_example_name.lwjgl3.abstract_engine.entity;

/**
 * Base interface for all entity components.
 * Components provide specific functionality to entities.
 */
public interface Component {
    
    /**
     * Sets the owner entity of this component
     * @param owner The entity that owns this component
     */
    void setOwner(Entity owner);
    
    /**
     * Gets the owner entity of this component
     * @return The entity that owns this component
     */
    Entity getOwner();
    
    /**
     * Initialize the component with any required data
     */
    void initialize();
    
    /**
     * Update the component state
     * @param deltaTime Time since last update
     */
    void update(float deltaTime);
}