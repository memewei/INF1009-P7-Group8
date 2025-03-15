package io.github.some_example_name.lwjgl3.application_classes.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

public class HealthSnakeGameScene extends Scene {
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private World world;
    private SceneManager sceneManager;
    
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
    
    private boolean gameOver = false;
    private boolean playerWon = false;
    private Texture gameOverTexture;
    private Texture victoryTexture;
    
    public HealthSnakeGameScene(SpriteBatch batch, EntityManager entityManager, 
                            MovementManager movementManager, World world,
                            SceneManager sceneManager) {
        this.batch = batch;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.world = world;
        this.sceneManager = sceneManager;
        
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
        
        enemies = new Array<EnemySnake>();
        foods = new Array<FoodEntity>();
    }
    
    @Override
    public void initialize() {
        System.out.println("[HealthSnakeGameScene] Initializing infinite world...");
        
        entityManager.clearEntities();
        
        // Stop any previous music and start game music
        IOManager.getInstance().getAudio().stopMusic();
        IOManager.getInstance().getAudio().playMusic("game_music.mp3");
        
        // Load textures
        try {
            backgroundTexture = new Texture(Gdx.files.internal("snake_background.png"));
            gameOverTexture = new Texture(Gdx.files.internal("game_over.png"));
            victoryTexture = new Texture(Gdx.files.internal("victory.png"));
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
                            "snake_body.png");
        entityManager.addEntity(player);
        
        // Create enemy snakes distributed throughout the world
        for (int i = 0; i < 10; i++) {
            createRandomEnemyInWorld(i);
        }
        
        // Initial food items scattered around the world
        for (int i = 0; i < 20; i++) {
            spawnFoodInWorld();
        }
        
        gameOver = false;
        playerWon = false;
        
        // Reset camera offset to center on player initially
        updateCameraPosition();
    }
    
    private void createRandomEnemyInWorld(int idNumber) {
        // Create enemy away from player
        float x, y;
        do {
            x = MathUtils.random(50, worldWidth - 50);
            y = MathUtils.random(50, worldHeight - 50);
        } while (Vector2.dst(x, y, player.getPosition().x, player.getPosition().y) < 300);
        
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
        
        // 70% chance for healthy food, 30% chance for unhealthy
        boolean isHealthy = MathUtils.randomBoolean(0.7f);
        
        String texturePath = isHealthy ? "healthy_food.png" : "unhealthy_food.png";
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
        if (gameOver) {
            // Handle input to return to menu after game over
            if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.ENTER)) {
                System.out.println("[HealthSnakeGameScene] Returning to menu...");
                sceneManager.changeScene(new HealthSnakeMenuScene(
                        batch,
                        sceneManager,
                        entityManager,
                        movementManager
                ), GameState.MAIN_MENU);
            }
            return;
        }
        
        // Handle pause
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("[HealthSnakeGameScene] Pausing game...");
            sceneManager.pushScene(new SnakePauseScene(batch, sceneManager, entityManager, movementManager), 
                                GameState.PAUSED);
            return;
        }
        
        // Update player and check win/lose conditions
        if (player.checkWinCondition()) {
            gameOver = true;
            playerWon = true;
            IOManager.getInstance().getAudio().playSound("victory.mp3");
            return;
        }
        
        if (player.checkLoseCondition()) {
            gameOver = true;
            playerWon = false;
            IOManager.getInstance().getAudio().playSound("game_over.mp3");
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
                gameOver = true;
                playerWon = false;
                IOManager.getInstance().getAudio().playSound("collision.mp3");
            }
            
            // Check player head against enemy body
            Array<Rectangle> enemyBodyBounds = enemy.getBodyBounds();
            for (Rectangle bodyPart : enemyBodyBounds) {
                if (playerBounds.overlaps(bodyPart)) {
                    gameOver = true;
                    playerWon = false;
                    IOManager.getInstance().getAudio().playSound("collision.mp3");
                    break;
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
    
    private void repositionOffscreenFood() {
        Vector2 playerPos = player.getPosition();
        float visibleRange = 1000; // How far from player food is considered "in range"
        
        for (FoodEntity food : foods) {
            Vector2 foodPos = food.getPosition();
            
            // If food is too far from player, remove it and spawn a new one
            if (Vector2.dst(foodPos.x, foodPos.y, playerPos.x, playerPos.y) > visibleRange) {
                // Remove this food
                foods.removeValue(food, true);
                entityManager.removeEntity(food.getEntityID());
                
                // Spawn a new one near the player
                spawnFoodNearPlayer();
                
                // No need to continue checking this food
                break;
            }
        }
    }
    
    private void spawnFoodNearPlayer() {
        Vector2 playerPos = player.getPosition();
        float spawnDistance = MathUtils.random(300, 800);
        float angle = MathUtils.random(MathUtils.PI2);
        
        float x = playerPos.x + MathUtils.cos(angle) * spawnDistance;
        float y = playerPos.y + MathUtils.sin(angle) * spawnDistance;
        
        // Keep coordinates within world bounds
        x = MathUtils.clamp(x, 50, worldWidth - 50);
        y = MathUtils.clamp(y, 50, worldHeight - 50);
        
        // 70% chance for healthy food, 30% chance for unhealthy
        boolean isHealthy = MathUtils.randomBoolean(0.7f);
        
        String texturePath = isHealthy ? "healthy_food.png" : "unhealthy_food.png";
        String name = isHealthy ? "HealthyFood_" + MathUtils.random(1000) : "UnhealthyFood_" + MathUtils.random(1000);
        
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
                
                // We need to adjust the enemy's position for rendering
                Vector2 origPos = enemy.getPosition();
                
                // Draw enemy at screen position
                enemy.renderAtPosition(batch, screenPos.x, screenPos.y);
            }
        }
        
        // Draw player (always centered)
        player.render(batch);
        
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
        
        // Draw bar outlines and labels
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(20, Gdx.graphics.getHeight() - 30, 200, 20);
        shapeRenderer.rect(Gdx.graphics.getWidth() - 220, Gdx.graphics.getHeight() - 30, 200, 20);
        shapeRenderer.end();
        
        // Draw text labels
        batch.begin();
        font.draw(batch, "Healthy", 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Unhealthy", Gdx.graphics.getWidth() - 220, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Size: " + player.getBodySize(), 20, 30);
        
        // Show game over or victory screen
        if (gameOver) {
            Texture overlayTexture = playerWon ? victoryTexture : gameOverTexture;
            if (overlayTexture != null) {
                batch.draw(overlayTexture, 
                        (Gdx.graphics.getWidth() - overlayTexture.getWidth()) / 2f,
                        (Gdx.graphics.getHeight() - overlayTexture.getHeight()) / 2f);
                
                font.draw(batch, "Press ENTER to continue",
                        Gdx.graphics.getWidth() / 2f - 120,
                        Gdx.graphics.getHeight() / 2f - 100);
            }
        }
        
        batch.end();
    }
    
    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (gameOverTexture != null) {
            gameOverTexture.dispose();
        }
        if (victoryTexture != null) {
            victoryTexture.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}