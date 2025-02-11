package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Vector2;
import java.util.UUID;

public abstract class Entity {
	protected String entityID;
	protected String entityName;
	protected float positionX;
	protected float positionY;

	public Entity(String entityName, float positionX, float positionY) {
		this.entityID = UUID.randomUUID().toString(); //Unique Entity ID
		this.entityName = entityName;
		this.positionX = positionX;
		this.positionY = positionY;
	}

	public abstract void update(float deltaTime); // Abstract method to update movement of entity

	//Return Entity ID
	public String getEntityID() {
		return entityID;
	}
	//Return Entity Name
	public String getEntityName() {
		return entityName;
	}
	//Change Entity Name
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	//Return Current Entity Location
	public Vector2 getPosition() {
		return new Vector2(positionX, positionY);
	}
	//Change Entity Location
	public void setPosition(float x, float y) {
		this.positionX = x;
		this.positionY = y;
	}
}
