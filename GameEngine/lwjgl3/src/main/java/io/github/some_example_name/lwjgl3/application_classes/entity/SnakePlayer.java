package io.github.some_example_name.lwjgl3.application_classes.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.application_classes.game.LevelManager;
import io.github.some_example_name.lwjgl3.abstract_engine.control.ControlMode;

public class SnakePlayer extends MovableEntity {
    private Array<SnakeSegment> bodySegments;
    private float direction; // in radians
    private float speed;
    private float initialSpeed = 150f;
    private float minSpeed = 80f;
    private float maxSpeed = 300f;
    private float turnSpeed = 3.0f; // radians per second

    // Food tracking
    private int healthyFoodCount = 0;
    private int unhealthyFoodCount = 0;
    private int healthyCalories = 0;
    private int unhealthyCalories = 0;

    // Level management
    private LevelManager levelManager;

    private Texture headTexture;
    private Texture bodyTexture;

    private float segmentSpacing = 22f; // distance between segments
    private float baseBodySize = 28f; // base size of body segments
    private float currentBodySize; // current size after applying multipliers

    // For infinite world, we center the player on screen and move the world
    private boolean centeredOnScreen = true;

    // Vector to store last movement input
    private Vector2 movementInput = new Vector2(0, 0);

    public SnakePlayer(String entityName, float positionX, float positionY, String headTexturePath, String bodyTexturePath, LevelManager levelManager) {
        super(entityName, positionX, positionY, headTexturePath);
        this.headTexture = new Texture(Gdx.files.internal(headTexturePath));
        this.bodyTexture = new Texture(Gdx.files.internal(bodyTexturePath));
        this.levelManager = levelManager;


        // Apply level-specific settings
        this.speed = levelManager.getSnakeSpeed();
        this.currentBodySize = baseBodySize * levelManager.getSnakeSizeMultiplier();

        this.direction = 0f; // start facing right

        // Initialize body segments
        bodySegments = new Array<SnakeSegment>();

        // Add initial body segments
        for (int i = 0; i < 5; i++) {
            float xPos = positionX - (i + 1) * segmentSpacing;
            SnakeSegment segment = new SnakeSegment(xPos, positionY, currentBodySize);
            bodySegments.add(segment);
        }
    }

    public Vector2 getPosition() {
        return new Vector2(positionX, positionY);
    }


