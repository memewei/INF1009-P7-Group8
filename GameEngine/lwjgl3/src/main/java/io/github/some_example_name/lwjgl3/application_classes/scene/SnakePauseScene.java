package io.github.some_example_name.lwjgl3.application_classes.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameState;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;

public class SnakePauseScene extends Scene {
    private Texture pauseBackground;
    private SpriteBatch batch;
    private SceneManager sceneManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private IOManager ioManager;
    private BitmapFont font;

    private String[] menuItems = {
        "Resume Game",
        "Restart Game",
        "Return to Main Menu",
        "Exit Game"
    };

    private int selectedItem = 0;
    private float timeElapsed = 0;

    public SnakePauseScene(SpriteBatch batch, SceneManager sceneManager,
                         EntityManager entityManager, MovementManager movementManager, IOManager ioManager) {
        this.batch = batch;
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.ioManager = ioManager;

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
    }

    @Override
    public void initialize() {
        System.out.println("[SnakePauseScene] Initializing...");

        try {
            pauseBackground = new Texture(Gdx.files.internal("pause_menu.png"));
            System.out.println("[SnakePauseScene] Pause menu loaded.");
        } catch (Exception e) {
            System.err.println("[SnakePauseScene] Error loading background: " + e.getMessage());
            // Create a default semi-transparent black texture
        }

        // Play pause sound
        ioManager.getAudio().playSound("pause.mp3");
    }

    @Override
    public void update(float deltaTime) {
        timeElapsed += deltaTime;

        // Menu navigation
        if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.UP)) {
            selectedItem = (selectedItem - 1 + menuItems.length) % menuItems.length;
            ioManager.getAudio().playSound("menu_move.mp3");
        } else if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.DOWN)) {
            selectedItem = (selectedItem + 1) % menuItems.length;
            ioManager.getAudio().playSound("menu_move.mp3");
        }

        // Escape key resumes game (same as selecting "Resume Game")
        if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.ESCAPE)) {
            resumeGame();
            return;
        }

        // Enter key selects current menu item
        if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.ENTER)) {
            handleMenuSelection();
        }
    }

    private void handleMenuSelection() {
        ioManager.getAudio().playSound("menu_select.mp3");

        switch (selectedItem) {
            case 0: // Resume Game
                resumeGame();
                break;

            case 1: // Restart Game
                System.out.println("[SnakePauseScene] Restarting game...");
                // Remove current pause scene
                sceneManager.popScene();
                // Replace the current game scene with a new one
                sceneManager.changeScene(
                    new HealthSnakeGameScene(
                        batch,
                        entityManager,
                        movementManager,
                        sceneManager.getWorld(),
                        sceneManager,
                        ioManager
                    ),
                    GameState.RUNNING
                );
                break;

            case 2: // Return to Main Menu
                System.out.println("[SnakePauseScene] Returning to main menu...");
                sceneManager.changeScene(
                    new HealthSnakeMenuScene(
                        batch,
                        sceneManager,
                        entityManager,
                        movementManager,
                        ioManager
                    ),
                    GameState.MAIN_MENU
                );
                break;

            case 3: // Exit Game
                System.out.println("[SnakePauseScene] Exiting game...");
                Gdx.app.exit();
                break;
        }
    }

    private void resumeGame() {
        System.out.println("[SnakePauseScene] Resuming game...");
        if (sceneManager != null) {
            sceneManager.popScene();
            sceneManager.setGameState(GameState.RUNNING);
        } else {
            System.err.println("[SnakePauseScene] sceneManager is NULL! Cannot resume.");
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();

        // Draw semi-transparent overlay if no background texture
        if (pauseBackground != null) {
            batch.draw(pauseBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        // Draw pause title
        font.getData().setScale(2.5f);
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "GAME PAUSED",
                Gdx.graphics.getWidth() / 2 - 120,
                Gdx.graphics.getHeight() - 100);

        // Draw menu items
        font.getData().setScale(1.5f);
        float menuY = Gdx.graphics.getHeight() / 2 + 50;
        float menuSpacing = 50;

        for (int i = 0; i < menuItems.length; i++) {
            // Highlight selected item
            if (i == selectedItem) {
                // Pulsing effect for selected item
                float pulse = (float) Math.sin(timeElapsed * 5) * 0.2f + 0.8f;
                font.setColor(1f, pulse, pulse, 1f);
                font.draw(batch, "> " + menuItems[i] + " <",
                        Gdx.graphics.getWidth() / 2 - 150,
                        menuY - i * menuSpacing);
                font.setColor(Color.WHITE); // Reset color
            } else {
                font.draw(batch, menuItems[i],
                        Gdx.graphics.getWidth() / 2 - 100,
                        menuY - i * menuSpacing);
            }
        }

        // Draw controls hint
        font.getData().setScale(1.0f);
        font.draw(batch, "Arrow Keys: Navigate | Enter: Select | Esc: Resume",
                Gdx.graphics.getWidth() / 2 - 220,
                50);

        batch.end();
    }

    @Override
    public void dispose() {
        if (pauseBackground != null) {
            pauseBackground.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        System.out.println("[SnakePauseScene] Resources disposed.");
    }
}
