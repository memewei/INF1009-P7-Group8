package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {
	private List<Entity> entities;

	public EntityManager() {
		this.entities = new ArrayList<>();
	}

	public Entity getEntityByID(String entityID) {
		for (Entity entity : entities) {
			if (entity.getEntityID().equals(entityID)) {
				return entity;
			}
		}
		return null;
	}

	public void addEntity(Entity entity) {
		entities.add(entity);
	}

	public void removeEntity(String entityID) {
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
}