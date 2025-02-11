package io.github.some_example_name.lwjgl3;

import java.util.HashMap;
import java.util.Map;

public class ImmovableEntity extends Entity {
	private Map<String, String> componentData;

	public ImmovableEntity(String entityID, String entityName, float positionX, float positionY) {
		super(entityName, positionX, positionY);
		this.componentData = new HashMap<>();
	}

	public void setComponent(String key, String value) {
		componentData.put(key, value);
	}

	public String getComponent(String key) {
		return componentData.getOrDefault(key, null);
	}

	public void removeComponent(String key) {
		componentData.remove(key);
	}

	@Override
	public void update(float deltaTime) {
		// Placeholder for future logic (e.g., animated objects)
	}
}
