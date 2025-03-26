package io.github.some_example_name.lwjgl3.application_classes.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameState;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;
import io.github.some_example_name.lwjgl3.application_classes.entity.SnakePlayer;
import io.github.some_example_name.lwjgl3.application_classes.entity.EnemySnake;
import io.github.some_example_name.lwjgl3.application_classes.entity.FoodEntity;
import io.github.some_example_name.lwjgl3.application_classes.game.LevelManager;

public class HealthSnakeGameScene extends Scene {
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private World world;
    private SceneManager sceneManager;
    private IOManager ioManager;
    private LevelManager levelManager;

    private SnakePlayer player;
    private Array<EnemySnake> enemies;
    private Array<FoodEntity> foods;

    // Infinite world parameters
    private float worldWidth = 2000; // Virtual world width
    private float worldHeight = 2000; // Virtual world height
    private Vector2 cameraOffset = new Vector2(0, 0); // Camera offset from player

    private float foodSpawnTimer = 0;
    private float foodSpawnInterval = 2f; // Seconds between food spawns
    private int maxFood = 30; // Maximum food items in the world

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    
    // Level transition
    private boolean waitingForInput = false;
    private boolean showingLevelTransition = false;
    private float transitionTimer = 0;
    private float transitionDuration = 2.0f;
    private String transitionMessage = "";
    private Texture levelTransitionTexture;

    public HealthSnakeGameScene(SpriteBatch batch, EntityManager entityManager,
                            MovementManager movementManager, World world,
                            SceneManager sceneManager, IOManager ioManager) {
        this(batch, entityManager, movementManager, world, sceneManager, ioManager, new LevelManager());
    }
    
    public HealthSnakeGameScene(SpriteBatch batch, EntityManager entityManager,
                            MovementManager movementManager, World world,
                            SceneManager sceneManager, IOManager ioManager,
                            LevelManager levelManager) {
        this.batch = batch;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.world = world;
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        this.levelManager = levelManager;

        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont(Gdx.files.internal("game_font.fnt"));
        font.setColor(Color.WHITE);
        font.getData().setScale(0.3f);

        enemies = new Array<EnemySnake>();
        foods = new Array<FoodEntity>();
    }

    @Override
    public void initialize() {
        System.out.println("[HealthSnakeGameScene] Initializing level " + levelManager.getCurrentLevel() + 
                          (levelManager.isUnhealthyPath() ? " (Unhealthy Path)" : " (Healthy Path)"));

        entityManager.clearEntities();

        // Stop any previous music and start game music
        ioManager.getAudio().stopMusic();
        ioManager.getAudio().playMusic("game_music.mp3");

        // Load textures
        try {
            backgroundTexture = new Texture(Gdx.files.internal("snake_background.png"));
            levelTransitionTexture = new Texture(Gdx.files.internal("level_transition.png"));
            System.out.println("[HealthSnakeGameScene] Textures loaded.");
        } catch (Exception e) {
            System.err.println("[HealthSnakeGameScene] Error loading textures: " + e.getMessage());
            // Fallback or placeholder textures could be used here
        }

        // Create player in the center of the world
        player = new SnakePlayer("Player",
                            worldWidth / 2f,
                            worldHeight / 2f,
                            "snake_head.png",
                            "snake_body.png",
                            levelManager);
        entityManager.addEntity(player);
        
        // Add player to movement manager
        movementManager.addEntity(player);

        // Create enemy snakes distributed throughout the world
        int enemyCount = 5 + levelManager.getCurrentLevel() * 2; // More enemies in higher levels
        for (int i = 0; i < enemyCount; i++) {
            createRandomEnemyInWorld(i);
        }

        // Initial food items scattered around the world
        for (int i = 0; i < 20; i++) {
            spawnFoodInWorld();
        }

        // Reset camera offset to center on player initially
        updateCameraPosition();
        
        // Show level transition if not level 1
        if (levelManager.getCurrentLevel() > 1) {
            showLevelTransition();
        }
    }
    
    private void showLevelTransition() {
        showingLevelTransition = true;
        transitionTimer = 0; // Reset the timer for animations
        transitionMessage = levelManager.getLevelDescription();
        ioManager.getAudio().playSound("level_up.mp3");
    }

