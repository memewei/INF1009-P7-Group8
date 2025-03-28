package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import io.github.some_example_name.lwjgl3.application_classes.component.EnemyComponent;
import io.github.some_example_name.lwjgl3.application_classes.component.HealthComponent;

/**
 * Manages entity lifecycle, organization, and rendering
 * Provides an event system for entity changes
 */
public class EntityManager implements Disposable {
    private static final Logger LOGGER = Logger.getLogger(EntityManager.class.getName());
    
    // Entity storage
    private final Map<String, Entity> entityMap;  // For O(1) lookup by ID
    private final Map<Class<?>, List<Entity>> entityTypeMap; // For quick lookup by type
    private final World world;
    
    // Event listeners
    private final List<EntityListener> entityListeners;
    
    /**
     * Create a new EntityManager
     * @param world The Box2D world for physics entities
     */
    public EntityManager(World world) {
        this.world = world;
        this.entityMap = new HashMap<>();
        this.entityTypeMap = new HashMap<>();
        this.entityListeners = new CopyOnWriteArrayList<>(); // Thread-safe for iteration during modification
        
        LOGGER.log(Level.INFO, "EntityManager initialized with Box2D world");
    }
    
    /**
     * Get all entities
     */
    public Collection<Entity> getAllEntities() {
        return entityMap.values();
    }
    
    /**
     * Get a specific entity by ID
     * @param entityID The unique ID of the entity
     * @return The entity or null if not found
     */
    public Entity getEntityByID(String entityID) {
        return entityMap.get(entityID);  // O(1) lookup time
    }
    
    /**
     * Check if an entity with the specified ID exists
     * @param entityID The unique ID of the entity
     * @return true if the entity exists
     */
    public boolean hasEntity(String entityID) {
        return entityMap.containsKey(entityID);
    }
    
    /**
     * Get all entities of a specific type
     * @param <T> The entity type
     * @param entityClass The class of entities to retrieve
     * @return List of entities of the specified type
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity> List<T> getEntitiesByType(Class<T> entityClass) {
        List<Entity> entities = entityTypeMap.get(entityClass);
        
        if (entities == null) {
            return new ArrayList<>();
        }
        
        // Safe cast because we only add entities of the correct type to each list
        return (List<T>) new ArrayList<>(entities);
    }
    
    /**
     * Add an entity to the manager
     * @param entity The entity to add
     * @return true if the entity was successfully added
     */
    public boolean addEntity(Entity entity) {
        if (entity == null) {
            LOGGER.log(Level.WARNING, "Attempted to add a null entity.");
            return false;
        }
    
        // Prevent adding duplicates based on entity ID
        if (hasEntity(entity.getEntityID())) {
            LOGGER.log(Level.INFO, "Entity with ID '{0}' already exists. Skipping addition.", entity.getEntityID());
            return false;
        }
    
        // Store in main map
        entityMap.put(entity.getEntityID(), entity);
        
        // Store in type map
        Class<?> entityClass = entity.getClass();
        addEntityToTypeMap(entity, entityClass);
        
        // Also add to parent type maps for polymorphic lookup
        Class<?> superClass = entityClass.getSuperclass();
        while (superClass != null && Entity.class.isAssignableFrom(superClass)) {
            addEntityToTypeMap(entity, superClass);
            superClass = superClass.getSuperclass();
        }
        
        // Handle static entities (Box2D physics setup)
        if (entity instanceof StaticEntity) {
            handleStaticEntity((StaticEntity) entity);
        }
        
        // Notify listeners
        notifyEntityAdded(entity);
        
        LOGGER.log(Level.FINE, "Entity '{0}' added successfully.", entity.getEntityID());
        return true;
    }
    
    /**
     * Add an entity to the type map
     */
    private void addEntityToTypeMap(Entity entity, Class<?> type) {
        entityTypeMap.computeIfAbsent(type, k -> new ArrayList<>()).add(entity);
    }
    
