package io.github.some_example_name.lwjgl3.abstract_engine.movement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Component;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Disposable;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;

/**
 * MovementComponent manages a dynamic Box2D body for an Entity.
 * Implements the Component interface for better integration with the entity system.
 */
public class MovementComponent implements Component, IMovable, Disposable {
    private Body body;
    private Entity owner;
    private World world;
    private float bodyWidth = 1.0f;
    private float bodyHeight = 1.0f;

    private float acceleration = 50f;
    private float maxSpeed = 50f;
    private float friction = 0.9f;
    private float linearDamping = 0.1f;
    
    private Vector2 initialPosition;
    private boolean initialized = false;

    /**
     * Creates a new movement component for an entity.
     * Note: The component must be initialized with the world and position before use.
     */
    public MovementComponent() {
        this.initialPosition = new Vector2();
    }
    
    /**
     * Full constructor that creates the Box2D body immediately
     * @param world The Box2D world
     * @param x Starting X position
     * @param y Starting Y position
     * @param width Width of the physics body
     * @param height Height of the physics body
     */
    public MovementComponent(World world, float x, float y, float width, float height) {
        this.world = world;
        this.initialPosition = new Vector2(x, y);
        this.bodyWidth = width;
        this.bodyHeight = height;
        
        // Create physics body if world is available
        if (world != null) {
            createBody(x, y);
            initialized = true;
        }
    }
    
    /**
     * Create the Box2D physics body
     */
    private void createBody(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(bodyWidth / 2f, bodyHeight / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.1f;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(owner);

        shape.dispose();

        // Slight damping to reduce sliding in top-down movement
        body.setLinearDamping(linearDamping);
    }

    @Override
    public void setOwner(Entity owner) {
        this.owner = owner;
        
        // Update fixture user data if body exists
        if (body != null) {
            for (Fixture fixture : body.getFixtureList()) {
                fixture.setUserData(owner);
            }
        }
    }

    @Override
    public Entity getOwner() {
        return owner;
    }

    @Override
    public void initialize() {
        // If already initialized or world is null, do nothing
        if (initialized || world == null) {
            return;
        }
        
        // Create the physics body
        createBody(initialPosition.x, initialPosition.y);
        initialized = true;
    }
    
    /**
     * Set the physics world for this component
     * @param world The Box2D world
     */
    public void setWorld(World world) {
        if (this.world == null) {
            this.world = world;
            if (!initialized && initialPosition != null) {
                createBody(initialPosition.x, initialPosition.y);
                initialized = true;
            }
        }
    }

    @Override
    public void move(float forceX, float forceY) {
        if (body == null) return;
        
        Vector2 velocity = body.getLinearVelocity();
        
        // Apply acceleration forces
        velocity.x += forceX * acceleration;
        velocity.y += forceY * acceleration;
        
        // Apply speed limits if needed
        if (maxSpeed > 0) {
            if (velocity.len() > maxSpeed) {
                velocity.nor().scl(maxSpeed);
            }
        }
        
        // Apply friction
        velocity.x *= friction;
        velocity.y *= friction;
        
        // Set the new velocity
        body.setLinearVelocity(velocity);
    }

    @Override
    public void stop() {
        if (body == null) return;
        
        Vector2 velocity = body.getLinearVelocity();
        velocity.x *= friction;
        velocity.y *= friction;
        body.setLinearVelocity(velocity);
    }

    /**
     * Get the current position from the physics body
     */
    public Vector2 getPosition() {
        return body != null ? body.getPosition() : initialPosition;
    }
    
    /**
     * Set the position of the physics body
     */
    public void setPosition(float x, float y) {
        if (body != null) {
            body.setTransform(x, y, body.getAngle());
        } else {
            initialPosition.set(x, y);
        }
    }
    
    /**
     * Get the current rotation of the physics body in radians
     */
    public float getRotation() {
        return body != null ? body.getAngle() : 0f;
    }
    
    /**
     * Set the rotation of the physics body in radians
     */
    public void setRotation(float angle) {
        if (body != null) {
            body.setTransform(body.getPosition(), angle);
        }
    }

    /**
     * Update the component state
     */
    @Override
    public void update(float deltaTime) {
        // Apply default behavior (gradual stopping)
        stop();
        
        // Update owner entity position if it exists
        if (owner != null && body != null) {
            Vector2 position = body.getPosition();
            owner.setPosition(position.x, position.y);
        }
    }
    
    /**
     * Set the movement parameters
     */
    public void setMovementParameters(float acceleration, float maxSpeed, float friction) {
        this.acceleration = acceleration;
        this.maxSpeed = maxSpeed;
        this.friction = friction;
    }
    
    /**
     * Set the body dimensions for collision
     */
    public void setBodyDimensions(float width, float height) {
        // Store new dimensions
        this.bodyWidth = width;
        this.bodyHeight = height;
        
        // If body exists, recreate it with new dimensions
        if (body != null && world != null) {
            Vector2 position = body.getPosition();
            float angle = body.getAngle();
            
            // Remove old body
            world.destroyBody(body);
            
            // Create new body with updated dimensions
            createBody(position.x, position.y);
            body.setTransform(position, angle);
        }
    }
    
    /**
     * Get access to the underlying Box2D body
     */
    public Body getBody() {
        return body;
    }

    /**
     * Clean up resources
     */
    @Override
    public void dispose() {
        if (body != null && world != null) {
            world.destroyBody(body);
            body = null;
        }
    }
}