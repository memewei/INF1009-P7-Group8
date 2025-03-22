package io.github.some_example_name.lwjgl3.application_classes.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Displays nutrition information as a popup when eating food.
 * Educational component showing calories and nutrition facts.
 */
public class NutritionPopup {
    private String foodName;
    private int calories;
    private String nutritionFact;
    private boolean isHealthy;
    private Vector2 position;
    private Texture backgroundTexture;
    private BitmapFont font;
    private float displayTime;
    private float displayDuration = 3.0f; // Show for 3 seconds
    private boolean isVisible = false;
    
    public NutritionPopup() {
        backgroundTexture = new Texture(Gdx.files.internal("popup_background.png"));
        font = new BitmapFont(Gdx.files.internal("game_font.fnt"));
        font.getData().setScale(0.25f);
        position = new Vector2();
    }
    
    /**
     * Show a nutrition popup for a specific food
     * @param foodName The name of the food
     * @param calories The calorie content
     * @param nutritionFact A fact about the nutritional value
     * @param isHealthy Whether the food is healthy
     * @param x X-coordinate to display the popup
     * @param y Y-coordinate to display the popup
     */
    public void show(String foodName, int calories, String nutritionFact, boolean isHealthy, float x, float y) {
        this.foodName = foodName;
        this.calories = calories;
        this.nutritionFact = nutritionFact;
        this.isHealthy = isHealthy;
        this.position.set(x, y);
        this.displayTime = 0;
        this.isVisible = true;
    }
    
    /**
     * Update the popup timer
     * @param deltaTime Time since last frame
     */
    public void update(float deltaTime) {
        if (isVisible) {
            displayTime += deltaTime;
            if (displayTime >= displayDuration) {
                isVisible = false;
            }
        }
    }
    
    /**
     * Render the nutrition popup
     * @param batch The SpriteBatch to render with
     */
    public void render(SpriteBatch batch) {
        if (!isVisible) {
            return;
        }
        
        // Calculate fade out effect near the end
        float alpha = 1.0f;
        if (displayTime > displayDuration - 0.5f) {
            alpha = (displayDuration - displayTime) / 0.5f;
        }
        
        // Calculate popup position, ensuring it stays on screen
        float popupWidth = 300;
        float popupHeight = 180;
        
        float x = position.x;
        float y = position.y;
        
        // Adjust if would go off screen
        if (x + popupWidth > Gdx.graphics.getWidth()) {
            x = Gdx.graphics.getWidth() - popupWidth - 10;
        }
        if (y + popupHeight > Gdx.graphics.getHeight()) {
            y = Gdx.graphics.getHeight() - popupHeight - 10;
        }
        if (x < 10) x = 10;
        if (y < 10) y = 10;
        
        // Draw background with appropriate alpha
        Color originalColor = batch.getColor();
        Color bgColor = isHealthy ? new Color(0.2f, 0.8f, 0.2f, alpha * 0.9f) : new Color(0.8f, 0.2f, 0.2f, alpha * 0.9f);
        batch.setColor(bgColor);
        batch.draw(backgroundTexture, x, y, popupWidth, popupHeight);
        
        // Draw header with food name
        font.getData().setScale(0.3f);
        Color headerColor = isHealthy ? new Color(0.9f, 1.0f, 0.9f, alpha) : new Color(1.0f, 0.9f, 0.9f, alpha);
        font.setColor(headerColor);
        font.draw(batch, foodName, x + 10, y + popupHeight - 15);
        
        // Draw calories
        font.getData().setScale(0.25f);
        Color calorieColor = new Color(1.0f, 1.0f, 0.8f, alpha);
        font.setColor(calorieColor);
        font.draw(batch, "Calories: " + calories + " kcal", x + 10, y + popupHeight - 45);
        
        // Draw nutrition fact with wrapping
        font.getData().setScale(0.22f);
        Color factColor = new Color(1.0f, 1.0f, 1.0f, alpha);
        font.setColor(factColor);
        font.draw(batch, nutritionFact, x + 10, y + popupHeight - 75, popupWidth - 20, -1, true);
        
        // Reset batch color
        batch.setColor(originalColor);
    }
    
    /**
     * Check if the popup is currently visible
     */
    public boolean isVisible() {
        return isVisible;
    }
    
    /**
     * Dispose of resources
     */
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}