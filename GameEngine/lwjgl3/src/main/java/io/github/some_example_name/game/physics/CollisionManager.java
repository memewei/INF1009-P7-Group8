package io.github.some_example_name.game.physics;

import io.github.some_example_name.game.entities.Entity;
import java.util.List;
import java.util.ArrayList;

public class CollisionManager {
    private List<Collidable> collidableEntities;

    public CollisionManager() {
        this.collidableEntities = new ArrayList<>();
    }

    public void addCollidable(Collidable entity) {
        if (entity instanceof Entity) { // Ensure only Entities are added
            collidableEntities.add(entity);
        } else {
            throw new IllegalArgumentException("Only entities can be added as collidables.");
        }
    }

    public void removeCollidable(Collidable entity) {
        collidableEntities.remove(entity);
    }

    public void checkCollisions() {
        int size = collidableEntities.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Entity a = (Entity) collidableEntities.get(i);
                Entity b = (Entity) collidableEntities.get(j);

                if (isColliding(a, b)) {
                    if (a instanceof Collidable) {
                        ((Collidable) a).onCollision(b);
                    }
                    if (b instanceof Collidable) {
                        ((Collidable) b).onCollision(a);
                    }
                }
            }
        }
    }

    private boolean isColliding(Entity a, Entity b) {
        // Ensures width/height exist to prevent runtime errors
        if (a.getWidth() == 0 || a.getHeight() == 0 || b.getWidth() == 0 || b.getHeight() == 0) {
            return false; // Avoid collision detection with missing dimensions
        }

        return (Math.abs(a.getPosition().x - b.getPosition().x) < (a.getWidth() / 2 + b.getWidth() / 2)) &&
               (Math.abs(a.getPosition().y - b.getPosition().y) < (a.getHeight() / 2 + b.getHeight() / 2));
    }
}
