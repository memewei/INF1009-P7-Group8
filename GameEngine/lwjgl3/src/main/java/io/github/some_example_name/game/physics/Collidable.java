package io.github.some_example_name.game.physics;

import io.github.some_example_name.game.entities.Entity;

public interface Collidable {
    void onCollision(Entity other);
}
