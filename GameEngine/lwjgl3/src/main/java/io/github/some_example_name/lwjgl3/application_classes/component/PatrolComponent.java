package io.github.some_example_name.lwjgl3.application_classes.component;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.Component;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

/**
 * Component that handles entity patrolling along a set of waypoints
 */
public class PatrolComponent implements Component {
    private Entity owner;
    private List<Vector2> patrolPoints;
    private int currentPointIndex;
    private float waypointThreshold = 20f; // How close to get to a point before moving to next
    private boolean loop = true;
    private boolean reversing = false;
    private float waitTimer = 0f;
    private float waitTime = 1f; // Time to wait at each waypoint
    
    /**
     * Create a new patrol component
     */
    public PatrolComponent() {
        patrolPoints = new ArrayList<>();
        currentPointIndex = 0;
    }
    
    /**
     * Create a new patrol component with specific waypoints
     * @param waypoints List of patrol points
     */
    public PatrolComponent(List<Vector2> waypoints) {
        this.patrolPoints = new ArrayList<>(waypoints);
        currentPointIndex = 0;
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
        // Reset to first point
        currentPointIndex = 0;
        waitTimer = 0f;
        reversing = false;
    }
    
    @Override
    public void update(float deltaTime) {
        if (patrolPoints.isEmpty() || owner == null) return;
        
        // If waiting at a waypoint, update timer
        if (waitTimer > 0) {
            waitTimer -= deltaTime;
            return;
        }
        
        // Check if we've reached the current waypoint
        Vector2 currentPoint = patrolPoints.get(currentPointIndex);
        Vector2 ownerPos = owner.getPosition();
        
        float distanceToPoint = currentPoint.dst(ownerPos);
        if (distanceToPoint <= waypointThreshold) {
            // We've reached the waypoint, move to next
            waitTimer = waitTime;
            moveToNextWaypoint();
        }
    }
    
    /**
     * Move to the next waypoint in the sequence
     */
    private void moveToNextWaypoint() {
        if (patrolPoints.isEmpty()) return;
        
        if (reversing) {
            currentPointIndex--;
            
            if (currentPointIndex < 0) {
                if (loop) {
                    reversing = false;
                    currentPointIndex = 1; // Skip the first point we just reached
                    
                    // If there's only one point, stay there
                    if (currentPointIndex >= patrolPoints.size()) {
                        currentPointIndex = 0;
                    }
                } else {
                    currentPointIndex = 0;
                }
            }
        } else {
            currentPointIndex++;
            
            if (currentPointIndex >= patrolPoints.size()) {
                if (loop) {
                    if (patrolPoints.size() > 1) {
                        // Either loop back to first point or reverse direction
                        if (loop) {
                            currentPointIndex = 0;
                        } else {
                            reversing = true;
                            currentPointIndex = patrolPoints.size() - 2; // Skip the last point we just reached
                        }
                    } else {
                        // Only one point, just stay there
                        currentPointIndex = 0;
                    }
                } else {
                    currentPointIndex = patrolPoints.size() - 1;
                }
            }
        }
    }
    
    /**
     * Get the current patrol point
     */
    public Vector2 getCurrentPatrolPoint() {
        if (patrolPoints.isEmpty() || currentPointIndex < 0 || currentPointIndex >= patrolPoints.size()) {
            return null;
        }
        return patrolPoints.get(currentPointIndex);
    }
    
    /**
     * Add a patrol point
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void addPatrolPoint(float x, float y) {
        patrolPoints.add(new Vector2(x, y));
    }
    
    /**
     * Add a patrol point
     * @param point The point to add
     */
    public void addPatrolPoint(Vector2 point) {
        patrolPoints.add(new Vector2(point)); // Make a copy to avoid reference issues
    }
    
    /**
     * Remove a patrol point
     * @param index The index of the point to remove
     */
    public void removePatrolPoint(int index) {
        if (index >= 0 && index < patrolPoints.size()) {
            patrolPoints.remove(index);
            if (currentPointIndex >= patrolPoints.size()) {
                currentPointIndex = 0;
            }
        }
    }
    
    /**
     * Clear all patrol points
     */
    public void clearPatrolPoints() {
        patrolPoints.clear();
        currentPointIndex = 0;
    }
    
    /**
     * Get all patrol points
     */
    public List<Vector2> getPatrolPoints() {
        return new ArrayList<>(patrolPoints); // Return a copy
    }
    
    /**
     * Set the threshold distance for reaching waypoints
     */
    public void setWaypointThreshold(float threshold) {
        this.waypointThreshold = Math.max(1f, threshold);
    }
    
    /**
     * Get the waypoint threshold
     */
    public float getWaypointThreshold() {
        return waypointThreshold;
    }
    
    /**
     * Set whether the patrol should loop
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    
    /**
     * Check if the patrol is set to loop
     */
    public boolean isLoop() {
        return loop;
    }
    
    /**
     * Set the wait time at each waypoint
     */
    public void setWaitTime(float waitTime) {
        this.waitTime = Math.max(0f, waitTime);
    }
    
    /**
     * Get the wait time at each waypoint
     */
    public float getWaitTime() {
        return waitTime;
    }
}