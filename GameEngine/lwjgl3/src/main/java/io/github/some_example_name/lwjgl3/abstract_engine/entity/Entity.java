package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Base abstract class for all game entities. Provides a type-safe component system
 * and common entity functionality.
 */
public abstract class Entity {
    protected final String entityID;
    protected String entityName;
    protected float positionX;
    protected float positionY;
    protected Texture texture;
    
    // Type-safe component system
    private Map<Class<?>, Component> components;

    /**
     * Creates a new entity with the specified name, position, and texture
     */
    public Entity(String entityName, float positionX, float positionY, String texturePath) {
        this.entityID = UUID.randomUUID().toString();
        this.entityName = entityName;
        this.positionX = positionX;
        this.positionY = positionY;
        this.components = new HashMap<>();
        
        // Load texture if path is provided
        if (texturePath != null && !texturePath.isEmpty()) {
            try {
                this.texture = new Texture(Gdx.files.internal(texturePath));
            } catch (Exception e) {
                System.err.println("[Entity] Failed to load texture: " + texturePath);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Creates a new entity with the specified name only (no position or texture)
     */
    public Entity(String entityName) {
        this.entityID = UUID.randomUUID().toString();
        this.entityName = entityName;
        this.components = new HashMap<>();
    }

    /**
     * Add a component to this entity
     * @param <T> The component type
     * @param component The component to add
     */
    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
        // Notify component it's been added to this entity
        component.setOwner(this);
    }

    /**
     * Check if this entity has a component of the specified type
     * @param <T> The component type
     * @param componentClass The class of the component
     * @return true if the entity has this component type
     */
    public <T extends Component> boolean hasComponent(Class<?> componentClass) {
        return components.containsKey(componentClass);
    }

    /**
     * Get a component of the specified type
     * @param <T> The component type
     * @param componentClass The class of the component
     * @return The component, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentClass) {
        return (T) components.get(componentClass);
    }

    /**
     * Remove a component of the specified type
     * @param <T> The component type
     * @param componentClass The class of the component
     * @return The removed component, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T removeComponent(Class<T> componentClass) {
        Component component = components.remove(componentClass);
        if (component != null) {
            // Notify component it's been removed from this entity
            component.setOwner(null);
        }
        return (T) component;
    }

    /**
     * Get all components attached to this entity
     */
    public Map<Class<?>, Component> getAllComponents() {
        return new HashMap<>(components);  // Return a copy to prevent modification
    }

    /**
     * Update the entity state
     * @param deltaTime Time since last update
     */
    public abstract void update(float deltaTime);
    
    /**
     * Render the entity
     * @param batch The SpriteBatch to render with
     */
    public abstract void render(SpriteBatch batch);

    /**
     * Get the width of the entity
     */
    public int getWidth() {
        return texture != null ? texture.getWidth() : 0;
    }

    /**
     * Get the height of the entity
     */
    public int getHeight() {
        return texture != null ? texture.getHeight() : 0;
    }

    /**
     * Get the unique ID of this entity
     */
    public String getEntityID() {
        return entityID;
    }

    /**
     * Get the name of this entity
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Set the name of this entity
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Get the position of this entity as a Vector2
     */
    public Vector2 getPosition() {
        return new Vector2(positionX, positionY);
    }

    /**
     * Set the position of this entity
     */
    public void setPosition(float x, float y) {
        this.positionX = x;
        this.positionY = y;
    }

    /**
     * Handle collision with another entity
     */
    public abstract void onCollision(Entity other);

    /**
     * Dispose entity resources to prevent memory leaks
     */
    public void dispose() {
        // Dispose texture if it exists
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
        
        // Dispose any components that need disposal
        for (Component component : components.values()) {
            if (component instanceof Disposable) {
                ((Disposable) component).dispose();
            }
        }
        
        // Clear components
        components.clear();
    }
}