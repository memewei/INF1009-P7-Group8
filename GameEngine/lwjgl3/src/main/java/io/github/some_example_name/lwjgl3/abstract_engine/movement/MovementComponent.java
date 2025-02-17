package io.github.some_example_name.lwjgl3.abstract_engine.movement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;

public class MovementComponent implements IMovable {
    private Body body;
    private MovableEntity owner; // New field for the owning entity

    /**
     * Updated constructor: accepts the owning MovableEntity.
     * When you create a MovementComponent in your MovableEntity, pass "this" as owner.
     */
    public MovementComponent(World world, float x, float y, MovableEntity owner) {
        this.owner = owner;
        // Define the dynamic body in Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        // Define the shape for collision (a box)
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        // Define fixture properties
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.1f;

        // Create the fixture and set its user data to the owning entity.
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(owner);

        shape.dispose(); // Clean up the shape

        // Apply slight damping to reduce perpetual sliding
        body.setLinearDamping(0.1f);
    }

    // Updated move method for top-down movement (force in X and Y)
    @Override
    public void move(float forceX, float forceY) {
        body.setLinearVelocity(new Vector2(forceX, forceY));
    }

    @Override
    public void stop() {
        body.setLinearVelocity(new Vector2(0, 0));
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public void update(float deltaTime) {
        // For top-down movement, no gravity is applied.
    }
}
