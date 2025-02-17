package io.github.some_example_name.lwjgl3.abstract_engine.collision;

import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

public class Box2DCollisionListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Object userDataA = contact.getFixtureA().getUserData();
        Object userDataB = contact.getFixtureB().getUserData();

        if (userDataA instanceof Entity && userDataB instanceof Entity) {
            Entity entityA = (Entity) userDataA;
            Entity entityB = (Entity) userDataB;

            if (entityA instanceof Collidable) {
                ((Collidable) entityA).onCollision(entityB);
                System.out.println(entityA.getEntityName() + " collided with " + entityB.getEntityName());
            }
            if (entityB instanceof Collidable) {
                ((Collidable) entityB).onCollision(entityA);
                System.out.println(entityB.getEntityName() + " collided with " + entityA.getEntityName());
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        // Optionally handle end of contact
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) { }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) { }
}
