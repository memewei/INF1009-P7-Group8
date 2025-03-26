package io.github.some_example_name.lwjgl3.application_classes.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.application_classes.game.NutritionManager;

/**
 * FoodEntity with improved OOP practices
 * - Added setters for food properties
 * - Removed static factory methods (moved to FoodEntityFactory)
 * - Added JavaDoc comments
 */
public class FoodEntity extends Entity implements Collidable {
    private boolean isHealthy;
    private float foodSize;
    private boolean active = true;
    private int calories;
    private String foodName;
    private String educationalFact;
    
    /**
     * Create a new food entity
     */
    public FoodEntity(String entityName, float positionX, float positionY, boolean isHealthy, String texturePath) {
        super(entityName, positionX, positionY, texturePath);
        this.isHealthy = isHealthy;
        // Size based on food type
        this.foodSize = isHealthy ? 40f : 42f;
        
        // Get food information from the nutrition manager
        NutritionManager.FoodInfo info = NutritionManager.getInstance().getFoodInfo(texturePath, isHealthy);
        this.calories = info.getCalories();
        this.foodName = info.getFoodName();
        this.educationalFact = info.getEducationalFact();
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
    
    /**
     * Is this a healthy food
     */
    public boolean isHealthy() {
        return isHealthy;
    }
    
    /**
     * Set whether this is a healthy food
     */
    public void setHealthy(boolean healthy) {
        isHealthy = healthy;
    }
    
    /**
     * Get the collision bounds of this food
     */
    public Rectangle getBounds() {
        return new Rectangle(positionX - foodSize/2, positionY - foodSize/2, foodSize, foodSize);
    }
    
    /**
     * Is this food active (available to be eaten)
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Deactivate this food (after being eaten)
     */
    public void deactivate() {
        active = false;
    }
    
    /**
     * Get the calorie value of this food
     */
    public int getCalories() {
        return calories;
    }
    
    /**
     * Set the calorie value of this food
     */
    public void setCalories(int calories) {
        this.calories = calories;
    }
    
    /**
     * Get the name of this food
     */
    public String getFoodName() {
        return foodName;
    }
    
    /**
     * Set the name of this food
     */
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
    
    /**
     * Get the educational fact for this food
     */
    public String getEducationalFact() {
        return educationalFact;
    }
    
    /**
     * Set the educational fact for this food
     */
    public void setEducationalFact(String educationalFact) {
        this.educationalFact = educationalFact;
    }
    
    /**
     * Get the food size
     */
    public float getFoodSize() {
        return foodSize;
    }
    
    /**
     * Set the food size
     */
    public void setFoodSize(float foodSize) {
        this.foodSize = foodSize;
    }
    
    @Override
    public void onCollision(Entity other) {
        if (other instanceof SnakePlayer) {
            // Deactivate the food when the player collides with it
            deactivate();
        }
    }
    
    /**
     * Get the position as a Vector2
     */
    public Vector2 getPosition() {
        return new Vector2(positionX, positionY);
    }
    
    /**
     * Get the distance between this food and a position
     */
    public float distanceTo(float x, float y) {
        return Vector2.dst(positionX, positionY, x, y);
    }
}