package io.github.some_example_name.lwjgl3.application_classes.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;

public class EnemySnake extends Entity implements Collidable {
    private Array<Vector2> bodySegments;
    private float direction; // in radians
    private float speed;
    private float turnSpeed;
    private float segmentSpacing = 22f;
    private float bodySize = 28f;
    private float directionChangeTimer;
    private float directionChangeInterval;
    private Texture headTexture;
    private Texture bodyTexture;
    
    public EnemySnake(String entityName, float positionX, float positionY, 
                     String headTexturePath, String bodyTexturePath, int length) {
        super(entityName, positionX, positionY, headTexturePath);
        
        this.headTexture = new Texture(Gdx.files.internal(headTexturePath));
        this.bodyTexture = new Texture(Gdx.files.internal(bodyTexturePath));
        
        // Random speed between 80 and 150
        this.speed = MathUtils.random(80f, 150f);
        // Random turn speed between 1 and 2.5 radians per second
        this.turnSpeed = MathUtils.random(1.0f, 2.5f);
        
        // Random initial direction
        this.direction = MathUtils.random(MathUtils.PI2);
        
        // Random time between direction changes (2-5 seconds)
        this.directionChangeInterval = MathUtils.random(2f, 5f);
        this.directionChangeTimer = 0;
        
        // Initialize body segments
        bodySegments = new Array<Vector2>(length);
        
        // Add initial body segments
        for (int i = 0; i < length; i++) {
            float xPos = positionX - (i + 1) * segmentSpacing * MathUtils.cos(direction);
            float yPos = positionY - (i + 1) * segmentSpacing * MathUtils.sin(direction);
            bodySegments.add(new Vector2(xPos, yPos));
        }
    }
    
    /**
     * Creates an enemy snake at a random position on the screen
     */
    public static EnemySnake createRandomEnemy(int idNumber) {
        // Create enemy away from the center of the screen
        float x, y;
        do {
            x = MathUtils.random(50, Gdx.graphics.getWidth() - 50);
            y = MathUtils.random(50, Gdx.graphics.getHeight() - 50);
        } while (Math.abs(x - Gdx.graphics.getWidth()/2) < 300 && 
                Math.abs(y - Gdx.graphics.getHeight()/2) < 300);
        
        String name = "EnemySnake_" + idNumber;
        int length = MathUtils.random(5, 15); // Random length between 5-15 segments
        
        return new EnemySnake(name, x, y, "enemy_head.png", "enemy_body.png", length);
    }
    
    @Override
    public void update(float deltaTime) {
        // Update direction change timer
        directionChangeTimer += deltaTime;
        if (directionChangeTimer >= directionChangeInterval) {
            // Change direction by a random amount
            direction += MathUtils.random(-1f, 1f);
            directionChangeTimer = 0;
            // Set a new random interval for the next change
            directionChangeInterval = MathUtils.random(2f, 5f);
        }
        
        // Calculate new position based on direction and speed
        float moveX = MathUtils.cos(direction) * speed * deltaTime;
        float moveY = MathUtils.sin(direction) * speed * deltaTime;
        
        // Store previous head position
        Vector2 prevHeadPos = new Vector2(positionX, positionY);
        
        // Update head position
        positionX += moveX;
        positionY += moveY;
        
        // Update body segments
        Vector2 prevPos = prevHeadPos;
        for (Vector2 segment : bodySegments) {
            Vector2 currentPos = new Vector2(segment);
            
            // Calculate direction to previous segment
            Vector2 dir = new Vector2(prevPos).sub(currentPos);
            float dist = dir.len();
            
            // Only move if distance exceeds segment spacing
            if (dist > segmentSpacing) {
                dir.nor();
                segment.add(dir.scl(dist - segmentSpacing));
            }
            
            prevPos = new Vector2(segment);
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        // Draw body segments
        for (int i = bodySegments.size - 1; i >= 0; i--) {
            Vector2 segment = bodySegments.get(i);
            batch.draw(bodyTexture, 
                    segment.x - bodySize/2, 
                    segment.y - bodySize/2, 
                    bodySize, 
                    bodySize);
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
    
    /**
     * Renders the snake at a specific screen position (for infinite world scrolling)
     */
    public void renderAtPosition(SpriteBatch batch, float screenX, float screenY) {
        // Calculate the offset from the snake's world position to the screen position
        float offsetX = screenX - positionX;
        float offsetY = screenY - positionY;
        
        // Draw body segments at offset positions
        for (int i = bodySegments.size - 1; i >= 0; i--) {
            Vector2 segment = bodySegments.get(i);
            batch.draw(bodyTexture, 
                    segment.x + offsetX - bodySize/2, 
                    segment.y + offsetY - bodySize/2, 
                    bodySize, 
                    bodySize);
        }
        
        // Draw head at offset position
        batch.draw(headTexture, 
                screenX - bodySize/2, 
                screenY - bodySize/2, 
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
    
    public Rectangle getHeadBounds() {
        return new Rectangle(positionX - bodySize/2, positionY - bodySize/2, bodySize, bodySize);
    }
    
    public Array<Rectangle> getBodyBounds() {
        Array<Rectangle> bounds = new Array<Rectangle>();
        for (Vector2 segment : bodySegments) {
            bounds.add(new Rectangle(segment.x - bodySize/2, segment.y - bodySize/2, 
                                    bodySize, bodySize));
        }
        return bounds;
    }
    
    @Override
    public void onCollision(Entity other) {
        // Enemy snakes don't react to collisions
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
}