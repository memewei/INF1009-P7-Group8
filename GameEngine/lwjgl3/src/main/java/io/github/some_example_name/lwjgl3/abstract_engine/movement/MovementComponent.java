package io.github.some_example_name.lwjgl3.abstract_engine.movement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;


//MovementComponent manages a dynamic Box2D body for a MovableEntity in a top-down setup.
//The fixture's user data is set to the MovableEntity (owner) for collision detection.
public class MovementComponent implements IMovable {
    private Body body;
    private MovableEntity owner; // Links this body to the MovableEntity


    //Creates a dynamic body at (x, y) for the given MovableEntity.
    //The 'owner' parameter should be the MovableEntity using this component.
    public MovementComponent(World world, float x, float y, MovableEntity owner) {
        this.owner = owner;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.1f;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(owner);

        shape.dispose();

        // Slight damping to reduce sliding in top-down movement
        body.setLinearDamping(0.1f);
    }

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
        // You can add any per-frame logic if needed.
    }
}
