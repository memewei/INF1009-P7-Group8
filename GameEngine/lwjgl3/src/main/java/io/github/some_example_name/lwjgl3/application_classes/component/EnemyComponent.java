package io.github.some_example_name.lwjgl3.application_classes.component;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.Component;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.PhysicsComponent;

/**
 * Component that provides basic enemy behavior such as following targets
 * and making movement decisions
 */
public class EnemyComponent implements Component {
    private Entity owner;
    private Entity target;
    private float detectionRange = 300f;
    private float attackRange = 50f;
    private float moveSpeed = 100f;
    private float turnSpeed = 2.0f;
    private float directionChangeTimer = 0f;
    private float directionChangeInterval = 3.0f;
    private float currentDirection = 0f;
    private boolean aggressive = true;
    private EnemyState state = EnemyState.IDLE;
    
    /**
     * Create a new enemy component with default settings
     */
    public EnemyComponent() {
        // Default constructor
    }
    
    /**
     * Create a new enemy component with specific settings
     * @param detectionRange Range at which to detect targets
     * @param attackRange Range at which to attack targets
     * @param moveSpeed Movement speed
     * @param aggressive Whether the enemy is aggressive by default
     */
    public EnemyComponent(float detectionRange, float attackRange, float moveSpeed, boolean aggressive) {
        this.detectionRange = detectionRange;
        this.attackRange = attackRange;
        this.moveSpeed = moveSpeed;
        this.aggressive = aggressive;
    }
    
    @Override
    public void setOwner(Entity owner) {
        this.owner = owner;
    }
    
    @Override
    public Entity getOwner() {
        return owner;
    }
    
    @Override
    public void initialize() {
        // Initialize random direction
        currentDirection = MathUtils.random(MathUtils.PI2);
        directionChangeInterval = MathUtils.random(2.0f, 5.0f);
    }
    
    @Override
    public void update(float deltaTime) {
        if (owner == null) return;
        
        // Update state
        updateState(deltaTime);
        
        // Handle behavior based on state
        switch (state) {
            case IDLE:
                updateIdleBehavior(deltaTime);
                break;
            case PATROL:
                updatePatrolBehavior(deltaTime);
                break;
            case CHASE:
                updateChaseBehavior(deltaTime);
                break;
            case ATTACK:
                updateAttackBehavior(deltaTime);
                break;
            case FLEE:
                updateFleeBehavior(deltaTime);
                break;
        }
    }
    
    /**
     * Update the enemy state based on current conditions
     */
    private void updateState(float deltaTime) {
        // If we have a target and we're aggressive, decide whether to chase or attack
        if (target != null && aggressive) {
            float distanceToTarget = getDistanceToTarget();
            
            if (distanceToTarget <= attackRange) {
                state = EnemyState.ATTACK;
            } else if (distanceToTarget <= detectionRange) {
                state = EnemyState.CHASE;
            } else {
                // Target exists but is too far away
                state = owner.hasComponent(PatrolComponent.class) ? EnemyState.PATROL : EnemyState.IDLE;
            }
        } else {
            // No target or not aggressive
            state = owner.hasComponent(PatrolComponent.class) ? EnemyState.PATROL : EnemyState.IDLE;
        }
    }
    
    /**
     * Idle behavior - random direction changes
     */
    private void updateIdleBehavior(float deltaTime) {
        // Update direction change timer
        directionChangeTimer += deltaTime;
        if (directionChangeTimer >= directionChangeInterval) {
            // Change direction randomly
            currentDirection += MathUtils.random(-MathUtils.PI/4, MathUtils.PI/4);
            directionChangeTimer = 0;
            directionChangeInterval = MathUtils.random(2.0f, 5.0f);
        }
        
        // Move slowly in the current direction
        moveInDirection(currentDirection, moveSpeed * 0.5f, deltaTime);
    }
    
    /**
     * Patrol behavior - follow patrol points if available
     */
    private void updatePatrolBehavior(float deltaTime) {
        PatrolComponent patrol = owner.getComponent(PatrolComponent.class);
        if (patrol != null) {
            patrol.update(deltaTime);
            
            // Get direction to current patrol point
            Vector2 targetPoint = patrol.getCurrentPatrolPoint();
            if (targetPoint != null) {
                Vector2 ownerPos = owner.getPosition();
                Vector2 direction = new Vector2(targetPoint).sub(ownerPos).nor();
                
                // Move toward patrol point
                moveInDirection(direction.angleRad(), moveSpeed * 0.7f, deltaTime);
            }
        } else {
            // Fall back to idle behavior if no patrol component
            updateIdleBehavior(deltaTime);
        }
    }
    
    /**
     * Chase behavior - move toward target
     */
    private void updateChaseBehavior(float deltaTime) {
        if (target == null) return;
        
        // Get direction to target
        Vector2 targetPos = target.getPosition();
        Vector2 ownerPos = owner.getPosition();
        Vector2 direction = new Vector2(targetPos).sub(ownerPos).nor();
        
        // Move toward target
        moveInDirection(direction.angleRad(), moveSpeed, deltaTime);
    }
    
