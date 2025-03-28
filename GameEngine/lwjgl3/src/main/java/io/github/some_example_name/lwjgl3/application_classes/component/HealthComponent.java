package io.github.some_example_name.lwjgl3.application_classes.component;

import java.util.ArrayList;
import java.util.List;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.Component;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

/**
 * Component that handles entity health, damage, and related events
 */
public class HealthComponent implements Component {
    private Entity owner;
    private float maxHealth;
    private float currentHealth;
    private boolean invulnerable = false;
    private float invulnerabilityTimer = 0;
    private boolean alive = true;
    
    private List<HealthListener> listeners = new ArrayList<>();
    
    /**
     * Create a new health component with the specified max health
     * @param maxHealth The maximum health value
     */
    public HealthComponent(float maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
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
        // Already initialized in constructor
    }
    
    @Override
    public void update(float deltaTime) {
        // Handle invulnerability timer
        if (invulnerable && invulnerabilityTimer > 0) {
            invulnerabilityTimer -= deltaTime;
            if (invulnerabilityTimer <= 0) {
                invulnerable = false;
            }
        }
    }
    
    /**
     * Apply damage to the entity
     * @param amount The amount of damage to apply
     * @param source The source of the damage (can be null)
     * @return The actual amount of damage applied
     */
    public float damage(float amount, Entity source) {
        if (invulnerable || !alive || amount <= 0) {
            return 0;
        }
        
        float oldHealth = currentHealth;
        currentHealth -= amount;
        
        // Check for death
        if (currentHealth <= 0) {
            currentHealth = 0;
            alive = false;
            notifyDeath(source);
        }
        
        // Notify health changed
        float actualDamage = oldHealth - currentHealth;
        notifyDamage(actualDamage, source);
        
        return actualDamage;
    }
    
    /**
     * Heal the entity
     * @param amount The amount to heal
     * @param source The source of the healing (can be null)
     * @return The actual amount healed
     */
    public float heal(float amount, Entity source) {
        if (!alive || amount <= 0) {
            return 0;
        }
        
        float oldHealth = currentHealth;
        currentHealth = Math.min(currentHealth + amount, maxHealth);
        
        // Notify health changed
        float actualHeal = currentHealth - oldHealth;
        if (actualHeal > 0) {
            notifyHeal(actualHeal, source);
        }
        
        return actualHeal;
    }
    
    /**
     * Make the entity invulnerable for a duration
     * @param duration The duration in seconds
     */
    public void setInvulnerable(float duration) {
        invulnerable = true;
        invulnerabilityTimer = duration;
    }
    
    /**
     * Revive the entity with the specified health percentage
     * @param healthPercentage The percentage of max health to restore (0-1)
     */
    public void revive(float healthPercentage) {
        if (!alive) {
            alive = true;
            currentHealth = maxHealth * Math.max(0, Math.min(1, healthPercentage));
            notifyRevive();
        }
    }
    
    /**
     * Get the current health value
     */
    public float getCurrentHealth() {
        return currentHealth;
    }
    
    /**
     * Get the maximum health value
     */
    public float getMaxHealth() {
        return maxHealth;
    }
    
    /**
     * Set the maximum health value
     * @param maxHealth The new maximum health
     * @param adjustCurrent Whether to adjust current health proportionally
     */
    public void setMaxHealth(float maxHealth, boolean adjustCurrent) {
        if (maxHealth <= 0) {
            return;
        }
        
        if (adjustCurrent && this.maxHealth > 0) {
            // Adjust current health proportionally
            float ratio = currentHealth / this.maxHealth;
            this.maxHealth = maxHealth;
            currentHealth = this.maxHealth * ratio;
        } else {
            this.maxHealth = maxHealth;
            // Cap current health at new max
            if (currentHealth > this.maxHealth) {
                currentHealth = this.maxHealth;
            }
        }
        
        notifyHealthChanged();
    }
    
    /**
     * Get the current health as a percentage (0-1)
     */
    public float getHealthPercentage() {
        return maxHealth > 0 ? currentHealth / maxHealth : 0;
    }
    
    /**
     * Check if the entity is alive
     */
    public boolean isAlive() {
        return alive;
    }
    
    /**
     * Check if the entity is currently invulnerable
     */
    public boolean isInvulnerable() {
        return invulnerable;
    }
    
    /**
     * Add a health listener
     * @param listener The listener to add
     */
    public void addListener(HealthListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove a health listener
     * @param listener The listener to remove
     */
    public void removeListener(HealthListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify listeners that health has changed
     */
    private void notifyHealthChanged() {
        for (HealthListener listener : listeners) {
            listener.onHealthChanged(this, currentHealth, maxHealth);
        }
    }
    
    /**
     * Notify listeners that damage was taken
     */
    private void notifyDamage(float amount, Entity source) {
        for (HealthListener listener : listeners) {
            listener.onDamageTaken(this, amount, source);
        }
        notifyHealthChanged();
    }
    
    /**
     * Notify listeners that healing occurred
     */
    private void notifyHeal(float amount, Entity source) {
        for (HealthListener listener : listeners) {
            listener.onHealReceived(this, amount, source);
        }
        notifyHealthChanged();
    }
    
    /**
     * Notify listeners that death occurred
     */
    private void notifyDeath(Entity source) {
        for (HealthListener listener : listeners) {
            listener.onDeath(this, source);
        }
    }
    
    /**
     * Notify listeners that revival occurred
     */
    private void notifyRevive() {
        for (HealthListener listener : listeners) {
            listener.onRevive(this);
        }
        notifyHealthChanged();
    }
    
    /**
     * Interface for health event listeners
     */
    public interface HealthListener {
        /**
         * Called when health has changed
         */
        void onHealthChanged(HealthComponent component, float currentHealth, float maxHealth);
        
        /**
         * Called when damage is taken
         */
        void onDamageTaken(HealthComponent component, float amount, Entity source);
        
        /**
         * Called when healing is received
         */
        void onHealReceived(HealthComponent component, float amount, Entity source);
        
        /**
         * Called when the entity dies
         */
        void onDeath(HealthComponent component, Entity source);
        
        /**
         * Called when the entity is revived
         */
        void onRevive(HealthComponent component);
    }
}