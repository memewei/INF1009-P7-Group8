package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.utils.Disposable;

public abstract class Scene implements Disposable {
    public abstract void render();
}