    /**
     * Attack behavior - stay close to target and attempt attacks
     */
    private void updateAttackBehavior(float deltaTime) {
        if (target == null) return;
        
        // Check if we should attempt an attack
        attemptAttack();
        
        // Get direction to target
        Vector2 targetPos = target.getPosition();
        Vector2 ownerPos = owner.getPosition();
        Vector2 direction = new Vector2(targetPos).sub(ownerPos).nor();
        
        // Stay at attack range - move slightly toward or away from target
        float distanceToTarget = getDistanceToTarget();
        float moveMultiplier = distanceToTarget > attackRange ? 1.0f : -0.5f;
        
        moveInDirection(direction.angleRad(), moveSpeed * moveMultiplier, deltaTime);
    }
    
    /**
     * Flee behavior - move away from target
     */
    private void updateFleeBehavior(float deltaTime) {
        if (target == null) return;
        
        // Get direction away from target
        Vector2 targetPos = target.getPosition();
        Vector2 ownerPos = owner.getPosition();
        Vector2 direction = new Vector2(ownerPos).sub(targetPos).nor();
        
        // Move away from target
        moveInDirection(direction.angleRad(), moveSpeed * 1.2f, deltaTime);
    }
    
    private float wrapAngle(float angle) {
        angle = angle % (2 * (float)Math.PI);
        if (angle > Math.PI) angle -= 2 * Math.PI;
        if (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }

    /**
     * Attempt to attack the target
     */
    private void attemptAttack() {
        // Check if we have an attack component
        AttackComponent attackComponent = owner.getComponent(AttackComponent.class);
        if (attackComponent != null && target != null) {
            // Check cooldown and range
            float distanceToTarget = getDistanceToTarget();
            if (distanceToTarget <= attackRange) {
                attackComponent.attack(target);
            }
        }
    }
    
    /**
     * Move the entity in the specified direction
     */
    private void moveInDirection(float direction, float speed, float deltaTime) {
        // Gradually rotate toward target direction (smooth turning)
        float angleDiff = wrapAngle(direction - currentDirection);
        float maxTurn = turnSpeed * deltaTime;
        
        if (Math.abs(angleDiff) <= maxTurn) {
            currentDirection = direction;
        } else {
            currentDirection += Math.signum(angleDiff) * maxTurn;
        }
        
        // Calculate movement vector
        float moveX = MathUtils.cos(currentDirection) * speed * deltaTime;
        float moveY = MathUtils.sin(currentDirection) * speed * deltaTime;
        
        // Apply movement to physics component if available
        PhysicsComponent physics = owner.getComponent(PhysicsComponent.class);
        if (physics != null) {
            physics.applyForce(moveX * 10, moveY * 10); // Force multiplier for physics
        } else {
            // Otherwise, directly update position
            Vector2 position = owner.getPosition();
            owner.setPosition(position.x + moveX, position.y + moveY);
        }
    }
    
    /**
     * Calculate distance to the current target
     */
    private float getDistanceToTarget() {
        if (target == null || owner == null) return Float.MAX_VALUE;
        
        Vector2 targetPos = target.getPosition();
        Vector2 ownerPos = owner.getPosition();
        return targetPos.dst(ownerPos);
    }
    
    /**
     * Set the target entity to follow/attack
     */
    public void setTarget(Entity target) {
        this.target = target;
    }
    
    /**
     * Get the current target entity
     */
    public Entity getTarget() {
        return target;
    }
    
    /**
     * Set the detection range
     */
    public void setDetectionRange(float range) {
        this.detectionRange = range;
    }
    
    /**
     * Get the detection range
     */
    public float getDetectionRange() {
        return detectionRange;
    }
    
    /**
     * Set the attack range
     */
    public void setAttackRange(float range) {
        this.attackRange = range;
    }
    
    /**
     * Get the attack range
     */
    public float getAttackRange() {
        return attackRange;
    }
    
    /**
     * Set the move speed
     */
    public void setMoveSpeed(float speed) {
        this.moveSpeed = speed;
    }
    
    /**
     * Get the move speed
     */
    public float getMoveSpeed() {
        return moveSpeed;
    }
    
    /**
     * Set whether the enemy is aggressive
     */
    public void setAggressive(boolean aggressive) {
        this.aggressive = aggressive;
    }
    
    /**
     * Check if the enemy is aggressive
     */
    public boolean isAggressive() {
        return aggressive;
    }
    
    /**
     * Get the current state
     */
    public EnemyState getState() {
        return state;
    }
    
    /**
     * Enum for different enemy states
     */
    public enum EnemyState {
        IDLE,
        PATROL,
        CHASE,
        ATTACK,
        FLEE
    }
}