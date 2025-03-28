package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Component that wraps a Box2D physics body for entities
 */
public class PhysicsComponent implements Component, Disposable {
    private Entity owner;
    private Body body;
    private boolean autoSyncPosition = true;
    
    /**
     * Create a new physics component with the given body
     * @param body The Box2D physics body
     */
    public PhysicsComponent(Body body) {
        this.body = body;
    }
    
    @Override
    public void setOwner(Entity owner) {
        this.owner = owner;
        
        // Update body user data
        if (body != null && body.getFixtureList() != null && body.getFixtureList().size > 0) {
            for (int i = 0; i < body.getFixtureList().size; i++) {
                body.getFixtureList().get(i).setUserData(owner);
            }
        }
    }
    
    @Override
    public Entity getOwner() {
        return owner;
    }
    
    @Override
    public void initialize() {
        // Already initialized in constructor
    }
    
    @Override
    public void update(float deltaTime) {
        // Sync entity position with body position if auto-sync is enabled
        if (autoSyncPosition && owner != null && body != null) {
            Vector2 position = body.getPosition();
            owner.setPosition(position.x, position.y);
        }
    }
    
    /**
     * Get the Box2D physics body
     */
    public Body getBody() {
        return body;
    }
    
    /**
     * Set the position of the body
     */
    public void setPosition(float x, float y) {
        if (body != null) {
            body.setTransform(x, y, body.getAngle());
        }
    }
    
    /**
     * Get the position of the body
     */
    public Vector2 getPosition() {
        return body != null ? body.getPosition() : new Vector2();
    }
    
    /**
     * Set the rotation of the body in radians
     */
    public void setRotation(float angle) {
        if (body != null) {
            body.setTransform(body.getPosition(), angle);
        }
    }
    
    /**
     * Get the rotation of the body in radians
     */
    public float getRotation() {
        return body != null ? body.getAngle() : 0f;
    }
    
    /**
     * Apply a force to the body
     */
    public void applyForce(float forceX, float forceY) {
        if (body != null) {
            body.applyForceToCenter(forceX, forceY, true);
        }
    }
    
    /**
     * Apply an impulse to the body
     */
    public void applyImpulse(float impulseX, float impulseY) {
        if (body != null) {
            body.applyLinearImpulse(impulseX, impulseY, 
                                   body.getPosition().x, body.getPosition().y, true);
        }
    }
    
    /**
     * Set whether the entity position should automatically sync with the body position
     */
    public void setAutoSyncPosition(boolean autoSync) {
        this.autoSyncPosition = autoSync;
    }
    
    /**
     * Check if auto position sync is enabled
     */
    public boolean isAutoSyncPosition() {
        return autoSyncPosition;
    }
    
    /**
     * Set the body type (static, dynamic, kinematic)
     */
    public void setBodyType(BodyType type) {
        if (body != null) {
            switch (type) {
                case STATIC:
                    body.setType(com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody);
                    break;
                case DYNAMIC:
                    body.setType(com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody);
                    break;
                case KINEMATIC:
                    body.setType(com.badlogic.gdx.physics.box2d.BodyDef.BodyType.KinematicBody);
                    break;
            }
        }
    }
    
    @Override
    public void dispose() {
        // The body should be disposed by the World that created it
        // Just nullify the reference here
        body = null;
    }
    
    /**
     * Enum for body types
     */
    public enum BodyType {
        STATIC,
        DYNAMIC,
        KINEMATIC
    }
}