    private void createRandomEnemyInWorld(int idNumber) {
        Vector2 playerPos = player.getPosition();

        // Calculate maximum distances in each direction
        float maxDistanceLeft = Math.min(1200, playerPos.x - 50);
        float maxDistanceRight = Math.min(1200, worldWidth - 50 - playerPos.x);
        float maxDistanceDown = Math.min(1200, playerPos.y - 50);
        float maxDistanceUp = Math.min(1200, worldHeight - 50 - playerPos.y);

        float x, y;
        boolean tooClose;
        int attempts = 0;

        do {
            if (maxDistanceLeft < 100 || maxDistanceRight < 100 || maxDistanceDown < 100 || maxDistanceUp < 100) {
                // Player is near edge, use directional spawn
                if (playerPos.x < worldWidth / 2) {
                    x = playerPos.x + MathUtils.random(300, maxDistanceRight);
                } else {
                    x = playerPos.x - MathUtils.random(300, maxDistanceLeft);
                }

                if (playerPos.y < worldHeight / 2) {
                    y = playerPos.y + MathUtils.random(300, maxDistanceUp);
                } else {
                    y = playerPos.y - MathUtils.random(300, maxDistanceDown);
                }
            } else {
                // Use angular distribution
                float angle = MathUtils.random(MathUtils.PI2);
                float distance = MathUtils.random(400, 1200);

                x = playerPos.x + MathUtils.cos(angle) * distance;
                y = playerPos.y + MathUtils.sin(angle) * distance;

                // Adjust position if it would be outside bounds
                if (x < 50) x = 50 + MathUtils.random(50);
                if (x > worldWidth - 50) x = worldWidth - 50 - MathUtils.random(50);
                if (y < 50) y = 50 + MathUtils.random(50);
                if (y > worldHeight - 50) y = worldHeight - 50 - MathUtils.random(50);
            }

            tooClose = Vector2.dst(x, y, playerPos.x, playerPos.y) < 300;
            attempts++;
        } while (tooClose && attempts < 10);
        
        // Adjust enemy speed based on level
        float enemySpeed = MathUtils.random(80f, 150f);
        if (levelManager.isUnhealthyPath()) {
            // Enemies are slower on unhealthy path
            enemySpeed *= 0.8f;
        } else {
            // Enemies are faster on healthy path
            enemySpeed *= (1.0f + (levelManager.getCurrentLevel() * 0.1f));
        }

        // Position the enemy on the map
        EnemySnake enemy = new EnemySnake(
            "EnemySnake_" + idNumber,
            x, y,
            "enemy_head.png",
            "enemy_body.png",
            MathUtils.random(5, 15)
        );

        enemies.add(enemy);
        entityManager.addEntity(enemy);
    }

    private void spawnFoodInWorld() {
        if (foods.size >= maxFood) return;

        // Position food randomly across the map
        float x = MathUtils.random(50, worldWidth - 50);
        float y = MathUtils.random(50, worldHeight - 50);

        // Adjust healthy/unhealthy ratio based on level path
        float healthyChance = levelManager.isUnhealthyPath() ? 0.5f : 0.7f;
        boolean isHealthy = MathUtils.randomBoolean(healthyChance);

        String texturePath = FoodEntity.getRandomTexturePath(isHealthy);
        String name = isHealthy ? "HealthyFood_" + MathUtils.random(1000) : "UnhealthyFood_" + MathUtils.random(1000);

        FoodEntity food = new FoodEntity(name, x, y, isHealthy, texturePath);
        foods.add(food);
        entityManager.addEntity(food);
    }

    private void updateCameraPosition() {
        // Update camera to follow player
        Vector2 playerPos = player.getPosition();

        // Calculate screen center
        float screenCenterX = Gdx.graphics.getWidth() / 2f;
        float screenCenterY = Gdx.graphics.getHeight() / 2f;

        // Set camera offset so player is at screen center
        cameraOffset.x = playerPos.x - screenCenterX;
        cameraOffset.y = playerPos.y - screenCenterY;
    }

    // Convert a world position to screen position
    private Vector2 worldToScreen(float worldX, float worldY) {
        return new Vector2(
            worldX - cameraOffset.x,
            worldY - cameraOffset.y
        );
    }

