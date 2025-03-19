package io.github.some_example_name.lwjgl3.application_classes.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameState;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;

public class HealthSnakeDeathScene extends Scene {

    private Texture backgroundTexture;
    private Texture gameOverTexture;
    private Texture snakeSkullTexture;
    private Texture[] unhealthyFoodTextures;

    private SpriteBatch batch;
    private SceneManager sceneManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private IOManager ioManager;
    private BitmapFont font;

    private String[] menuItems = {
        "Try Again",
        "Main Menu",
        "Exit Game"
    };

    private int selectedItem = 0;
    private float timeElapsed = 0;

    // For particle effects
    private float[] particleX;
    private float[] particleY;
    private float[] particleSpeedX;
    private float[] particleSpeedY;
    private float[] particleSize;
    private float[] particleRotation;
    private float[] particleRotationSpeed;
    private int[] particleTextureIndex;
    private boolean[] particleIsUnhealthy;
    private final int particleCount = 30;

    private String deathMessage;
    private String[] deathMessages = {
        "Too many unhealthy snacks!",
        "Your snake couldn't handle the junk food!",
        "Healthy eating is important!",
        "Game Over - Try to eat more fruits!",
        "Your snake's diet was its downfall!"
    };

    private final int finalScore;
    private final String deathCause;

    public HealthSnakeDeathScene(SpriteBatch batch, SceneManager sceneManager,
            EntityManager entityManager, MovementManager movementManager, IOManager ioManager,
            int finalScore, String deathCause) {
        this.batch = batch;
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.ioManager = ioManager;
        this.finalScore = finalScore;
        this.deathCause = deathCause;

        font = new BitmapFont(Gdx.files.internal("game_font.fnt"));
        font.setColor(Color.WHITE);
        font.getData().setScale(0.3f);

        // Initialize particle system
        particleX = new float[particleCount];
        particleY = new float[particleCount];
        particleSpeedX = new float[particleCount];
        particleSpeedY = new float[particleCount];
        particleSize = new float[particleCount];
        particleRotation = new float[particleCount];
        particleRotationSpeed = new float[particleCount];
        particleIsUnhealthy = new boolean[particleCount];

        initializeParticles();

        // Select a random death message
        deathMessage = deathMessages[MathUtils.random(deathMessages.length - 1)];
    }

    private void initializeParticles() {
        for (int i = 0; i < particleCount; i++) {
            particleX[i] = MathUtils.random(0, Gdx.graphics.getWidth());
            particleY[i] = MathUtils.random(0, Gdx.graphics.getHeight());
            particleSpeedX[i] = MathUtils.random(-20f, 20f);
            particleSpeedY[i] = MathUtils.random(-20f, 20f);
            particleSize[i] = MathUtils.random(20f, 40f);
            particleRotation[i] = MathUtils.random(0, 360);
            particleRotationSpeed[i] = MathUtils.random(-50f, 50f);
            particleIsUnhealthy[i] = MathUtils.randomBoolean(0.7f); // 70% chance for unhealthy food particles
        }
    }

    @Override
    public void initialize() {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("snake_background.png"));
            gameOverTexture = new Texture(Gdx.files.internal("game_over.png"));
            snakeSkullTexture = new Texture(Gdx.files.internal("snake_skull.png"));
            unhealthyFoodTextures = new Texture[5];
            for (int i = 0; i < 5; i++) {
                unhealthyFoodTextures[i] = new Texture(Gdx.files.internal("unhealthy_" + (i + 1) + ".png"));
            }

            particleTextureIndex = new int[particleCount];
            for (int i = 0; i < particleCount; i++) {
                if (particleIsUnhealthy[i]) {
                    particleTextureIndex[i] = MathUtils.random(unhealthyFoodTextures.length - 1);
                }
            }

