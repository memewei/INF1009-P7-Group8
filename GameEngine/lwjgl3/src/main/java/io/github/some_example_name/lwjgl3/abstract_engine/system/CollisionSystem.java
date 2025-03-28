package io.github.some_example_name.lwjgl3.abstract_engine.system;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.PhysicsComponent;
import io.github.some_example_name.lwjgl3.abstract_engine.event.EventSystem;
import io.github.some_example_name.lwjgl3.abstract_engine.event.GameEvent;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.ResourceManager;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.NonCollidable;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;


/**
 * System that handles collision detection and resolution
 * through the Box2D physics engine
 */
public class CollisionSystem extends AbstractSystem implements ContactListener {
    private static final Logger LOGGER = Logger.getLogger(CollisionSystem.class.getName());
    
    private World physicsWorld;
    private boolean playCollisionSounds = true;
    private String defaultCollisionSound = "collision.mp3";
    
    /**
     * Create a new collision system
     * @param entityManager The entity manager
     * @param physicsWorld The Box2D physics world
     */
    public CollisionSystem(EntityManager entityManager, World physicsWorld) {
        super(entityManager);
        this.physicsWorld = physicsWorld;
        
        // Set this system as the contact listener for the physics world
        if (physicsWorld != null) {
            physicsWorld.setContactListener(this);
        }
    }
    
    @Override
    protected void initialize() {
        // Add required components
        addRequiredComponent(PhysicsComponent.class);
    }
    
    @Override
    public void update(float deltaTime) {
        if (!enabled || physicsWorld == null) return;
        
        // Box2D physics world update is typically handled by the main game loop
        // This method can be used for non-physics collision detection or additional logic
        
        // Example: Check for overlapping entities without using Box2D physics
        // This could be used for simple trigger areas, collectibles, etc.
        checkOverlappingEntities();
    }
    
    /**
     * Check for overlapping entities using simple bounding box checks
     */
    private void checkOverlappingEntities() {
        List<Entity> entities = getRelevantEntities();
        
        // Check each entity against all other entities
        for (int i = 0; i < entities.size(); i++) {
            Entity entityA = entities.get(i);
            
            // Skip entities marked as non-collidable
            if (entityA instanceof NonCollidable) {
                continue;
            }
            
            // Get physics component
            PhysicsComponent physicsA = entityA.getComponent(PhysicsComponent.class);
            if (physicsA == null) continue;
            
            // Check against all other entities
            for (int j = i + 1; j < entities.size(); j++) {
                Entity entityB = entities.get(j);
                
                // Skip entities marked as non-collidable
                if (entityB instanceof NonCollidable) {
                    continue;
                }
                
                // Get physics component
                PhysicsComponent physicsB = entityB.getComponent(PhysicsComponent.class);
                if (physicsB == null) continue;
                
                // Collision already handled by Box2D, so no need for additional checks here
                // This method could be extended for entities without physics components
            }
        }
    }
    
    /**
     * Check if two entities are overlapping using simple bounding box checks
     */
    private boolean areEntitiesOverlapping(Entity entityA, Entity entityB) {
        // Simple AABB check for non-physics entities
        float aLeft = entityA.getPosition().x;
        float aRight = aLeft + entityA.getWidth();
        float aBottom = entityA.getPosition().y;
        float aTop = aBottom + entityA.getHeight();
        
        float bLeft = entityB.getPosition().x;
        float bRight = bLeft + entityB.getWidth();
        float bBottom = entityB.getPosition().y;
        float bTop = bBottom + entityB.getHeight();
        
        return !(aRight < bLeft || aLeft > bRight || aTop < bBottom || aBottom > bTop);
    }
    
    // ContactListener methods for Box2D physics
    
    @Override
    public void beginContact(Contact contact) {
        if (!enabled) return;
        
        // Get the entities involved in the collision from fixture user data
        Entity entityA = getUserEntity(contact.getFixtureA().getUserData());
        Entity entityB = getUserEntity(contact.getFixtureB().getUserData());
        
        if (entityA == null || entityB == null) {
            return;
        }
        
        boolean collisionHandled = false;
        
        // Handle collision if entities implement Collidable
        if (entityA instanceof Collidable) {
            ((Collidable) entityA).onCollision(entityB);
            collisionHandled = true;
        }
        
        if (entityB instanceof Collidable) {
            ((Collidable) entityB).onCollision(entityA);
            collisionHandled = true;
        }
        
        // Trigger collision event
        EventSystem.getInstance().triggerEvent(
            GameEvent.createCollisionEvent(entityA.getEntityID(), entityB.getEntityID())
        );
        
        // Play collision sound if enabled and collision was handled
        if (playCollisionSounds && collisionHandled) {
            IOManager.getInstance().getAudio().playSound(defaultCollisionSound);
        }
    }
    
    @Override
    public void endContact(Contact contact) {
        // Handle end of contact if needed
    }
    
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Optional pre-solve handling
    }
    
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Optional post-solve handling
    }
    
    /**
     * Set whether collision sounds should be played
     */
    public void setPlayCollisionSounds(boolean play) {
        this.playCollisionSounds = play;
    }
    
    /**
     * Set the default collision sound
     */
    public void setDefaultCollisionSound(String soundFile) {
        this.defaultCollisionSound = soundFile;
    }
    
    /**
     * Extract the entity from fixture user data
     */
    private Entity getUserEntity(Object userData) {
        if (userData instanceof Entity) {
            return (Entity) userData;
        }
        return null;
    }
    
    /**
     * Set the physics world
     */
    public void setPhysicsWorld(World world) {
        if (this.physicsWorld != null) {
            // Remove this system as the contact listener from the old world
            this.physicsWorld.setContactListener(null);
        }
        
        this.physicsWorld = world;
        
        if (world != null) {
            // Set this system as the contact listener for the new world
            world.setContactListener(this);
        }
    }
    
    @Override
    public void dispose() {
        // Cleanup, but don't dispose the physics world as it's managed elsewhere
        if (physicsWorld != null) {
            physicsWorld.setContactListener(null);
        }
        
        LOGGER.log(Level.INFO, "CollisionSystem disposed");
    }
}