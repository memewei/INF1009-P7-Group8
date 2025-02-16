package io.github.some_example_name.lwjgl3.abstract_engine.movement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class MovementComponent implements IMovable {
    private Body body;

    public MovementComponent(World world, float x, float y) { //might need to have this in MovablEntity
        // Define the body in Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        // Define the shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        // Define physics properties
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.1f;

        body.createFixture(fixtureDef);
        shape.dispose(); // Cleanup

        // Apply slight damping to reduce velocity over time
        body.setLinearDamping(0.1f);
    }

    @Override
    public void move(float forceX) {
        body.setLinearVelocity(new Vector2(forceX, body.getLinearVelocity().y));
    }

    @Override
    public void jump() {
        if (isGrounded()) {
            body.applyLinearImpulse(new Vector2(0, 5f), body.getWorldCenter(), true);
        }
    }

    @Override
    public void stop() {
        body.setLinearVelocity(new Vector2(0, body.getLinearVelocity().y));
    }

    public boolean isGrounded() {
        return body.getLinearVelocity().y == 0;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public void update(float deltaTime) {
        body.applyForceToCenter(new Vector2(0, -9.8f * deltaTime), true);
    }
}