            System.out.println("[HealthSnakeDeathScene] Textures loaded successfully.");
        } catch (Exception e) {
            System.err.println("[HealthSnakeDeathScene] Error loading textures: " + e.getMessage());
            // Use placeholder handling if textures fail to load
        }

        // Play death sound
        ioManager.getAudio().stopMusic();
        ioManager.getAudio().playSound("game_over.mp3");

        // Start sad music after a delay
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000); // Wait 1 second
                    ioManager.getAudio().playMusic("sad_music.mp3");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void update(float deltaTime) {
        timeElapsed += deltaTime;

        // Update particles
        for (int i = 0; i < particleCount; i++) {
            particleX[i] += particleSpeedX[i] * deltaTime;
            particleY[i] += particleSpeedY[i] * deltaTime;
            particleRotation[i] += particleRotationSpeed[i] * deltaTime;

            // Wrap particles around screen
            if (particleX[i] < -particleSize[i]) {
                particleX[i] = Gdx.graphics.getWidth() + particleSize[i];
            }
            if (particleX[i] > Gdx.graphics.getWidth() + particleSize[i]) {
                particleX[i] = -particleSize[i];
            }
            if (particleY[i] < -particleSize[i]) {
                particleY[i] = Gdx.graphics.getHeight() + particleSize[i];
            }
            if (particleY[i] > Gdx.graphics.getHeight() + particleSize[i]) {
                particleY[i] = -particleSize[i];
            }
        }

        // Menu navigation
        if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.UP)) {
            selectedItem = (selectedItem - 1 + menuItems.length) % menuItems.length;
            ioManager.getAudio().playSound("menu_move.mp3");
        } else if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.DOWN)) {
            selectedItem = (selectedItem + 1) % menuItems.length;
            ioManager.getAudio().playSound("menu_move.mp3");
        }

        // Menu selection
        if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.ENTER)) {
            handleMenuSelection();
        }
    }

    private void handleMenuSelection() {
        ioManager.getAudio().playSound("menu_select.mp3");

        switch (selectedItem) {
            case 0: // Try Again
                System.out.println("[HealthSnakeDeathScene] Restarting game...");
                ioManager.getAudio().stopMusic();

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

            case 1: // Main Menu
                System.out.println("[HealthSnakeDeathScene] Returning to main menu...");
                ioManager.getAudio().stopMusic();

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

            case 2: // Exit Game
                System.out.println("[HealthSnakeDeathScene] Exiting game...");
                Gdx.app.exit();
                break;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();

        // Draw background
        if (backgroundTexture != null) {
            // Tint the background darker for death scene
            batch.setColor(0.5f, 0.5f, 0.5f, 1f);
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(Color.WHITE);
        }

        // Draw particles (food items floating in background)
        for (int i = 0; i < particleCount; i++) {
            Texture particleTexture;
            if (particleIsUnhealthy[i]) {
                particleTexture = unhealthyFoodTextures[particleTextureIndex[i]];
            } else {
                particleTexture = snakeSkullTexture;
            }

            if (particleTexture != null) {
                batch.draw(
                        particleTexture,
                        particleX[i] - particleSize[i] / 2,
                        particleY[i] - particleSize[i] / 2,
                        particleSize[i] / 2, // origin x
                        particleSize[i] / 2, // origin y
                        particleSize[i],
                        particleSize[i],
                        1, 1, // scale
                        particleRotation[i],
                        0, 0, // src xy
                        particleTexture.getWidth(),
                        particleTexture.getHeight(),
                        false, false // flip xy
                );
            }
        }

        // Draw game over text
        if (gameOverTexture != null) {
            float scale = 0.8f + 0.2f * (float) Math.sin(timeElapsed * 2);
            float width = gameOverTexture.getWidth() * scale;
            float height = gameOverTexture.getHeight() * scale;
            batch.draw(
                    gameOverTexture,
                    (Gdx.graphics.getWidth() - width) / 2,
                    Gdx.graphics.getHeight() - height - 50,
                    width,
                    height
            );
        }

        // Draw death message
        font.getData().setScale(0.3f);
        float messageWidth = font.draw(batch, deathMessage, 0, 0).width;
        font.setColor(1f, 0.3f, 0.3f, 1f);
        font.draw(
                batch,
                deathMessage,
                (Gdx.graphics.getWidth() - messageWidth) / 2,
                Gdx.graphics.getHeight() - 150
        );

        // Draw final score
        font.getData().setScale(0.3f);
        font.setColor(1f, 1f, 1f, 1f);
        String scoreText = "Final Score: " + finalScore;
        float scoreWidth = font.draw(batch, scoreText, 0, 0).width;
        font.draw(
                batch,
                scoreText,
                (Gdx.graphics.getWidth() - scoreWidth) / 2,
                Gdx.graphics.getHeight() - 200
        );

        // Draw death cause if provided
        if (deathCause != null && !deathCause.isEmpty()) {
            font.getData().setScale(0.3f);
            float causeWidth = font.draw(batch, "Cause: " + deathCause, 0, 0).width;
            font.draw(
                    batch,
                    "Cause: " + deathCause,
                    (Gdx.graphics.getWidth() - causeWidth) / 2,
                    Gdx.graphics.getHeight() - 230
            );
        }

        // Draw menu items
        font.getData().setScale(0.3f);
        float menuY = Gdx.graphics.getHeight() / 2 - 50;
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
        font.getData().setScale(0.3f);
        font.draw(batch, "Arrow Keys: Navigate | Enter: Select",
                Gdx.graphics.getWidth() / 2 - 225,
                50);

        batch.end();
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (gameOverTexture != null) {
            gameOverTexture.dispose();
        }
        if (snakeSkullTexture != null) {
            snakeSkullTexture.dispose();
        }
        if (unhealthyFoodTextures != null) {
            for (Texture texture : unhealthyFoodTextures) {
                texture.dispose();
            }
        }
        if (font != null) {
            font.dispose();
        }
    }
}