    @Override
    public void update(float deltaTime) {
        // Handle level transition display
        if (showingLevelTransition) {
            // Increment the transition timer for animation effects
            transitionTimer += deltaTime;
            
            // Check if player pressed Enter to continue
            if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.ENTER)) {
                showingLevelTransition = false;
                transitionTimer = 0; // Reset timer when exiting transition
            }
            // Don't process other game updates during transition
            return;
        }

        // Handle pause
        if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("[HealthSnakeGameScene] Pausing game...");
            sceneManager.pushScene(new SnakePauseScene(batch, sceneManager, entityManager, movementManager, ioManager),
                                GameState.PAUSED);
            return;
        }

        movementManager.updateMovement(deltaTime);

        // Level progression checks
        if (player.checkWinCondition() && !player.checkLoseCondition()) {
            // Player met healthy goal but not unhealthy - proceed to next healthy level
            System.out.println("[HealthSnakeGameScene] Healthy goal met, progressing to next level...");
            progressToNextLevel(false);
            return;
        } else if (player.checkLoseCondition() && !player.checkWinCondition()) {
            // Player met unhealthy goal but not healthy - proceed to next unhealthy level
            System.out.println("[HealthSnakeGameScene] Unhealthy goal met, progressing to next level...");
            progressToNextLevel(true);
            return;
        } else if (player.checkWinCondition() && player.checkLoseCondition()) {
            // Both goals met simultaneously - proceed based on which is higher percentage
            boolean goUnhealthy = player.getUnhealthyFoodPercentage() > player.getHealthyFoodPercentage();
            System.out.println("[HealthSnakeGameScene] Both goals met, progressing to next " + 
                              (goUnhealthy ? "unhealthy" : "healthy") + " level...");
            progressToNextLevel(goUnhealthy);
            return;
        }

        // Food spawn timer
        foodSpawnTimer += deltaTime;
        if (foodSpawnTimer >= foodSpawnInterval && foods.size < maxFood) {
            spawnFoodInWorld();
            foodSpawnTimer = 0;
        }

        // Check for food that is out of the visible area and reposition it
        repositionOffscreenFood();

        // Check collisions with food
        Rectangle playerBounds = player.getHeadBounds();
        Array<FoodEntity> foodsToRemove = new Array<FoodEntity>();

        for (FoodEntity food : foods) {
            if (!food.isActive()) {
                foodsToRemove.add(food);
                continue;
            }

            if (playerBounds.overlaps(food.getBounds())) {
                player.eatFood(food);
                food.deactivate();
                foodsToRemove.add(food);
            }
        }

        // Remove eaten food
        for (FoodEntity food : foodsToRemove) {
            foods.removeValue(food, true);
            entityManager.removeEntity(food.getEntityID());
        }

        // Check collisions with enemy snakes
        for (EnemySnake enemy : enemies) {
            // Check head-to-head collision
            if (playerBounds.overlaps(enemy.getHeadBounds())) {
                ioManager.getAudio().playSound("collision.mp3");

                // Calculate final score and transition to death scene
                handleGameOver("Collision with enemy snake head");
                return;
            }

            // Check player head against enemy body
            Array<Rectangle> enemyBodyBounds = enemy.getBodyBounds();
            for (Rectangle bodyPart : enemyBodyBounds) {
                if (playerBounds.overlaps(bodyPart)) {
                    ioManager.getAudio().playSound("collision.mp3");

                    // Calculate final score and transition to death scene
                    handleGameOver("Collision with enemy snake body");
                    return;
                }
            }
        }

        // Handle world wrapping for enemies that move off-screen
        for (EnemySnake enemy : enemies) {
            Vector2 pos = enemy.getPosition();

            // If enemy is far from player (beyond the visible area + buffer), reposition it
            if (Vector2.dst(pos.x, pos.y, player.getPosition().x, player.getPosition().y) > 1500) {
                repositionEnemyNearPlayer(enemy);
            }
        }

        // Update all entities
        player.update(deltaTime);
        for (EnemySnake enemy : enemies) {
            enemy.update(deltaTime);
        }

        // Update camera position to follow player
        updateCameraPosition();
    }
    
    private void progressToNextLevel(boolean unhealthyPath) {
        // Update level manager with progression info
        levelManager.progressToNextLevel(player.getHealthyFoodPercentage(), player.getUnhealthyFoodPercentage());
        
        // Save current player body size for continuity
        int currentBodySize = player.getBodySize();
        
        // Start a new game scene with the updated level manager
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
    }
    
    private void handleGameOver(String deathCause) {
        // Pass separate calorie counts to death scene
        sceneManager.changeScene(
            new HealthSnakeDeathScene(
                batch,
                sceneManager,
                entityManager,
                movementManager,
                ioManager,
                player.getHealthyCalories(),
                player.getUnhealthyCalories(),
                player.getHealthyFoodCount(),
                player.getUnhealthyFoodCount(),
                deathCause,
                levelManager
            ),
            GameState.GAME_OVER
        );
    }

    private void repositionOffscreenFood() {
        Vector2 playerPos = player.getPosition();
        float visibleRange = 1000; // How far from player food is considered "in range"
        Array<FoodEntity> foodsToReposition = new Array<FoodEntity>();

        // First, collect all food items that are too far away
        for (FoodEntity food : foods) {
            Vector2 foodPos = food.getPosition();

            // If food is too far from player, mark it for repositioning
            if (Vector2.dst(foodPos.x, foodPos.y, playerPos.x, playerPos.y) > visibleRange) {
                foodsToReposition.add(food);
            }
        }

        // Then reposition them one by one
        for (FoodEntity food : foodsToReposition) {
            // Remove this food from our tracking arrays
            foods.removeValue(food, true);
            entityManager.removeEntity(food.getEntityID());

            // Spawn a new one near the player
            spawnFoodNearPlayer();
        }
    }

