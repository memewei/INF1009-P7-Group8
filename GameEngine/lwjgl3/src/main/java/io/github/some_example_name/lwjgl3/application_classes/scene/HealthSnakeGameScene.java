package io.github.some_example_name.lwjgl3.application_classes.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
    
    private float foodSpawnTimer = 0;
    private float foodSpawnInterval = 2f; // Seconds between food spawns
    private int maxFood = 15; // Maximum food items on screen
    
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
        System.out.println("[HealthSnakeGameScene] Initializing...");
        
        entityManager.clearEntities();
        
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
        
        // Create player in the center of the screen
        player = new SnakePlayer("Player", 
                            Gdx.graphics.getWidth() / 2f, 
                            Gdx.graphics.getHeight() / 2f,
                            "snake_head.png", 
                            "snake_body.png");
        entityManager.addEntity(player);
        
        // Create enemy snakes
        for (int i = 0; i < 5; i++) {
            EnemySnake enemy = EnemySnake.createRandomEnemy(i);
            enemies.add(enemy);
            entityManager.addEntity(enemy);
        }
        
        // Initial food items
        for (int i = 0; i < 10; i++) {
            spawnFood();
        }
        
        gameOver = false;
        playerWon = false;
    }
    
    private void spawnFood() {
        // 70% chance for healthy food, 30% chance for unhealthy
        boolean isHealthy = MathUtils.randomBoolean(0.7f);
        FoodEntity food = FoodEntity.createRandomFood(isHealthy);
        foods.add(food);
        entityManager.addEntity(food);
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
            spawnFood();
            foodSpawnTimer = 0;
        }
        
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
        
        // Update all entities
        player.update(deltaTime);
        for (EnemySnake enemy : enemies) {
            enemy.update(deltaTime);
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        // Draw background
        batch.begin();
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        
        // Draw all entities
        for (FoodEntity food : foods) {
            food.render(batch);
        }
        
        for (EnemySnake enemy : enemies) {
            enemy.render(batch);
        }
        
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