package io.github.some_example_name.lwjgl3.abstract_engine.entity;

/**
 * Interface for components and other objects that need to clean up resources.
 * Implementing classes should dispose of any resources that could cause memory leaks.
 */
public interface Disposable {
    
    /**
     * Dispose of resources to prevent memory leaks
     */
    void dispose();
}