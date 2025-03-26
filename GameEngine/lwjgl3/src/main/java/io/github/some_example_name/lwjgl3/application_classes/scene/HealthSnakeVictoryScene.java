package io.github.some_example_name.lwjgl3.application_classes.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameState;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;
import io.github.some_example_name.lwjgl3.abstract_engine.ui.AssetPaths;
import io.github.some_example_name.lwjgl3.application_classes.game.NutritionManager;
import io.github.some_example_name.lwjgl3.application_classes.game.LevelManager;

public class HealthSnakeVictoryScene extends Scene {
    private Texture backgroundTexture;
    private Texture victoryTexture;
    private Texture trophyTexture;
    private Texture healthyFoodTexture;

    private SpriteBatch batch;
    private SceneManager sceneManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private IOManager ioManager;
    private BitmapFont font;
    private LevelManager levelManager;

    private String[] menuItems = {
        "Next Level",
        "Replay Current Level",
        "Main Menu",
        "Exit Game"
    };

    private int selectedItem = 0;
    private float timeElapsed = 0;

    // For firework particle effects
    private float[] particleX;
    private float[] particleY;
    private float[] particleSpeedX;
    private float[] particleSpeedY;
    private float[] particleSize;
    private float[] particleAlpha;
    private float[] particleLifetime;
    private Color[] particleColor;
    private final int particleCount = 100;

    // For floating healthy food icons
    private float[] foodX;
    private float[] foodY;
    private float[] foodSpeedX;
    private float[] foodSpeedY;
    private float[] foodSize;
    private float[] foodRotation;
    private float[] foodRotationSpeed;
    private final int foodCount = 20;

    private String[] victoryMessages = {
        "Level Complete!",
        "You're a Health Hero!",
        "Great Job!",
        "Amazing Health Skills!",
        "Nutritional Champion!"
    };
    private String victoryMessage;
    private String educationalFact;

    private final int healthyCalories;
    private final int unhealthyCalories;
    private final int healthyCount;
    private final int unhealthyCount;
    private final int snakeLength;
    private final int caloriesBurned;
    private final int level;

    public HealthSnakeVictoryScene(SpriteBatch batch, SceneManager sceneManager,
                                 EntityManager entityManager, MovementManager movementManager, IOManager ioManager,
                                 int healthyCalories, int unhealthyCalories, int healthyCount, int unhealthyCount,
                                 int snakeLength, LevelManager levelManager) {
        this.batch = batch;
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.ioManager = ioManager;
        this.healthyCalories = healthyCalories;
        this.unhealthyCalories = unhealthyCalories;
        this.healthyCount = healthyCount;
        this.unhealthyCount = unhealthyCount;
        this.snakeLength = snakeLength;
        this.levelManager = levelManager;
        this.level = levelManager.getCurrentLevel();
        
        // Calculate calories burned based on snake length and activity level
        this.caloriesBurned = calculateCaloriesBurned(snakeLength, levelManager);

        font = new BitmapFont(Gdx.files.internal("game_font.fnt"));
        font.setColor(Color.WHITE);
        font.getData().setScale(0.3f);

        // Initialize firework particles
        particleX = new float[particleCount];
        particleY = new float[particleCount];
        particleSpeedX = new float[particleCount];
        particleSpeedY = new float[particleCount];
        particleSize = new float[particleCount];
        particleAlpha = new float[particleCount];
        particleLifetime = new float[particleCount];
        particleColor = new Color[particleCount];

        // Initialize floating food
        foodX = new float[foodCount];
        foodY = new float[foodCount];
        foodSpeedX = new float[foodCount];
        foodSpeedY = new float[foodCount];
        foodSize = new float[foodCount];
        foodRotation = new float[foodCount];
        foodRotationSpeed = new float[foodCount];

        initializeParticles();
        initializeFood();

        // Select a random victory message
        victoryMessage = victoryMessages[MathUtils.random(victoryMessages.length - 1)];
        
        // Get a random educational fact about healthy eating
        educationalFact = NutritionManager.getInstance().getRandomHealthyEatingFact();
    }
    
    private int calculateCaloriesBurned(int snakeLength, LevelManager levelManager) {
        // Simple formula: base calories * snake length * level * activity factor
        int baseCalories = 50;
        return Math.round(baseCalories * snakeLength * levelManager.getCurrentLevel());
    }

    private void initializeParticles() {
        for (int i = 0; i < particleCount; i++) {
            resetParticle(i);
        }
    }

