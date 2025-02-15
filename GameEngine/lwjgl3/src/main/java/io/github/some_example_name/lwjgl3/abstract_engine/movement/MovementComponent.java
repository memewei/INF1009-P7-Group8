package io.github.some_example_name.lwjgl3.abstract_engine.movement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class MovementComponent {
    private Body body;

    public MovementComponent(World world, float x, float y) {
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

        // Optional: Apply slight damping to reduce velocity over time
        body.setLinearDamping(0.1f);
    }

    //  Handles movement logic directly (Encapsulation)
    public void move(float forceX) {
        body.applyForceToCenter(new Vector2(forceX, 0), true);
    }

    public void jump() {
        if (isGrounded()) {
            body.applyLinearImpulse(new Vector2(0, 5f), body.getWorldCenter(), true);
        }
    }

    public void applyGravity(float gravityForce, float deltaTime) {
        body.applyForceToCenter(new Vector2(0, gravityForce * deltaTime), true);
    }

    public void stop() {
        body.setLinearVelocity(new Vector2(0, 0));
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Vector2 getVelocity() {
        return body.getLinearVelocity();
    }

    public boolean isGrounded() {
        return body.getLinearVelocity().y == 0;
    }

    public void update(float deltaTime) {
        applyGravity(-9.8f, deltaTime); // Moves gravity handling inside MovementComponent
    }
}
