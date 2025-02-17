package io.github.some_example_name.lwjgl3.abstract_engine.movement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class MovementComponent implements IMovable {
    private Body body;

    public MovementComponent(World world, float x, float y) {
        // Create a dynamic body for the movable entity.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        // Define the shape.
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        // Define the fixture and physics properties.
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.1f;

        body.createFixture(fixtureDef);
        shape.dispose();

        // Apply slight damping to reduce perpetual sliding.
        body.setLinearDamping(0.1f);
    }

    @Override
    public void move(float forceX, float forceY) {
        // Directly set the linear velocity to the desired values.
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
        // In a top-down game, you likely do not want to apply any extra gravity.
        // If needed, you can include other per-frame logic here.
    }
}