    private void resetParticle(int i) {
        float centerX = MathUtils.random(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getWidth() * 0.8f);
        float centerY = MathUtils.random(Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getHeight() * 0.8f);

        float angle = MathUtils.random(MathUtils.PI2);
        float speed = MathUtils.random(50f, 150f);

        particleX[i] = centerX;
        particleY[i] = centerY;
        particleSpeedX[i] = MathUtils.cos(angle) * speed;
        particleSpeedY[i] = MathUtils.sin(angle) * speed;
        particleSize[i] = MathUtils.random(2f, 6f);
        particleAlpha[i] = 1.0f;
        particleLifetime[i] = MathUtils.random(0.5f, 2.0f);

        // Random vibrant colors
        particleColor[i] = new Color(
            MathUtils.random(0.5f, 1.0f),
            MathUtils.random(0.5f, 1.0f),
            MathUtils.random(0.5f, 1.0f),
            1.0f
        );
    }

    private void initializeFood() {
        for (int i = 0; i < foodCount; i++) {
            foodX[i] = MathUtils.random(0, Gdx.graphics.getWidth());
            foodY[i] = MathUtils.random(0, Gdx.graphics.getHeight());
            foodSpeedX[i] = MathUtils.random(-30f, 30f);
            foodSpeedY[i] = MathUtils.random(-30f, 30f);
            foodSize[i] = MathUtils.random(20f, 40f);
            foodRotation[i] = MathUtils.random(0, 360);
            foodRotationSpeed[i] = MathUtils.random(-60f, 60f);
        }
    }

    @Override
    public void initialize() {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("snake_background.png"));
            victoryTexture = new Texture(Gdx.files.internal("victory.png"));
            trophyTexture = new Texture(Gdx.files.internal("victory.png"));
            healthyFoodTexture = new Texture(Gdx.files.internal("healthy_food.png"));