private void spawnFoodNearPlayer() {
    Vector2 playerPos = player.getPosition();

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

    // Adjust healthy/unhealthy ratio based on level path
    float healthyChance = levelManager.isUnhealthyPath() ? 0.5f : 0.7f;
    boolean isHealthy = MathUtils.randomBoolean(healthyChance);

    String texturePath = FoodEntity.getRandomTexturePath(isHealthy);
    String name = isHealthy ? "HealthyFood_" + MathUtils.random(1000) : "UnhealthyFood_" + MathUtils.random(1000);

    // Create and add the new food entity
    FoodEntity food = new FoodEntity(name, x, y, isHealthy, texturePath);
    foods.add(food);
    entityManager.addEntity(food);
}

    private void repositionEnemyNearPlayer(EnemySnake enemy) {
        Vector2 playerPos = player.getPosition();
        float spawnDistance = MathUtils.random(800, 1200);
        float angle = MathUtils.random(MathUtils.PI2);

        float x = playerPos.x + MathUtils.cos(angle) * spawnDistance;
        float y = playerPos.y + MathUtils.sin(angle) * spawnDistance;

        // Keep coordinates within world bounds
        x = MathUtils.clamp(x, 50, worldWidth - 50);
        y = MathUtils.clamp(y, 50, worldHeight - 50);

        enemy.setPosition(x, y);
    }

    @Override
    public void render(SpriteBatch batch) {
        // Draw tiled background
        batch.begin();
        if (backgroundTexture != null) {
            // Calculate how many tiles we need to cover the screen
            int bgWidth = backgroundTexture.getWidth();
            int bgHeight = backgroundTexture.getHeight();

            // Calculate starting position based on camera offset
            float startX = -(cameraOffset.x % bgWidth);
            if (startX > 0) startX -= bgWidth;

            float startY = -(cameraOffset.y % bgHeight);
            if (startY > 0) startY -= bgHeight;

            // Draw background tiles
            for (float x = startX; x < Gdx.graphics.getWidth(); x += bgWidth) {
                for (float y = startY; y < Gdx.graphics.getHeight(); y += bgHeight) {
                    batch.draw(backgroundTexture, x, y);
                }
            }
        }

        // Draw food entities
        for (FoodEntity food : foods) {
            Vector2 screenPos = worldToScreen(food.getPosition().x, food.getPosition().y);

            // Only draw if on screen
            if (screenPos.x >= -50 && screenPos.x <= Gdx.graphics.getWidth() + 50 &&
                screenPos.y >= -50 && screenPos.y <= Gdx.graphics.getHeight() + 50) {

                // We need to draw the food at its screen position
                float origX = food.getPosition().x;
                float origY = food.getPosition().y;

                // Temporarily set position to screen coordinates for rendering
                food.setPosition(screenPos.x, screenPos.y);
                food.render(batch);

                // Restore original world position
                food.setPosition(origX, origY);
            }
        }

        // Draw enemy snakes
        for (EnemySnake enemy : enemies) {
            Vector2 screenPos = worldToScreen(enemy.getPosition().x, enemy.getPosition().y);

            // Only draw if on screen (use a larger margin for larger entities)
            if (screenPos.x >= -100 && screenPos.x <= Gdx.graphics.getWidth() + 100 &&
                screenPos.y >= -100 && screenPos.y <= Gdx.graphics.getHeight() + 100) {

                // Draw enemy at screen position
                enemy.renderAtPosition(batch, screenPos.x, screenPos.y);
            }
        }

        // Draw player (always centered)
        player.render(batch);
        
        // Draw UI elements - progress bars, level info, etc.
        drawUI(batch);
        
        // Draw level transition overlay if active
        if (showingLevelTransition) {
            drawLevelTransition(batch);
        }

        batch.end();
    }
    
    private void drawUI(SpriteBatch batch) {
        // End batch to draw shape renderer elements
        batch.end();
        
        // Draw UI elements with ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    
        // Draw healthy food progress bar (green)
        shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1);
        shapeRenderer.rect(20, Gdx.graphics.getHeight() - 30, 200 * player.getHealthyFoodPercentage(), 20);
    
        // Draw unhealthy food progress bar (red)
        shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1);
        shapeRenderer.rect(Gdx.graphics.getWidth() - 220, Gdx.graphics.getHeight() - 30, 200 * player.getUnhealthyFoodPercentage(), 20);
    
        shapeRenderer.end();
    
        // Draw bar outlines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(20, Gdx.graphics.getHeight() - 30, 200, 20);
        shapeRenderer.rect(Gdx.graphics.getWidth() - 220, Gdx.graphics.getHeight() - 30, 200, 20);
        shapeRenderer.end();
    
        // Restart batch for text drawing
        batch.begin();
        
        // Draw bar labels
        font.draw(batch, "Healthy", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Unhealthy", Gdx.graphics.getWidth() - 220, Gdx.graphics.getHeight() - 40);
        
        // Draw level information
        String levelText = "Level " + levelManager.getCurrentLevel();
        font.draw(batch, levelText, Gdx.graphics.getWidth() / 2 - 60, Gdx.graphics.getHeight() - 20);
        
        // Draw food counts
        String healthyCount = player.getHealthyFoodCount() + "/" + levelManager.getHealthyFoodGoal();
        String unhealthyCount = player.getUnhealthyFoodCount() + "/" + levelManager.getUnhealthyFoodGoal();
        
        font.draw(batch, healthyCount, 230, Gdx.graphics.getHeight() - 20);
        font.draw(batch, unhealthyCount, Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 20);
        
        // Draw calorie counters
        font.setColor(0.2f, 0.9f, 0.2f, 1f);
        font.draw(batch, "Healthy: " + player.getHealthyCalories() + " kcal", 20, 30);
        
        font.setColor(0.9f, 0.2f, 0.2f, 1f);
        font.draw(batch, "Unhealthy: " + player.getUnhealthyCalories() + " kcal", Gdx.graphics.getWidth() - 220, 30);
        
        // Reset font color
        font.setColor(Color.WHITE);
        font.draw(batch, "Size: " + player.getBodySize(), Gdx.graphics.getWidth() / 2 - 40, 30);
    }

    private void drawCenteredText(SpriteBatch batch, String text, float y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        
        // Use GlyphLayout to calculate exact text width
        GlyphLayout layout = new GlyphLayout(font, text);
        float textWidth = layout.width;
        
        // Draw centered text
        font.draw(batch, text, (Gdx.graphics.getWidth() - textWidth) / 2f, y);
    }
    
    private void drawLevelTransition(SpriteBatch batch) {
        // Draw semi-transparent background
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(levelTransitionTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);
        
        // Create pulsing effect using transitionTimer
        float pulse = (float)Math.sin(transitionTimer * 3) * 0.2f + 0.8f;
        float scale = 0.35f + 0.05f * pulse;
        
        // Draw level message - centered
        drawCenteredText(batch, transitionMessage, Gdx.graphics.getHeight() / 2 + 70, scale, Color.WHITE);
        
        // Display specific information based on the level path - centered
        String infoText;
        if (levelManager.isUnhealthyPath()) {
            infoText = "Unhealthy Path: Snake is slower and larger!";
            drawCenteredText(batch, infoText, Gdx.graphics.getHeight() / 2 + 20, 0.3f, new Color(1, 0.5f, 0.5f, 1));
        } else {
            infoText = "Healthy Path: Snake is faster and more agile!";
            drawCenteredText(batch, infoText, Gdx.graphics.getHeight() / 2 + 20, 0.3f, new Color(0.5f, 1, 0.5f, 1));
        }
        
        // Show goals - centered
        String healthyGoal = "Healthy Food Goal: " + levelManager.getHealthyFoodGoal();
        String unhealthyGoal = "Unhealthy Food Goal: " + levelManager.getUnhealthyFoodGoal();
        
        drawCenteredText(batch, healthyGoal, Gdx.graphics.getHeight() / 2 - 20, 0.25f, new Color(0.5f, 1, 0.5f, 1));
        drawCenteredText(batch, unhealthyGoal, Gdx.graphics.getHeight() / 2 - 60, 0.25f, new Color(1, 0.5f, 0.5f, 1));
        
        // Add "Press Enter to continue" text with pulsing effect - centered
        drawCenteredText(batch, "Press Enter to continue", 
                         Gdx.graphics.getHeight() / 2 - 120, 0.3f, 
                         new Color(1, 1, 1, pulse)); // Pulsing opacity
        
        // Reset font settings
        font.setColor(Color.WHITE);
        font.getData().setScale(0.3f);
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (levelTransitionTexture != null) {
            levelTransitionTexture.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}