    /**
     * Handle static entity creation
     */
    private void handleStaticEntity(StaticEntity entity) {
        if (world == null) return;
        
        // Add enemy metadata if appropriate
        if (entity.getEntityName().toLowerCase().contains("enemy")) {
            entity.addComponent(new EnemyComponent());
            entity.addComponent(new HealthComponent(100));
        }

        createStaticBodyForEntity(entity);
    }
    
    /**
     * Remove all entities from the manager
     */
    public void clearEntities() {
        LOGGER.log(Level.INFO, "Clearing all entities ({0} total)...", entityMap.size());
        
        // Dispose each entity to prevent memory leaks
        for (Entity entity : entityMap.values()) {
            notifyEntityRemoved(entity);
            entity.dispose();
        }
        
        entityMap.clear();
        entityTypeMap.clear();
    }
    
    /**
     * Remove a specific entity by ID
     * @param entityID The unique ID of the entity to remove
     * @return The removed entity, or null if not found
     */
    public Entity removeEntity(String entityID) {
        Entity entity = entityMap.remove(entityID);
        
        if (entity != null) {
            // Remove from type maps
            for (List<Entity> entityList : entityTypeMap.values()) {
                entityList.remove(entity);
            }
            
            // Notify listeners
            notifyEntityRemoved(entity);
            LOGGER.log(Level.FINE, "Entity '{0}' removed.", entityID);
        }
        
        return entity;
    }
    
    /**
     * Update all entities
     * @param deltaTime Time since last update
     */
    public void updateEntities(float deltaTime) {
        for (Entity entity : entityMap.values()) {
            entity.update(deltaTime);
        }
    }
    
    /**
     * Get the number of active entities
     */
    public int getActiveEntitiesCount() {
        return entityMap.size();
    }
    
    /**
     * Render all entities
     * @param batch The SpriteBatch to render with
     */
    public void render(SpriteBatch batch) {
        for (Entity entity : entityMap.values()) {
            entity.render(batch);
        }
    }
    
    /**
     * Create a Box2D static body for the given StaticEntity
     */
    private void createStaticBodyForEntity(StaticEntity entity) {
        if (world == null) return;
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(entity.getPosition());
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(entity.getWidth() / 2f, entity.getHeight() / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        Fixture fixture = body.createFixture(fixtureDef);
        
        // Assign the entity as the user data for collision detection
        fixture.setUserData(entity);
        
        // Store body in a component for future reference
        PhysicsComponent physicsComponent = new PhysicsComponent(body);
        entity.addComponent(physicsComponent);
        
        shape.dispose();
    }
    
    /**
     * Add an entity listener
     * @param listener The listener to add
     */
    public void addEntityListener(EntityListener listener) {
        if (listener != null && !entityListeners.contains(listener)) {
            entityListeners.add(listener);
        }
    }
    
    /**
     * Remove an entity listener
     * @param listener The listener to remove
     */
    public void removeEntityListener(EntityListener listener) {
        entityListeners.remove(listener);
    }
    
    /**
     * Notify listeners that an entity was added
     */
    private void notifyEntityAdded(Entity entity) {
        for (EntityListener listener : entityListeners) {
            listener.onEntityAdded(entity);
        }
    }
    
    /**
     * Notify listeners that an entity was removed
     */
    private void notifyEntityRemoved(Entity entity) {
        for (EntityListener listener : entityListeners) {
            listener.onEntityRemoved(entity);
        }
    }
    
    /**
     * Dispose the manager and all entities
     */
    @Override
    public void dispose() {
        clearEntities();
        entityListeners.clear();
        LOGGER.log(Level.INFO, "EntityManager disposed");
    }
    
    /**
     * Interface for entity event listeners
     */
    public interface EntityListener {
        /**
         * Called when an entity is added to the manager
         */
        void onEntityAdded(Entity entity);
        
        /**
         * Called when an entity is removed from the manager
         */
        void onEntityRemoved(Entity entity);
    }
}