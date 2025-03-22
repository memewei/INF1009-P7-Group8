package io.github.some_example_name.lwjgl3.application_classes.game;

/**
 * Manages game levels, difficulty progression, and level-specific settings
 */
public class LevelManager {
    private int currentLevel;
    private float baseSnakeSpeed;
    private float snakeSpeedMultiplier;
    private float snakeSizeMultiplier;
    private int healthyFoodGoal;
    private int unhealthyFoodGoal;
    private String levelDescription;
    private boolean isUnhealthyPath;
    
    // Calories per food item (will be multiplied by level for higher levels)
    private final int BASE_HEALTHY_CALORIES = 100;
    private final int BASE_UNHEALTHY_CALORIES = 300;
    private int healthyCalories;
    private int unhealthyCalories;
    
    public LevelManager() {
        // Initialize with level 1 settings
        resetToLevel(1, false);
    }
    
    /**
     * Reset to a specific level with specified path
     * @param level Level number
     * @param unhealthyPath Whether this level is on the unhealthy progression path
     */
    public void resetToLevel(int level, boolean unhealthyPath) {
        this.currentLevel = level;
        this.isUnhealthyPath = unhealthyPath;
        
        // Base settings
        baseSnakeSpeed = 150f;
        
        if (unhealthyPath) {
            // Unhealthy path - slower snake, larger size
            snakeSpeedMultiplier = Math.max(0.7f - (level * 0.05f), 0.4f);
            snakeSizeMultiplier = 1.0f + (level * 0.2f);
            healthyFoodGoal = 20; // Need more healthy food to balance
            unhealthyFoodGoal = 10 + (level * 2);
            levelDescription = "Unhealthy Level " + level + ": Your snake is slower and larger!";
        } else {
            // Healthy path - faster snake, normal size
            snakeSpeedMultiplier = 1.0f + (level * 0.15f);
            snakeSizeMultiplier = 1.0f;
            healthyFoodGoal = 15 + (level * 3);
            unhealthyFoodGoal = 10;
            levelDescription = "Healthy Level " + level + ": Your snake is faster!";
        }
        
        // Calculate calories based on level
        healthyCalories = BASE_HEALTHY_CALORIES * level;
        unhealthyCalories = BASE_UNHEALTHY_CALORIES * level;
    }
    
    /**
     * Progress to the next level based on current performance
     * @param healthyPercentage Percentage of healthy food goal completed
     * @param unhealthyPercentage Percentage of unhealthy food goal completed
     */
    public void progressToNextLevel(float healthyPercentage, float unhealthyPercentage) {
        boolean nextIsUnhealthyPath = false;
        
        // Determine which path to take
        if (healthyPercentage >= 1.0f && unhealthyPercentage < 1.0f) {
            // Healthy path - player completed healthy goal first
            nextIsUnhealthyPath = false;
        } else if (unhealthyPercentage >= 1.0f && healthyPercentage < 1.0f) {
            // Unhealthy path - player completed unhealthy goal first
            nextIsUnhealthyPath = true;
        } else if (healthyPercentage >= 1.0f && unhealthyPercentage >= 1.0f) {
            // Both goals completed - continue on current path
            nextIsUnhealthyPath = isUnhealthyPath;
        }
        
        // Go to next level
        resetToLevel(currentLevel + 1, nextIsUnhealthyPath);
    }
    
    // Getters
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public float getSnakeSpeed() {
        return baseSnakeSpeed * snakeSpeedMultiplier;
    }
    
    public float getSnakeSizeMultiplier() {
        return snakeSizeMultiplier;
    }
    
    public int getHealthyFoodGoal() {
        return healthyFoodGoal;
    }
    
    public int getUnhealthyFoodGoal() {
        return unhealthyFoodGoal;
    }
    
    public String getLevelDescription() {
        return levelDescription;
    }
    
    public boolean isUnhealthyPath() {
        return isUnhealthyPath;
    }
    
    public int getHealthyCalories() {
        return healthyCalories;
    }
    
    public int getUnhealthyCalories() {
        return unhealthyCalories;
    }
    
    /**
     * Get the total calories consumed based on food counts
     */
    public int calculateCalories(int healthyCount, int unhealthyCount) {
        return (healthyCount * healthyCalories) + (unhealthyCount * unhealthyCalories);
    }
}