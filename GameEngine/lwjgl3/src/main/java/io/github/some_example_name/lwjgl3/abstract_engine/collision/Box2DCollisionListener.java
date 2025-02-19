package io.github.some_example_name.lwjgl3.abstract_engine.collision;

import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;

public class Box2DCollisionListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Object userDataA = contact.getFixtureA().getUserData();
        Object userDataB = contact.getFixtureB().getUserData();

        if (userDataA instanceof Entity && userDataB instanceof Entity) {
            Entity entityA = (Entity) userDataA;
            Entity entityB = (Entity) userDataB;

            boolean collisionHandled = false;

            if (entityA instanceof Collidable) {
                ((Collidable) entityA).onCollision(entityB);
                if (!collisionHandled) {
                    IOManager.getInstance().getAudio().playSound("hit_sound.mp3");
                    collisionHandled = true;
                }
            }

            if (entityB instanceof Collidable) {
                ((Collidable) entityB).onCollision(entityA);
                if (!collisionHandled) {
                    IOManager.getInstance().getAudio().playSound("hit_sound.mp3");
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        // Optionally handle end of contact
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
