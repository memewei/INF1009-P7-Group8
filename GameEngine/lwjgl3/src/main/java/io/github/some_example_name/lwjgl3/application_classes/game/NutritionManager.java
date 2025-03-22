package io.github.some_example_name.lwjgl3.application_classes.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Manages nutritional information and educational content about food items
 * in the game to support the educational aspect.
 */
public class NutritionManager {
    // Singleton instance
    private static NutritionManager instance;
    
    // Maps food texture names to nutritional info and educational facts
    private Map<String, FoodInfo> healthyFoodInfo;
    private Map<String, FoodInfo> unhealthyFoodInfo;
    private Random random;
    
    private NutritionManager() {
        random = new Random();
        initializeFoodInformation();
    }
    
    /**
     * Get the singleton instance
     */
    public static NutritionManager getInstance() {
        if (instance == null) {
            instance = new NutritionManager();
        }
        return instance;
    }
    
    /**
     * Initialize the food information database
     */
    private void initializeFoodInformation() {
        healthyFoodInfo = new HashMap<>();
        unhealthyFoodInfo = new HashMap<>();
        
        // Populate with sample data
        // Healthy foods
        addHealthyFood("healthy_1.png", "Apple", 95, 
                      "Apples are rich in fiber and vitamin C. They can help reduce the risk of chronic diseases.");
                      
        addHealthyFood("healthy_2.png", "Broccoli", 55, 
                      "Broccoli is packed with vitamins, minerals, fiber and antioxidants that support overall health.");
                      
        addHealthyFood("healthy_3.png", "Carrot", 50, 
                      "Carrots are rich in beta-carotene, which is converted to vitamin A in the body. Good for eye health.");
                      
        addHealthyFood("healthy_4.png", "Banana", 105, 
                      "Bananas are a good source of potassium and provide quick energy before or after exercise.");
                      
        addHealthyFood("healthy_5.png", "Orange", 62, 
                      "Oranges are high in vitamin C, which helps support immune function and collagen production.");
        
        // Unhealthy foods
        addUnhealthyFood("unhealthy_1.png", "Burger", 550, 
                        "Fast food burgers are high in calories, unhealthy fats, sodium, and have little nutritional value.");
                        
        addUnhealthyFood("unhealthy_2.png", "Pizza", 285, 
                        "Pizza slices can be high in calories, saturated fat, and sodium, especially with processed meats.");
                        
        addUnhealthyFood("unhealthy_3.png", "French Fries", 365, 
                         "French fries are fried in oil, making them high in calories and unhealthy fats.");
                         
        addUnhealthyFood("unhealthy_4.png", "Soda", 150, 
                         "Sodas contain a lot of added sugar with no nutritional value, contributing to obesity and tooth decay.");
                         
        addUnhealthyFood("unhealthy_5.png", "Donut", 250, 
                         "Donuts are high in sugar, refined carbs, and unhealthy fats with little nutritional benefits.");
    }
    
    /**
     * Add information about a healthy food
     */
    private void addHealthyFood(String textureName, String foodName, int calories, String educationalFact) {
        healthyFoodInfo.put(textureName, new FoodInfo(foodName, calories, educationalFact, true));
    }
    
    /**
     * Add information about an unhealthy food
     */
    private void addUnhealthyFood(String textureName, String foodName, int calories, String educationalFact) {
        unhealthyFoodInfo.put(textureName, new FoodInfo(foodName, calories, educationalFact, false));
    }
    
    /**
     * Get information about a food item from its texture name
     * @param textureName The filename of the food texture
     * @param isHealthy Whether the food is healthy or unhealthy
     * @return FoodInfo object with nutritional and educational information
     */
    public FoodInfo getFoodInfo(String textureName, boolean isHealthy) {
        // Strip path information if present
        String filename = textureName;
        int lastSlash = textureName.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < textureName.length() - 1) {
            filename = textureName.substring(lastSlash + 1);
        }
        
        if (isHealthy) {
            return healthyFoodInfo.getOrDefault(filename, 
                new FoodInfo("Healthy Food", 100, "This healthy food provides essential nutrients.", true));
        } else {
            return unhealthyFoodInfo.getOrDefault(filename,
                new FoodInfo("Unhealthy Food", 300, "This unhealthy food is high in calories and low in nutrients.", false));
        }
    }
    
    /**
     * Get a random educational fact about healthy eating
     */
    public String getRandomHealthyEatingFact() {
        String[] facts = {
            "Eating a variety of fruits and vegetables provides essential vitamins and minerals.",
            "Whole grains contain fiber, which helps with digestion and keeps you feeling full longer.",
            "Protein is essential for building and repairing tissues in your body.",
            "Drinking water instead of sugary drinks can help reduce calorie intake.",
            "Balanced meals containing protein, healthy fats, and complex carbohydrates help maintain energy.",
            "Portion control is just as important as food choice for maintaining healthy weight.",
            "Cooking at home lets you control ingredients and often results in healthier meals.",
            "Reading nutrition labels helps you make informed food choices.",
            "Eating slowly helps your body recognize when it's full, preventing overeating.",
            "Regular physical activity combined with a balanced diet is key to overall health."
        };
        
        int randomIndex = random.nextInt(facts.length);
        return facts[randomIndex];
    }
    
    /**
     * Class to store information about a food item
     */
    public static class FoodInfo {
        private String foodName;
        private int calories;
        private String educationalFact;
        private boolean isHealthy;
        
        public FoodInfo(String foodName, int calories, String educationalFact, boolean isHealthy) {
            this.foodName = foodName;
            this.calories = calories;
            this.educationalFact = educationalFact;
            this.isHealthy = isHealthy;
        }
        
        public String getFoodName() {
            return foodName;
        }
        
        public int getCalories() {
            return calories;
        }
        
        public String getEducationalFact() {
            return educationalFact;
        }
        
        public boolean isHealthy() {
            return isHealthy;
        }
    }
}