package io.github.some_example_name.lwjgl3.application_classes.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameState;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;

public class SnakeSettingScene extends Scene{
	private SpriteBatch batch;
    private SceneManager sceneManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private IOManager ioManager;

    private BitmapFont font;
    private Stage stage;
    private Texture soundSliderTexture, soundBarTexture, snakeTexture;
    private Viewport viewport;
    private Skin skin;

    public SnakeSettingScene(SpriteBatch batch, SceneManager sceneManager,
                         EntityManager entityManager, MovementManager movementManager, IOManager ioManager) {
        this.batch = batch;
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.ioManager = ioManager;

        font = new BitmapFont(Gdx.files.internal("game_font.fnt"));
        font.setColor(Color.WHITE);
        font.getData().setScale(0.3f);
    }

    @Override
    public void initialize() {
        System.out.println("[SnakeSettingScene] Initializing...");

        try {
        	soundSliderTexture = new Texture(Gdx.files.internal("sound_slider.png"));
            soundBarTexture = new Texture(Gdx.files.internal("sound_bar.png"));
            snakeTexture = new Texture(Gdx.files.internal("snake_head.png")); // Default color
            skin = new Skin(Gdx.files.internal("uiskin.json"));

            //Settings
            viewport = new ScreenViewport();  // Ensures correct resizing
            stage = new Stage(viewport);
            Gdx.input.setInputProcessor(stage); // Ensure input handling

            //Slider
            SliderStyle sliderStyle = new SliderStyle();
            sliderStyle.background = new TextureRegionDrawable(new TextureRegion(soundBarTexture));
            sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(soundSliderTexture));

            //Music Slider
            Slider musicSlider = new Slider(0, 100, 1, false, sliderStyle);
            musicSlider.setValue(100); // Default volume level
            musicSlider.setSize(200, 20);

            //Music Label to display slider value
            Label musicLabel = new Label("Music Volume: " + (int) musicSlider.getValue(), skin);
            musicSlider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    musicLabel.setText("Music Volume: " + (int) musicSlider.getValue());
                    float v = musicSlider.getValue()/100;
                    ioManager.getAudio().setMusicVolume(v);
                }
            });

            //Sound Slider
            Slider soundSlider = new Slider(0, 100, 1, false, sliderStyle);
            soundSlider.setValue(100); // Default volume level
            soundSlider.setSize(200, 20);

            //Sound Label to display slider value
            Label soundLabel = new Label("Sound Volume: " + (int) soundSlider.getValue(), skin);
            soundSlider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    soundLabel.setText("Sound Volume: " + (int) soundSlider.getValue());
                    float v = soundSlider.getValue()/100;
                    ioManager.getAudio().setSoundVolume(v);
                }
            });



            // Snake Color Buttons
            TextButton redButton = new TextButton("Red Snake", skin);
            TextButton blueButton = new TextButton("Blue Snake", skin);
            TextButton greenButton = new TextButton("Green Snake", skin);

//            // Button click listeners
//            redButton.addListener(new ChangeListener() {
//                @Override
//                public void changed(ChangeEvent event, Actor actor) {
//                    snakeTexture.dispose();
//                    snakeTexture = new Texture(Gdx.files.internal("snake_red.png"));
//                }
//            });
//
//            blueButton.addListener(new ChangeListener() {
//                @Override
//                public void changed(ChangeEvent event, Actor actor) {
//                    snakeTexture.dispose();
//                    snakeTexture = new Texture(Gdx.files.internal("snake_blue.png"));
//                }
//            });
//
//            greenButton.addListener(new ChangeListener() {
//                @Override
//                public void changed(ChangeEvent event, Actor actor) {
//                    snakeTexture.dispose();
//                    snakeTexture = new Texture(Gdx.files.internal("snake_green.png"));
//                }
//            });

            // Layout using Table
            Table table = new Table();
            table.setFillParent(true);
            table.center();

            table.add(musicLabel).padBottom(10).row();
            table.add(musicSlider).width(300).padBottom(40).expandX().center().row();
            table.add(soundLabel).padBottom(10).row();
            table.add(soundSlider).width(300).padBottom(40).expandX().center().row();

            Table buttonTable = new Table();
            buttonTable.add(redButton).pad(10);
            buttonTable.add(blueButton).pad(10);
            buttonTable.add(greenButton).pad(10);

            table.add(buttonTable).colspan(3).center().row();

            stage.addActor(table);

            // Add UI to stage
            stage.addActor(table);

             System.out.println("[SnakeSettingScene] Setting menu loaded.");
        } catch (Exception e) {
            System.err.println("[SnakeSettingScene] Error loading background: " + e.getMessage());
            // Create a default semi-transparent black texture
        }

        // Play pause sound
        ioManager.getAudio().playSound("pause.mp3");
    }

    @Override
    public void update(float deltaTime) {

        stage.act(deltaTime);

        //Resume Game (Settings not saved)
        if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.ESCAPE)) {
            resumeGame();
            return;
        }

        // Resume Game (Settings saved)
        if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.ENTER)) {
        	resumeGame();
            return;
        }
    }


    private void resumeGame() {
        System.out.println("[SnakeSettingScene] Resuming game...");
        if (sceneManager != null) {
            sceneManager.popScene();
            sceneManager.setGameState(GameState.RUNNING);
        } else {
            System.err.println("[SnakeSettingScene] sceneManager is NULL! Cannot resume.");
        }
    }

    @Override
    public void render(SpriteBatch batch) {
    	batch.begin();

    	stage.draw();

    	// Ensure viewport updates on resize
    	viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

    	// Draw pause title
    	GlyphLayout settingTitle = new GlyphLayout();
    	settingTitle.setText(font, "SETTINGS");

        font.getData().setScale(0.3f);
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "SETTINGS",
        		(viewport.getWorldWidth() - settingTitle.width) / 2,
                Gdx.graphics.getHeight() - 100);

        // Draw "Press enter to save and return to main menu" text
    	GlyphLayout layout = new GlyphLayout();
    	layout.setText(font, "Press enter to save and return to main menu");

    	// Correctly position text even after resizing
    	font.draw(batch, "Press enter to save and return to main menu",
    	          (viewport.getWorldWidth() - layout.width) / 2,  // Always centers text
    	          80);

        batch.end();

    }

    @Override
    public void dispose() {
    	// Dispose UI-related textures
        if (soundSliderTexture != null) {
        	soundSliderTexture.dispose();
        }
        if (soundBarTexture != null) {
        	soundBarTexture.dispose();
        }

     // Dispose the UI stage
        if (stage != null) stage.dispose();

        if (font != null) {
            font.dispose();
        }
        System.out.println("[SnakeSettingScene] Resources disposed.");
    }
}
