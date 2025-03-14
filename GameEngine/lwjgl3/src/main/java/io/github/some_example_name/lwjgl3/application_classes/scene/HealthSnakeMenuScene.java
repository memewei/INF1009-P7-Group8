package io.github.some_example_name.lwjgl3.application_classes.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameState;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;

public class HealthSnakeMenuScene extends Scene {
    private Texture backgroundTexture;
    private Texture titleTexture;
    private SpriteBatch batch;
    private SceneManager sceneManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private BitmapFont font;
    
    private String[] menuItems = {
        "Start Game",
        "How to Play",
        "Exit"
    };
    
    private int selectedItem = 0;
    private float timeElapsed;
    private boolean showingInstructions = false;
    private Texture instructionsTexture;

    public HealthSnakeMenuScene(SpriteBatch batch, SceneManager sceneManager, 
                             EntityManager entityManager, MovementManager movementManager) {
        this.batch = batch;
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.timeElapsed = 0;
        
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2.0f);
    }

    @Override
    public void initialize() {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("snake_menu_bg.png"));
            titleTexture = new Texture(Gdx.files.internal("health_snake_title.png"));
            instructionsTexture = new Texture(Gdx.files.internal("instructions.png"));
            
            System.out.println("[HealthSnakeMenuScene] Textures loaded successfully.");
        } catch (Exception e) {
            System.err.println("[HealthSnakeMenuScene] Error loading textures: " + e.getMessage());
            // Fallback textures or placeholder handling
        }
        
        // Start menu music
        IOManager.getInstance().getAudio().playMusic("menu_music.mp3");
    }

    @Override
    public void update(float deltaTime) {
        timeElapsed += deltaTime;
        
        if (showingInstructions) {
            // If instructions are showing, pressing any key returns to menu
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                showingInstructions = false;
            }
            return;
        }
        
        // Menu navigation
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.UP)) {
            selectedItem = (selectedItem - 1 + menuItems.length) % menuItems.length;
            IOManager.getInstance().getAudio().playSound("menu_move.mp3");
        } else if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.DOWN)) {
            selectedItem = (selectedItem + 1) % menuItems.length;
            IOManager.getInstance().getAudio().playSound("menu_move.mp3");
        }
        
        // Menu selection
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.ENTER)) {
            handleMenuSelection();
        }
    }
    
    private void handleMenuSelection() {
        IOManager.getInstance().getAudio().playSound("menu_select.mp3");
        
        switch (selectedItem) {
            case 0: // Start Game
                System.out.println("[HealthSnakeMenuScene] Starting game...");
                sceneManager.changeScene(
                    new HealthSnakeGameScene(
                        batch,
                        entityManager,
                        movementManager,
                        sceneManager.getWorld(),
                        sceneManager
                    ), 
                    GameState.RUNNING
                );
                break;
                
            case 1: // How to Play
                showingInstructions = true;
                break;
                
            case 2: // Exit
                System.out.println("[HealthSnakeMenuScene] Exiting game...");
                Gdx.app.exit();
                break;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        
        // Draw background
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        
        if (showingInstructions) {
            // Show instructions screen
            if (instructionsTexture != null) {
                batch.draw(instructionsTexture, 
                    (Gdx.graphics.getWidth() - instructionsTexture.getWidth()) / 2,
                    (Gdx.graphics.getHeight() - instructionsTexture.getHeight()) / 2);
            }
            
            // Draw "Press any key to return" text
            font.draw(batch, "Press any key to return", 
                    Gdx.graphics.getWidth() / 2 - 150,
                    80);
        } else {
            // Draw title
            if (titleTexture != null) {
                batch.draw(titleTexture,
                    (Gdx.graphics.getWidth() - titleTexture.getWidth()) / 2,
                    Gdx.graphics.getHeight() - titleTexture.getHeight() - 20);
            }
            
            // Draw menu items
            float menuY = Gdx.graphics.getHeight() / 2 + 50;
            float menuSpacing = 60;
            
            for (int i = 0; i < menuItems.length; i++) {
                // Highlight selected item
                if (i == selectedItem) {
                    // Pulsing effect for selected item
                    float pulse = (float) Math.sin(timeElapsed * 5) * 0.2f + 0.8f;
                    font.setColor(1f, pulse, pulse, 1f);
                    font.draw(batch, "> " + menuItems[i] + " <", 
                            Gdx.graphics.getWidth() / 2 - 150,
                            menuY - i * menuSpacing);
                    font.setColor(Color.WHITE); // Reset color
                } else {
                    font.draw(batch, menuItems[i], 
                            Gdx.graphics.getWidth() / 2 - 100,
                            menuY - i * menuSpacing);
                }
            }
            
            // Draw controls hint
            font.getData().setScale(1.0f);
            font.draw(batch, "Arrow Keys: Navigate | Enter: Select", 
                    Gdx.graphics.getWidth() / 2 - 180,
                    50);
            font.getData().setScale(2.0f);
        }
        
        batch.end();
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (titleTexture != null) {
            titleTexture.dispose();
        }
        if (instructionsTexture != null) {
            instructionsTexture.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}