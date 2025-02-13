package io.github.some_example_name.lwjgl3.abstract_engine.collision;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

public interface Collidable {
    void onCollision(Entity other);
}
