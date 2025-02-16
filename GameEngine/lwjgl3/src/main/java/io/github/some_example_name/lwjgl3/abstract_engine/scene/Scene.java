package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected List<Entity> entityComponents;
    protected int sceneID;

    public Scene(int sceneID) {
        this.sceneID = sceneID;
        this.entityComponents = new ArrayList<>();
    }

    public abstract void update();

    public abstract void render();

    public abstract void handleInput(String userInput);

    public abstract void dispose();
}
