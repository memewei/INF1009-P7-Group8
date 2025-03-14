package io.github.some_example_name.lwjgl3.application_classes.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

public class FoodEntity extends Entity implements Collidable {
    private boolean isHealthy;
    private float foodSize;
    private boolean active = true;
    
    public FoodEntity(String entityName, float positionX, float positionY, boolean isHealthy, String texturePath) {
        super(entityName, positionX, positionY, texturePath);
        this.isHealthy = isHealthy;
        this.foodSize = isHealthy ? 20f : 30f; // Unhealthy food is larger
    }
    
    /**
     * Creates a food entity at a random position on the screen
     */
    public static FoodEntity createRandomFood(boolean isHealthy) {
        float x = MathUtils.random(50, Gdx.graphics.getWidth() - 50);
        float y = MathUtils.random(50, Gdx.graphics.getHeight() - 50);
        
        String texturePath = isHealthy ? "healthy_food.png" : "unhealthy_food.png";
        String name = isHealthy ? "HealthyFood_" + MathUtils.random(1000) : "UnhealthyFood_" + MathUtils.random(1000);
        
        return new FoodEntity(name, x, y, isHealthy, texturePath);
    }
    
    @Override
    public void update(float deltaTime) {
        // Food doesn't need to update, it's static
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, 
                    positionX - foodSize/2, 
                    positionY - foodSize/2, 
                    foodSize, foodSize);
        }
    }
    
    public boolean isHealthy() {
        return isHealthy;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(positionX - foodSize/2, positionY - foodSize/2, foodSize, foodSize);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void deactivate() {
        active = false;
    }
    
    @Override
    public void onCollision(Entity other) {
        if (other instanceof SnakePlayer) {
            // Deactivate the food when the player collides with it
            deactivate();
        }
    }
}