            System.out.println("[HealthSnakeVictoryScene] Textures loaded successfully.");
        } catch (Exception e) {
            System.err.println("[HealthSnakeVictoryScene] Error loading textures: " + e.getMessage());
            // Use placeholder handling if textures fail to load
        }

        // Play victory sound
        ioManager.getAudio().stopMusic();
        ioManager.getAudio().playSound("victory.mp3");

        // Start victory music after a delay
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500); // Wait 1.5 seconds
                    ioManager.getAudio().playMusic("victory_music.mp3");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void update(float deltaTime) {
        timeElapsed += deltaTime;

        // Trigger new fireworks occasionally
        if (MathUtils.random() < 0.05f) {
            float centerX = MathUtils.random(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getWidth() * 0.8f);
            float centerY = MathUtils.random(Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getHeight() * 0.8f);

            // Create a burst of particles at this location
            Color burstColor = new Color(
                MathUtils.random(0.5f, 1.0f),
                MathUtils.random(0.5f, 1.0f),
                MathUtils.random(0.5f, 1.0f),
                1.0f
            );

            for (int i = 0; i < particleCount; i++) {
                if (particleAlpha[i] <= 0.1f) { // Reuse "dead" particles
                    float angle = MathUtils.random(MathUtils.PI2);
                    float speed = MathUtils.random(50f, 250f);

                    particleX[i] = centerX;
                    particleY[i] = centerY;
                    particleSpeedX[i] = MathUtils.cos(angle) * speed;
                    particleSpeedY[i] = MathUtils.sin(angle) * speed;
                    particleSize[i] = MathUtils.random(2f, 6f);
                    particleAlpha[i] = 1.0f;
                    particleLifetime[i] = MathUtils.random(0.5f, 2.0f);
                    particleColor[i] = burstColor;
                }
            }
        }

        // Update particles
        for (int i = 0; i < particleCount; i++) {
            if (particleAlpha[i] > 0.1f) {
                particleX[i] += particleSpeedX[i] * deltaTime;
                particleY[i] += particleSpeedY[i] * deltaTime;
                particleSpeedY[i] -= 50 * deltaTime; // Gravity effect

                // Fade out based on lifetime
                particleAlpha[i] -= deltaTime / particleLifetime[i];
                if (particleAlpha[i] < 0) particleAlpha[i] = 0;
            }
        }

        // Update floating food
        for (int i = 0; i < foodCount; i++) {
            foodX[i] += foodSpeedX[i] * deltaTime;
            foodY[i] += foodSpeedY[i] * deltaTime;
            foodRotation[i] += foodRotationSpeed[i] * deltaTime;

            // Wrap around screen
            if (foodX[i] < -foodSize[i]) foodX[i] = Gdx.graphics.getWidth() + foodSize[i];
            if (foodX[i] > Gdx.graphics.getWidth() + foodSize[i]) foodX[i] = -foodSize[i];
            if (foodY[i] < -foodSize[i]) foodY[i] = Gdx.graphics.getHeight() + foodSize[i];
            if (foodY[i] > Gdx.graphics.getHeight() + foodSize[i]) foodY[i] = -foodSize[i];
        }

        // Menu navigation
        if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.UP)) {
            selectedItem = (selectedItem - 1 + menuItems.length) % menuItems.length;
            ioManager.getAudio().playSound(AssetPaths.MENU_MOVE);
        } else if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.DOWN)) {
            selectedItem = (selectedItem + 1) % menuItems.length;
            ioManager.getAudio().playSound(AssetPaths.MENU_MOVE);
        }

        // Menu selection
        if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.ENTER)) {
            handleMenuSelection();
        }
    }

    private void handleMenuSelection() {
        ioManager.getAudio().playSound(AssetPaths.MENU_SELECT);

        switch (selectedItem) {
            case 0: // Next Level
                System.out.println("[HealthSnakeVictoryScene] Proceeding to next level...");
                ioManager.getAudio().stopMusic();
                
                // Progress to the next level
                levelManager.progressToNextLevel(1.0f, 0.0f);
                
                // Start new game with updated level
                sceneManager.changeScene(
                    new HealthSnakeGameScene(
                        batch,
                        entityManager,
                        movementManager,
                        sceneManager.getWorld(),
                        sceneManager,
                        ioManager,
                        levelManager
                    ),
                    GameState.RUNNING
                );
                break;

            case 1: // Replay Current Level
                System.out.println("[HealthSnakeVictoryScene] Replaying current level...");
                ioManager.getAudio().stopMusic();
                
                // Reset to current level
                int currentLevel = levelManager.getCurrentLevel();
                levelManager.resetToLevel(currentLevel, false);
                
                // Start new game with same level
                sceneManager.changeScene(
                    new HealthSnakeGameScene(
                        batch,
                        entityManager,
                        movementManager,
                        sceneManager.getWorld(),
                        sceneManager,
                        ioManager,
                        levelManager
                    ),
                    GameState.RUNNING
                );
                break;

            case 2: // Main Menu
                System.out.println("[HealthSnakeVictoryScene] Returning to main menu...");
                ioManager.getAudio().stopMusic();

                sceneManager.changeScene(
                    new HealthSnakeMenuScene(
                        batch,
                        sceneManager,
                        entityManager,
                        movementManager,
                        ioManager
                    ),
                    GameState.MAIN_MENU
                );
                break;

            case 3: // Exit Game
                System.out.println("[HealthSnakeVictoryScene] Exiting game...");
                Gdx.app.exit();
                break;
        }
    }

    private void drawCenteredText(SpriteBatch batch, String text, float y, float width, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        
        // Calculate the width of the text for centering
        float textWidth = font.draw(batch, text, 0, 0).width;
        
        // Draw centered text
        font.draw(batch, text, (Gdx.graphics.getWidth() - textWidth) / 2f, y);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();

        // Draw background with a golden tint for victory
        if (backgroundTexture != null) {
            batch.setColor(1f, 0.9f, 0.6f, 1f);
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(Color.WHITE);
        }

        // Draw floating food
        if (healthyFoodTexture != null) {
            for (int i = 0; i < foodCount; i++) {
                batch.draw(
                    healthyFoodTexture,
                    foodX[i] - foodSize[i]/2,
                    foodY[i] - foodSize[i]/2,
                    foodSize[i]/2, // origin x
                    foodSize[i]/2, // origin y
                    foodSize[i],
                    foodSize[i],
                    1, 1, // scale
                    foodRotation[i],
                    0, 0, // src xy
                    healthyFoodTexture.getWidth(),
                    healthyFoodTexture.getHeight(),
                    false, false // flip xy
                );
            }
        }

        // Draw firework particles
        for (int i = 0; i < particleCount; i++) {
            if (particleAlpha[i] > 0.1f) {
                Color color = particleColor[i].cpy();
                color.a = particleAlpha[i];
                batch.setColor(color);

                batch.draw(
                    healthyFoodTexture,  // Using food texture as particle
                    particleX[i] - particleSize[i]/2,
                    particleY[i] - particleSize[i]/2,
                    particleSize[i],
                    particleSize[i]
                );
            }
        }
        batch.setColor(Color.WHITE);

        // Draw victory text with animation
        if (victoryTexture != null) {
            // Pulsating scale effect
            float scale = 1.0f + 0.1f * (float)Math.sin(timeElapsed * 3);
            float width = victoryTexture.getWidth() * scale;
            float height = victoryTexture.getHeight() * scale;

            batch.draw(
                victoryTexture,
                (Gdx.graphics.getWidth() - width) / 2,
                Gdx.graphics.getHeight() - height - 50,
                width,
                height
            );
        }

        // Draw trophy
        if (trophyTexture != null) {
            float trophySize = 100;
            float trophyX = Gdx.graphics.getWidth() / 2 - trophySize / 2;
            float trophyY = Gdx.graphics.getHeight() / 2 + 20;

            // Make the trophy float up and down
            trophyY += Math.sin(timeElapsed * 2) * 10;

            batch.draw(
                trophyTexture,
                trophyX,
                trophyY,
                trophySize,
                trophySize
            );
        }

        // Draw level completion message
        font.getData().setScale(0.4f);
        font.setColor(1f, 0.8f, 0.2f, 1f);
        String completionText = "Level " + level + " Complete!";
        float messageWidth = font.draw(batch, completionText, 0, 0).width * 0.4f;
        font.draw(
            batch,
            completionText,
            (Gdx.graphics.getWidth() - messageWidth) / 2,
            Gdx.graphics.getHeight() - 150
        );
        
        // Draw victory message
        font.getData().setScale(0.3f);
        font.setColor(1f, 0.8f, 0.2f, 1f);
        messageWidth = font.draw(batch, victoryMessage, 0, 0).width * 0.3f;
        font.draw(
            batch,
            victoryMessage,
            (Gdx.graphics.getWidth() - messageWidth) / 2,
            Gdx.graphics.getHeight() - 190
        );

        // Draw statistics
        float statsY = Gdx.graphics.getHeight() / 2;
        
        // Healthy food stats (green)
        font.setColor(0.3f, 0.9f, 0.3f, 1f);
        String healthyText = "Healthy Food: " + healthyCount + " items (" + healthyCalories + " kcal)";
        float healthyWidth = font.draw(batch, healthyText, 0, 0).width * 0.3f;
        font.draw(
            batch,
            healthyText,
            (Gdx.graphics.getWidth() - healthyWidth) / 2,
            statsY
        );
        
        // Unhealthy food stats (red)
        font.setColor(0.9f, 0.3f, 0.3f, 1f);
        String unhealthyText = "Unhealthy Food: " + unhealthyCount + " items (" + unhealthyCalories + " kcal)";
        float unhealthyWidth = font.draw(batch, unhealthyText, 0, 0).width * 0.3f;
        font.draw(
            batch,
            unhealthyText,
            (Gdx.graphics.getWidth() - unhealthyWidth) / 2,
            statsY - 40
        );
        
        // Total calories
        font.setColor(1f, 1f, 1f, 1f);
        String totalText = "Total: " + (healthyCalories + unhealthyCalories) + " kcal consumed";
        float totalWidth = font.draw(batch, totalText, 0, 0).width * 0.3f;
        font.draw(
            batch,
            totalText,
            (Gdx.graphics.getWidth() - totalWidth) / 2,
            statsY - 80
        );
        
        // Calories burned
        String burnedText = "Calories Burned: " + caloriesBurned + " kcal";
        float burnedWidth = font.draw(batch, burnedText, 0, 0).width * 0.3f;
        font.draw(
            batch,
            burnedText,
            (Gdx.graphics.getWidth() - burnedWidth) / 2,
            statsY - 120
        );

        String lengthText = "Snake Length: " + snakeLength;
        float lengthWidth = font.draw(batch, lengthText, 0, 0).width * 0.3f;
        font.draw(
            batch,
            lengthText,
            (Gdx.graphics.getWidth() - lengthWidth) / 2,
            statsY - 160
        );
        
        // Draw educational fact
        font.setColor(0.9f, 0.9f, 1.0f, 1.0f);
        font.getData().setScale(0.25f);
        String factHeader = "Health Fact:";
        float factY = statsY - 200;
        float contentWidth = 500;

        // Draw centered header
        drawCenteredText(batch, factHeader, factY, contentWidth, 0.25f, new Color(0.9f, 0.9f, 1.0f, 1.0f));

        // For wrapped text, we'll use a special method to center the block
        float factTextY = factY - 20;
        float factX = (Gdx.graphics.getWidth() - contentWidth) / 2f;
        font.draw(batch, educationalFact, factX, factTextY, contentWidth, -1, true);
        
        // Reset font color
        font.setColor(Color.WHITE);

        // Draw menu items
        font.getData().setScale(0.3f);
        float menuY = statsY - 290;
        float menuSpacing = 50;

        for (int i = 0; i < menuItems.length; i++) {
            // Highlight selected item
            if (i == selectedItem) {
                // Pulsing effect for selected item
                float pulse = (float) Math.sin(timeElapsed * 5) * 0.2f + 0.8f;
                font.setColor(1f, pulse, 0.2f, 1f);
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
        font.getData().setScale(0.3f);
        font.draw(batch, "Arrow Keys: Navigate | Enter: Select",
                Gdx.graphics.getWidth() / 2 - 225,
                50);

        batch.end();
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (victoryTexture != null) {
            victoryTexture.dispose();
        }
        if (trophyTexture != null) {
            trophyTexture.dispose();
        }
        if (healthyFoodTexture != null) {
            healthyFoodTexture.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}