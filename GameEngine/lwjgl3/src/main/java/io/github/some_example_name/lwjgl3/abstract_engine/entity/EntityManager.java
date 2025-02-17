package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class EntityManager {
    private List<Entity> entities;
    private World world; // World reference for creating Box2D bodies

    // Constructor now accepts a Box2D world
    public EntityManager(World world) {
        this.entities = new ArrayList<>();
        this.world = world;
    }

    public Entity getEntityByID(String entityID) {
        for (Entity entity : entities) {
            if (entity.getEntityID().equals(entityID)) {
                return entity;
            }
        }
        return null;
    }

    // Add entity and create a static body if it's a StaticEntity
    public void addEntity(Entity entity) {
        entities.add(entity);
        if (entity instanceof StaticEntity) {
            createStaticBodyForEntity((StaticEntity) entity);
        }
    }

    public void removeEntity(String entityID) {
        // Optionally: destroy Box2D body if it's a static entity
        entities.removeIf(entity -> entity.getEntityID().equals(entityID));
    }

    public void updateEntities(float deltaTime) {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
    }

    public int getActiveEntitiesCount() {
        return entities.size();
    }
    
    // Render all entities
    public void render(SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);
        }
    }
    
    // Create a Box2D static body for the given StaticEntity
    private void createStaticBodyForEntity(StaticEntity entity) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(entity.getPosition());
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(entity.getWidth() / 2f, entity.getHeight() / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        Fixture fixture = body.createFixture(fixtureDef);
        // Assign the entity as the user data for collision detection
        fixture.setUserData(entity);
        shape.dispose();
    }
}
