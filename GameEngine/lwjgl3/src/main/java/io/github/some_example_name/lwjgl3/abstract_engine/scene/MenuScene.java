package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

import java.util.List;

public class MenuScene extends Scene {
    private List<String> menuOptions;

    public MenuScene(int sceneID, List<String> menuOptions) {
        super(sceneID);
        this.menuOptions = menuOptions;
    }

    @Override
    public void update() {
        float deltaTime = com.badlogic.gdx.Gdx.graphics.getDeltaTime(); // Get deltaTime
        for (Entity entity : entityComponents) { // Iterate over entities
            entity.update(deltaTime); // Pass deltaTime correctly
        }
    }

    @Override
    public void render() {
        System.out.println("Rendering menu options:");
        for (String option : menuOptions) {
            System.out.println(option);
        }
    }

    @Override
    public void handleInput(String userInput) {
        if (menuOptions.contains(userInput)) {
            System.out.println("Selected option: " + userInput);
        } else {
            System.out.println("Invalid option. Please choose from available options.");
        }
    }

    @Override
    public void dispose() {
    }
}
