package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class EntityManager {
    private final World world;
    private final Map<String, Entity> entityMap;  // Stores entities by ID

    public EntityManager(World world) {
        this.world = world;
        this.entityMap = new HashMap<>();
    }

    public Collection<Entity> getAllEntities() {
        return entityMap.values();
    }

    public Entity getEntityByID(String entityID) {
        return entityMap.get(entityID);  // O(1) lookup time
    }

    public boolean hasEntity(String entityID) {
        return getEntityByID(entityID) != null;
    }

    public void addEntity(Entity entity) {
        if (entity == null) {
            System.err.println("[EntityManager] Attempted to add a null entity.");
            return;
        }
    
        // Prevent adding duplicates based on entity ID
        if (hasEntity(entity.getEntityID())) {
            System.out.println("[EntityManager] Entity with ID '" + entity.getEntityID() + "' already exists. Skipping addition.");
            return;
        }
    
        entityMap.put(entity.getEntityID(), entity); // Store in HashMap
    
        // Handle static entities (Box2D physics setup)
        if (entity instanceof StaticEntity) {
            StaticEntity staticEntity = (StaticEntity) entity;

            if (staticEntity.getEntityName().toLowerCase().contains("enemy")) {
                staticEntity.addComponent("Type", "Enemy");
                staticEntity.addComponent("Health", "100");
                staticEntity.addComponent("AI", "Aggressive");
            }

            createStaticBodyForEntity(staticEntity);
        }
    
        System.out.println("[EntityManager] Entity '" + entity.getEntityID() + "' added successfully.");
    }

    public void clearEntities() {
        System.out.println("[EntityManager] Clearing all entities...");
        for (Entity entity : entityMap.values()) {
            entity.dispose();  //Dispose to prevent memory leaks
        }
        entityMap.clear();
    }
    

    public void removeEntity(String entityID) {
        entityMap.remove(entityID);
    }

    public void updateEntities(float deltaTime) {
        for (Entity entity : entityMap.values()) {
            entity.update(deltaTime);
        }
    }

    public int getActiveEntitiesCount() {
        return entityMap.size();
    }

    // Render all entities
    public void render(SpriteBatch batch) {
        for (Entity entity : entityMap.values()) {
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
