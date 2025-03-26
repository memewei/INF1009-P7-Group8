package io.github.some_example_name.lwjgl3.application_classes.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.ui.AssetPaths;
import io.github.some_example_name.lwjgl3.application_classes.game.LevelManager;

/**
 * Factory class for creating food entities
 * This improves OOP by encapsulating food creation logic
 */
public class FoodEntityFactory {
    
    /**
     * Get a random texture path based on whether the food is healthy
     */
    public static String getRandomTexturePath(boolean isHealthy) {
        if (isHealthy) {
            return AssetPaths.HEALTHY_FOOD[MathUtils.random(AssetPaths.HEALTHY_FOOD.length - 1)];
        } else {
            return AssetPaths.UNHEALTHY_FOOD[MathUtils.random(AssetPaths.UNHEALTHY_FOOD.length - 1)];
        }
    }
    
    /**
     * Creates a food entity at a random position on the screen
     */
    public static FoodEntity createRandomFood(boolean isHealthy, int screenWidth, int screenHeight) {
        float x = MathUtils.random(50, screenWidth - 50);
        float y = MathUtils.random(50, screenHeight - 50);
        
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
    
    /**
     * Creates food near player with smart positioning logic
     */
    public static FoodEntity createFoodNearPlayer(boolean isHealthy, Vector2 playerPos, float worldWidth, float worldHeight) {
        // Calculate the maximum possible spawn distance in each direction without going outside world bounds
        float maxDistanceLeft = Math.min(800, playerPos.x - 50);
        float maxDistanceRight = Math.min(800, worldWidth - 50 - playerPos.x);
        float maxDistanceDown = Math.min(800, playerPos.y - 50);
        float maxDistanceUp = Math.min(800, worldHeight - 50 - playerPos.y);

        // Generate position using a smarter approach
        float x, y;
        float spawnDistance;
        float angle;

        // If player is too close to any edge, use a different spawn strategy
        if (maxDistanceLeft < 100 || maxDistanceRight < 100 || maxDistanceDown < 100 || maxDistanceUp < 100) {
            // Player is near an edge - choose a position away from the nearest edge
            if (playerPos.x < worldWidth / 2) {
                // Player closer to left edge, spawn to the right
                x = playerPos.x + MathUtils.random(100, maxDistanceRight);
            } else {
                // Player closer to right edge, spawn to the left
                x = playerPos.x - MathUtils.random(100, maxDistanceLeft);
            }

            if (playerPos.y < worldHeight / 2) {
                // Player closer to bottom edge, spawn above
                y = playerPos.y + MathUtils.random(100, maxDistanceUp);
            } else {
                // Player closer to top edge, spawn below
                y = playerPos.y - MathUtils.random(100, maxDistanceDown);
            }
        } else {
            // Player is far from edges - use normal radial distribution
            // Try to find a suitable position (with retries)
            int attempts = 0;
            do {
                angle = MathUtils.random(MathUtils.PI2);
                spawnDistance = MathUtils.random(300, 800);

                x = playerPos.x + MathUtils.cos(angle) * spawnDistance;
                y = playerPos.y + MathUtils.sin(angle) * spawnDistance;

                // Adjust position if it would be outside bounds
                if (x < 50) x = 50 + MathUtils.random(50);
                if (x > worldWidth - 50) x = worldWidth - 50 - MathUtils.random(50);
                if (y < 50) y = 50 + MathUtils.random(50);
                if (y > worldHeight - 50) y = worldHeight - 50 - MathUtils.random(50);

                attempts++;
            } while (Vector2.dst(x, y, playerPos.x, playerPos.y) < 300 && attempts < 10);
        }

        // Create the food entity
        String texturePath = getRandomTexturePath(isHealthy);
        String name = isHealthy ? "HealthyFood_" + MathUtils.random(1000) : "UnhealthyFood_" + MathUtils.random(1000);
        
        return new FoodEntity(name, x, y, isHealthy, texturePath);
    }
    
    /**
     * Creates food with calories scaled by level
     */
    public static FoodEntity createLevelScaledFood(boolean isHealthy, float x, float y, LevelManager levelManager) {
        String texturePath = getRandomTexturePath(isHealthy);
        String name = isHealthy ? "HealthyFood_" + MathUtils.random(1000) : "UnhealthyFood_" + MathUtils.random(1000);
        
        FoodEntity food = new FoodEntity(name, x, y, isHealthy, texturePath);
        
        // Scale calories based on level
        int calories = isHealthy ? levelManager.getHealthyCalories() : levelManager.getUnhealthyCalories();
        food.setCalories(calories);
        
        return food;
    }
}