package io.github.some_example_name.lwjgl3.abstract_engine.movement;

public interface IMovable {
    void move(float forceX);
    void jump();
    void stop();
}