    @Override
    public void update(float deltaTime) {
        ControlMode controlMode = IOManager.getInstance().getControlMode();
    
        if (controlMode == ControlMode.MOUSE) {
            Vector2 mousePos = new Vector2(
                IOManager.getInstance().getDynamicInput().getMouseX(),
                Gdx.graphics.getHeight() - IOManager.getInstance().getDynamicInput().getMouseY()
            );
            Vector2 headPos = new Vector2(positionX, positionY);
            Vector2 dirToMouse = new Vector2(mousePos).sub(headPos).nor();
    
            // Calculate angle to mouse
            float targetAngle = dirToMouse.angleRad();
            
            // Use direct angle setting instead of lerp for more responsive control
            // Optionally add a small amount of smoothing if needed
            float smoothing = 0.7f; // Higher value = more responsive (0.0-1.0)
            direction = MathUtils.lerpAngle(direction, targetAngle, deltaTime * turnSpeed * smoothing);
        }
    
        // Movement calculations
        float moveX = MathUtils.cos(direction) * speed * deltaTime;
        float moveY = MathUtils.sin(direction) * speed * deltaTime;
    
        // Update head position
        Vector2 prevHeadPos = new Vector2(positionX, positionY);
        positionX += moveX;
        positionY += moveY;

        // If using infinite world, don't clamp the position
        if (!centeredOnScreen) {
            // Keep head within screen bounds
            float halfSize = currentBodySize / 2;
            positionX = MathUtils.clamp(positionX, halfSize, Gdx.graphics.getWidth() - halfSize);
            positionY = MathUtils.clamp(positionY, halfSize, Gdx.graphics.getHeight() - halfSize);
        }

        // Update body segments
        Vector2 prevPos = prevHeadPos;
        for (SnakeSegment segment : bodySegments) {
            Vector2 currentPos = new Vector2(segment.x, segment.y);

            // Calculate direction to previous segment
            Vector2 dir = new Vector2(prevPos).sub(currentPos);
            float dist = dir.len();

            // Only move if distance exceeds segment spacing
            if (dist > segmentSpacing) {
                dir.nor();
                segment.x += dir.x * (dist - segmentSpacing);
                segment.y += dir.y * (dist - segmentSpacing);
            }

            prevPos = new Vector2(segment.x, segment.y);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        float screenX, screenY;

        if (centeredOnScreen) {
            // For infinite world, the player is centered on screen
            screenX = Gdx.graphics.getWidth() / 2f;
            screenY = Gdx.graphics.getHeight() / 2f;
        } else {
            // For bounded world, use actual position
            screenX = positionX;
            screenY = positionY;
        }

        // Draw body segments (in reverse order so head appears on top)
        for (int i = bodySegments.size - 1; i >= 0; i--) {
            SnakeSegment segment = bodySegments.get(i);

            float segScreenX, segScreenY;
            if (centeredOnScreen) {
                // Calculate screen position relative to player-centered view
                segScreenX = screenX + (segment.x - positionX);
                segScreenY = screenY + (segment.y - positionY);
            } else {
                segScreenX = segment.x;
                segScreenY = segment.y;
            }

            batch.draw(bodyTexture,
                    segScreenX - segment.size/2,
                    segScreenY - segment.size/2,
                    segment.size,
                    segment.size);
        }

        // Draw head
        batch.draw(headTexture,
                screenX - currentBodySize/2,
                screenY - currentBodySize/2,
                currentBodySize/2, // origin x
                currentBodySize/2, // origin y
                currentBodySize,
                currentBodySize,
                1, 1, // scale x, y
                direction * MathUtils.radiansToDegrees, // rotation
                0, 0, // srcX, srcY
                headTexture.getWidth(),
                headTexture.getHeight(),
                false, false); // flip x, y
    }

    @Override
    public void move(float forceX, float forceY) {
        // Store the movement input for reference
        movementInput.x = forceX;
        movementInput.y = forceY;

        // Handle turning based on input with improved responsiveness
        if (forceX < 0) {
            // Turn left (counter-clockwise)
            direction += turnSpeed * Gdx.graphics.getDeltaTime() * 1.5f; // Increased turning speed
        } else if (forceX > 0) {
            // Turn right (clockwise)
            direction -= turnSpeed * Gdx.graphics.getDeltaTime() * 1.5f; // Increased turning speed
        }
    }

    @Override
    public void stop() {
        // Snake should not stop completely, so this is a no-op
        movementInput.set(0, 0);
    }

    public void eatFood(FoodEntity food) {
        if (food.isHealthy()) {
            // Eat healthy food
            healthyFoodCount++;

            // Add calories
            int calories = food.getCalories();
            healthyCalories += calories;

            // Grow slightly
            addBodySegment(1);

            // Play positive sound
            IOManager.getInstance().getAudio().playSound("healthy_food.mp3");
        } else {
            // Eat unhealthy food
            unhealthyFoodCount++;

            // Add calories
            int calories = food.getCalories();
            unhealthyCalories += calories;

            // Grow more (based on level)
            int segmentsToAdd = levelManager.isUnhealthyPath() ? 4 : 3;
            addBodySegment(segmentsToAdd);

            // Play negative sound
            IOManager.getInstance().getAudio().playSound("unhealthy_food.mp3");
        }
    }

    private void addBodySegment(int count) {
        // Add new segments at the end of the snake
        for (int i = 0; i < count; i++) {
            SnakeSegment lastSegment = bodySegments.size > 0 ? bodySegments.get(bodySegments.size - 1) : null;

            float newX, newY;
            if (lastSegment != null) {
                // Position new segment behind the last one
                newX = lastSegment.x;
                newY = lastSegment.y;
            } else {
                // If no segments exist yet, position behind the head
                float oppositeDirection = direction + MathUtils.PI;
                newX = positionX + MathUtils.cos(oppositeDirection) * segmentSpacing;
                newY = positionY + MathUtils.sin(oppositeDirection) * segmentSpacing;
            }

            bodySegments.add(new SnakeSegment(newX, newY, currentBodySize));
        }
    }

    /**
     * Apply level settings to the snake (after level change)
     */
    public void applyLevelSettings() {
        this.speed = levelManager.getSnakeSpeed();
        this.currentBodySize = baseBodySize * levelManager.getSnakeSizeMultiplier();

        // Update all segment sizes
        for (SnakeSegment segment : bodySegments) {
            segment.size = currentBodySize;
        }
    }

    /**
     * Reset the player for a new level but keep the current body size
     */
    public void resetForNewLevel(float posX, float posY) {
        // Clear food counters
        healthyFoodCount = 0;
        unhealthyFoodCount = 0;
        healthyCalories = 0;
        unhealthyCalories = 0;

        // Reset position
        this.positionX = posX;
        this.positionY = posY;
        this.direction = 0f;

        // Apply new level settings
        applyLevelSettings();

        // Keep existing body segments but reposition them
        for (int i = 0; i < bodySegments.size; i++) {
            SnakeSegment segment = bodySegments.get(i);
            segment.x = positionX - (i + 1) * segmentSpacing;
            segment.y = positionY;
            // Update size based on new level settings
            segment.size = currentBodySize;
        }
    }

    public Rectangle getHeadBounds() {
        return new Rectangle(positionX - currentBodySize/2, positionY - currentBodySize/2, currentBodySize, currentBodySize);
    }

    public Array<Rectangle> getBodyBounds() {
        Array<Rectangle> bounds = new Array<Rectangle>();
        for (SnakeSegment segment : bodySegments) {
            bounds.add(new Rectangle(segment.x - segment.size/2, segment.y - segment.size/2,
                                    segment.size, segment.size));
        }
        return bounds;
    }

    public boolean checkWinCondition() {
        return healthyFoodCount >= levelManager.getHealthyFoodGoal();
    }

    public boolean checkLoseCondition() {
        return unhealthyFoodCount >= levelManager.getUnhealthyFoodGoal();
    }

    public float getHealthyFoodPercentage() {
        return (float) healthyFoodCount / levelManager.getHealthyFoodGoal();
    }

    public float getUnhealthyFoodPercentage() {
        return (float) unhealthyFoodCount / levelManager.getUnhealthyFoodGoal();
    }

    public int getHealthyFoodCount() {
        return healthyFoodCount;
    }

    public int getUnhealthyFoodCount() {
        return unhealthyFoodCount;
    }

    public int getHealthyCalories() {
        return healthyCalories;
    }

    public int getUnhealthyCalories() {
        return unhealthyCalories;
    }

    public int getTotalCalories() {
        return healthyCalories + unhealthyCalories;
    }

    public int getBodySize() {
        return bodySegments.size;
    }

    public float getCurrentBodySize() {
        return currentBodySize;
    }

    /**
     * Sets whether the player is centered on screen (infinite world) or not
     */
    public void setCenteredOnScreen(boolean centered) {
        this.centeredOnScreen = centered;
    }

    /**
     * Checks if the player is using centered mode for infinite world
     */
    public boolean isCenteredOnScreen() {
        return centeredOnScreen;
    }

    @Override
    public void onCollision(Entity other) {
        if (other instanceof EnemySnake) {
            System.out.println("Player collided with enemy snake!");
            // Game over logic will be handled in the game scene
        } else if (other instanceof FoodEntity) {
            FoodEntity food = (FoodEntity) other;
            eatFood(food);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (headTexture != null) {
            headTexture.dispose();
        }
        if (bodyTexture != null) {
            bodyTexture.dispose();
        }
    }

    // Inner class to represent a body segment
    private class SnakeSegment {
        float x, y;
        float size;

        public SnakeSegment(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }
}
