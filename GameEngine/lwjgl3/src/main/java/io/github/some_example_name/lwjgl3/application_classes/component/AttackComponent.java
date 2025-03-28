package io.github.some_example_name.lwjgl3.application_classes.component;

import java.util.ArrayList;
import java.util.List;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.Component;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

/**
 * Component that handles entity attacks with support for different attack types,
 * cooldowns, and damage calculation
 */
public class AttackComponent implements Component {
    private Entity owner;
    private List<Attack> attacks;
    private int selectedAttackIndex;
    private List<AttackListener> listeners;
    
    /**
     * Create a new attack component
     */
    public AttackComponent() {
        this.attacks = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.selectedAttackIndex = 0;
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
        // Reset all attack cooldowns
        for (Attack attack : attacks) {
            attack.resetCooldown();
        }
    }
    
    @Override
    public void update(float deltaTime) {
        // Update cooldowns for all attacks
        for (Attack attack : attacks) {
            attack.updateCooldown(deltaTime);
        }
    }
    
    /**
     * Perform the currently selected attack on a target
     * @param target The target to attack
     * @return true if the attack was successful
     */
    public boolean attack(Entity target) {
        if (attacks.isEmpty() || selectedAttackIndex < 0 || selectedAttackIndex >= attacks.size()) {
            return false;
        }
        
        Attack attack = attacks.get(selectedAttackIndex);
        return performAttack(attack, target);
    }
    
    /**
     * Perform a specific attack by name
     * @param attackName The name of the attack
     * @param target The target to attack
     * @return true if the attack was successful
     */
    public boolean attackByName(String attackName, Entity target) {
        for (Attack attack : attacks) {
            if (attack.getName().equals(attackName)) {
                return performAttack(attack, target);
            }
        }
        return false;
    }
    
    /**
     * Execute an attack on a target
     */
    private boolean performAttack(Attack attack, Entity target) {
        if (attack.isOnCooldown()) {
            return false;
        }
        
        // Check for health component on target
        HealthComponent targetHealth = target.getComponent(HealthComponent.class);
        if (targetHealth == null) {
            // Target has no health component, just notify attack
            notifyAttackPerformed(attack, target, 0);
            attack.triggerCooldown();
            return true;
        }
        
        // Calculate damage
        float damage = calculateDamage(attack, target);
        
        // Apply damage to target
        float actualDamage = targetHealth.damage(damage, owner);
        
        // Trigger cooldown and notify listeners
        attack.triggerCooldown();
        notifyAttackPerformed(attack, target, actualDamage);
        
        return true;
    }
    
    /**
     * Calculate attack damage based on attack properties and target
     */
    private float calculateDamage(Attack attack, Entity target) {
        float baseDamage = attack.getDamage();
        
        // Apply damage multipliers based on attack type
        // This could be expanded with resistances, critical hits, etc.
        return baseDamage;
    }
    
    /**
     * Add an attack
     * @param attack The attack to add
     */
    public void addAttack(Attack attack) {
        attacks.add(attack);
    }
    
    /**
     * Remove an attack
     * @param attack The attack to remove
     */
    public void removeAttack(Attack attack) {
        attacks.remove(attack);
        if (selectedAttackIndex >= attacks.size()) {
            selectedAttackIndex = attacks.isEmpty() ? -1 : 0;
        }
    }
    
    /**
     * Get all available attacks
     */
    public List<Attack> getAttacks() {
        return new ArrayList<>(attacks);
    }
    
    /**
     * Set the selected attack by index
     */
    public void selectAttack(int index) {
        if (index >= 0 && index < attacks.size()) {
            selectedAttackIndex = index;
        }
    }
    
    /**
     * Set the selected attack by name
     */
    public void selectAttack(String attackName) {
        for (int i = 0; i < attacks.size(); i++) {
            if (attacks.get(i).getName().equals(attackName)) {
                selectedAttackIndex = i;
                return;
            }
        }
    }
    
    /**
     * Get the currently selected attack
     */
    public Attack getSelectedAttack() {
        if (selectedAttackIndex >= 0 && selectedAttackIndex < attacks.size()) {
            return attacks.get(selectedAttackIndex);
        }
        return null;
    }
    
    /**
     * Add an attack listener
     */
    public void addListener(AttackListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove an attack listener
     */
    public void removeListener(AttackListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify listeners that an attack was performed
     */
    private void notifyAttackPerformed(Attack attack, Entity target, float damage) {
        for (AttackListener listener : listeners) {
            listener.onAttackPerformed(owner, attack, target, damage);
        }
    }
    
    /**
     * Class representing an attack with properties
     */
    public static class Attack {
        private String name;
        private float damage;
        private float range;
        private float cooldown;
        private float cooldownTimer;
        private AttackType type;
        
        /**
         * Create a new attack
         * @param name Attack name
         * @param damage Base damage
         * @param range Attack range
         * @param cooldown Cooldown time in seconds
         * @param type Attack type
         */
        public Attack(String name, float damage, float range, float cooldown, AttackType type) {
            this.name = name;
            this.damage = damage;
            this.range = range;
            this.cooldown = cooldown;
            this.type = type;
            this.cooldownTimer = 0;
        }
        
        /**
         * Get the attack name
         */
        public String getName() {
            return name;
        }
        
        /**
         * Get the base damage
         */
        public float getDamage() {
            return damage;
        }
        
        /**
         * Get the attack range
         */
        public float getRange() {
            return range;
        }
        
        /**
         * Get the cooldown time
         */
        public float getCooldown() {
            return cooldown;
        }
        
        /**
         * Get the attack type
         */
        public AttackType getType() {
            return type;
        }
        
        /**
         * Check if the attack is on cooldown
         */
        public boolean isOnCooldown() {
            return cooldownTimer > 0;
        }
        
        /**
         * Get the remaining cooldown time
         */
        public float getRemainingCooldown() {
            return cooldownTimer;
        }
        
        /**
         * Update the cooldown timer
         */
        public void updateCooldown(float deltaTime) {
            if (cooldownTimer > 0) {
                cooldownTimer -= deltaTime;
                if (cooldownTimer < 0) {
                    cooldownTimer = 0;
                }
            }
        }
        
        /**
         * Trigger the attack cooldown
         */
        public void triggerCooldown() {
            cooldownTimer = cooldown;
        }
        
        /**
         * Reset the cooldown timer
         */
        public void resetCooldown() {
            cooldownTimer = 0;
        }
    }
    
    /**
     * Enum for different attack types
     */
    public enum AttackType {
        MELEE,
        RANGED,
        MAGIC,
        SPECIAL
    }
    
    /**
     * Interface for attack listeners
     */
    public interface AttackListener {
        /**
         * Called when an attack is performed
         * @param attacker The attacking entity
         * @param attack The attack that was performed
         * @param target The target of the attack
         * @param damage The damage dealt
         */
        void onAttackPerformed(Entity attacker, Attack attack, Entity target, float damage);
    }
}