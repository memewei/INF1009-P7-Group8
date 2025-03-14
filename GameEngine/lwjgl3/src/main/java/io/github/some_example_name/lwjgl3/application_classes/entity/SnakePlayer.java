package io.github.some_example_name.lwjgl3.application_classes.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;

public class SnakePlayer extends MovableEntity {
    private Array<SnakeSegment> bodySegments;
    private float direction; // in radians
    private float speed;
    private float initialSpeed = 150f;
    private float minSpeed = 80f;
    private float maxSpeed = 300f;
    private float turnSpeed = 3.0f; // radians per second
    
    private int healthyFoodCount = 0;
    private int unhealthyFoodCount = 0;
    private int healthyFoodGoal = 15;
    private int unhealthyFoodGoal = 10;
    
    private Texture headTexture;
    private Texture bodyTexture;
    
    private float segmentSpacing = 15f; // distance between segments
    private float bodySize = 20f; // size of body segments
    
    public SnakePlayer(String entityName, float positionX, float positionY, String headTexturePath, String bodyTexturePath) {
        super(entityName, positionX, positionY, headTexturePath);
        this.headTexture = new Texture(Gdx.files.internal(headTexturePath));
        this.bodyTexture = new Texture(Gdx.files.internal(bodyTexturePath));
        this.speed = initialSpeed;
        this.direction = 0f; // start facing right
        
        // Initialize body segments
        bodySegments = new Array<SnakeSegment>();
        
        // Add initial body segments
        for (int i = 0; i < 5; i++) {
            float xPos = positionX - (i + 1) * segmentSpacing;
            SnakeSegment segment = new SnakeSegment(xPos, positionY, bodySize);
            bodySegments.add(segment);
        }
    }
    
    @Override
    public void update(float deltaTime) {
        // Handle turning with arrow keys
        if (IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.LEFT)) {
            direction -= turnSpeed * deltaTime;
        }
        if (IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.RIGHT)) {
            direction += turnSpeed * deltaTime;
        }
        
        // Calculate new position based on direction and speed
        float moveX = MathUtils.cos(direction) * speed * deltaTime;
        float moveY = MathUtils.sin(direction) * speed * deltaTime;
        
        // Store previous position for first body segment
        Vector2 prevHeadPos = new Vector2(positionX, positionY);
        
        // Update head position
        positionX += moveX;
        positionY += moveY;
        
        // Keep head within screen bounds
        float halfSize = bodySize / 2;
        positionX = MathUtils.clamp(positionX, halfSize, Gdx.graphics.getWidth() - halfSize);
        positionY = MathUtils.clamp(positionY, halfSize, Gdx.graphics.getHeight() - halfSize);
        
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
        // Draw body segments (in reverse order so head appears on top)
        for (int i = bodySegments.size - 1; i >= 0; i--) {
            SnakeSegment segment = bodySegments.get(i);
            batch.draw(bodyTexture, 
                    segment.x - segment.size/2, 
                    segment.y - segment.size/2, 
                    segment.size, 
                    segment.size);
        }
        
        // Draw head
        batch.draw(headTexture, 
                positionX - bodySize/2, 
                positionY - bodySize/2, 
                bodySize/2, // origin x
                bodySize/2, // origin y
                bodySize, 
                bodySize, 
                1, 1, // scale x, y
                direction * MathUtils.radiansToDegrees, // rotation
                0, 0, // srcX, srcY
                headTexture.getWidth(), 
                headTexture.getHeight(), 
                false, false); // flip x, y
    }
    
    @Override
    public void move(float forceX, float forceY) {
        // This is handled in update() for smoother movement
    }
    
    @Override
    public void stop() {
        // Snake should not stop completely
    }
    
    public void eatFood(FoodEntity food) {
        if (food.isHealthy()) {
            // Eat healthy food
            healthyFoodCount++;
            
            // Grow slightly
            addBodySegment(1);
            
            // Increase speed (with cap)
            speed = Math.min(speed * 1.05f, maxSpeed);
            
            // Play positive sound
            IOManager.getInstance().getAudio().playSound("healthy_food.mp3");
        } else {
            // Eat unhealthy food
            unhealthyFoodCount++;
            
            // Grow more
            addBodySegment(3);
            
            // Decrease speed (with floor)
            speed = Math.max(speed * 0.95f, minSpeed);
            
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
            
            bodySegments.add(new SnakeSegment(newX, newY, bodySize));
        }
    }
    
    public Rectangle getHeadBounds() {
        return new Rectangle(positionX - bodySize/2, positionY - bodySize/2, bodySize, bodySize);
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
        return healthyFoodCount >= healthyFoodGoal;
    }
    
    public boolean checkLoseCondition() {
        return unhealthyFoodCount >= unhealthyFoodGoal;
    }
    
    public float getHealthyFoodPercentage() {
        return (float) healthyFoodCount / healthyFoodGoal;
    }
    
    public float getUnhealthyFoodPercentage() {
        return (float) unhealthyFoodCount / unhealthyFoodGoal;
    }
    
    public int getBodySize() {
        return bodySegments.size;
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