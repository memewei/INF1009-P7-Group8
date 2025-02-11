package io.github.some_example_name.lwjgl3;

public class MovableEntity extends Entity {
	private float speed;
	private float direction;
	private ImmovableEntity linkedImmovable;

	public MovableEntity(String entityID, String entityName, float positionX, float positionY, float speed,
			float direction, ImmovableEntity linkedImmovable) {
		super(entityName, positionX, positionY);
		this.speed = speed;
		this.direction = direction;
		this.linkedImmovable = linkedImmovable;
	}

	@Override
	public void update(float deltaTime) {
		// Placeholder for future logic (e.g., animated objects)
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getDirection() {
		return direction;
	}

	public void setDirection(float direction) {
		this.direction = direction;
	}

	public ImmovableEntity getLinkedImmovable() {
		return linkedImmovable;
	}
}
