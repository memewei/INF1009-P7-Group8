package io.github.some_example_name.lwjgl3.application_classes.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

public class FoodEntity extends Entity implements Collidable {
    private boolean isHealthy;
    private float foodSize;
    private boolean active = true;

    private static final String[] HEALTHY_TEXTURES = {
        "healthy_1.png",
        "healthy_2.png",
        "healthy_3.png",
        "healthy_4.png",
        "healthy_5.png"
    };
    
    private static final String[] UNHEALTHY_TEXTURES = {
        "unhealthy_1.png",
        "unhealthy_2.png",
        "unhealthy_3.png",
        "unhealthy_4.png",
        "unhealthy_5.png"
    };
    
    public static String getRandomTexturePath(boolean isHealthy) {
        if (isHealthy) {
            return HEALTHY_TEXTURES[MathUtils.random(HEALTHY_TEXTURES.length - 1)];
        } else {
            return UNHEALTHY_TEXTURES[MathUtils.random(UNHEALTHY_TEXTURES.length - 1)];
        }
    }    
    
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
        
        String texturePath = getRandomTexturePath(isHealthy);
        String name = isHealthy ? "HealthyFood_" + MathUtils.random(1000) : "UnhealthyFood_" + MathUtils.random(1000);
        
        return new FoodEntity(name, x, y, isHealthy, texturePath);
    }
    
    /**
     * Creates a food entity at a random position within the world
     */
    public static FoodEntity createRandomFoodInWorld(boolean isHealthy, float worldWidth, float worldHeight) {
        float x = MathUtils.random(50, worldWidth - 50);
        float y = MathUtils.random(50, worldHeight - 50);
        
        String texturePath = getRandomTexturePath(isHealthy);
        String name = isHealthy ? "HealthyFood_" + MathUtils.random(1000) : "UnhealthyFood_" + MathUtils.random(1000);
        
        return new FoodEntity(name, x, y, isHealthy, texturePath);
    }
    
    /**
     * Creates a food entity near the specified position
     */
    public static FoodEntity createFoodNearPosition(boolean isHealthy, float centerX, float centerY, float minDist, float maxDist) {
        float angle = MathUtils.random(MathUtils.PI2);
        float distance = MathUtils.random(minDist, maxDist);
        
        float x = centerX + MathUtils.cos(angle) * distance;
        float y = centerY + MathUtils.sin(angle) * distance;
        
        String texturePath = getRandomTexturePath(isHealthy);
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
    
    /**
     * Renders the food at a specific screen position (for infinite world scrolling)
     */
    public void renderAtPosition(SpriteBatch batch, float screenX, float screenY) {
        if (active) {
            batch.draw(texture, 
                    screenX - foodSize/2, 
                    screenY - foodSize/2, 
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
    
    /**
     * Get the distance between this food and a position
     */
    public float distanceTo(float x, float y) {
        return Vector2.dst(positionX, positionY, x, y);
    }
}