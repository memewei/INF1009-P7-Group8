package io.github.some_example_name.lwjgl3.application_classes.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
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
import io.github.some_example_name.lwjgl3.abstract_engine.ui.AssetPaths;
import io.github.some_example_name.lwjgl3.application_classes.entity.SnakePlayer;
import io.github.some_example_name.lwjgl3.application_classes.game.SnakeColor;
import io.github.some_example_name.lwjgl3.abstract_engine.config.GameConfig;
import io.github.some_example_name.lwjgl3.abstract_engine.control.ControlMode;

public class SnakeSettingScene extends Scene {
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
	private SnakeColor snakeColor;

	public SnakeSettingScene(SpriteBatch batch, SceneManager sceneManager, EntityManager entityManager,
			MovementManager movementManager, IOManager ioManager) {
		this.batch = batch;
		this.sceneManager = sceneManager;
		this.entityManager = entityManager;
		this.movementManager = movementManager;
		this.ioManager = ioManager;

		font = new BitmapFont(Gdx.files.internal(AssetPaths.GAME_FONT));
		font.setColor(Color.WHITE);
		font.getData().setScale(0.3f);
	}

	@Override
	public void initialize() {
		System.out.println("[SnakeSettingScene] Initializing...");

		try {
			soundSliderTexture = new Texture(Gdx.files.internal(AssetPaths.SOUND_SLIDER));
			soundBarTexture = new Texture(Gdx.files.internal(AssetPaths.SOUND_BAR));
			skin = new Skin(Gdx.files.internal(AssetPaths.UI_SKIN));
			
			//Retrieve the snake color from GameConfig
			snakeColor = GameConfig.getInstance().getSnakeColor();

			// Settings
			viewport = new ScreenViewport();
			stage = new Stage(viewport);
			Gdx.input.setInputProcessor(stage);

			// Slider style
			SliderStyle sliderStyle = new SliderStyle();
			sliderStyle.background = new TextureRegionDrawable(new TextureRegion(soundBarTexture));
			sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(soundSliderTexture));

			// Music Slider
			Slider musicSlider = new Slider(0, 100, 1, false, sliderStyle);
			musicSlider.setValue(ioManager.getAudio().getMusicVolume());
			musicSlider.setSize(200, 20);

			// Music Label
			Label musicLabel = new Label("Music Volume: " + (int) musicSlider.getValue(), skin);
			musicSlider.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					musicLabel.setText("Music Volume: " + (int) musicSlider.getValue());
					float v = musicSlider.getValue() / 100;
					ioManager.getAudio().setMusicVolume(v);
				}
			});

			// Sound Slider
			Slider soundSlider = new Slider(0, 100, 1, false, sliderStyle);
			soundSlider.setValue(ioManager.getAudio().getSoundVolume());
			soundSlider.setSize(200, 20);

			// Sound Label
			Label soundLabel = new Label("Sound Volume: " + (int) soundSlider.getValue(), skin);
			soundSlider.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					soundLabel.setText("Sound Volume: " + (int) soundSlider.getValue());
					float v = soundSlider.getValue() / 100;
					ioManager.getAudio().setSoundVolume(v);
				}
			});

			// Create styles for selected and unselected buttons using color differences
			TextButton.TextButtonStyle defaultStyle = skin.get(TextButton.TextButtonStyle.class);

			// Create a selected style with yellow text for highlighting
			TextButton.TextButtonStyle selectedStyle = new TextButton.TextButtonStyle(defaultStyle);
			selectedStyle.fontColor = Color.YELLOW;

			// Snake Color Buttons
			final TextButton greenButton = new TextButton("Green Snake", skin);
			final TextButton brownButton = new TextButton("Brown Snake", skin);
			final TextButton blueButton = new TextButton("Blue Snake", skin);

			// Retrieve the previous scene
			Scene previousScene = sceneManager.getSceneBelow();

			// Set the initial selected button
			applySnakeColorStyle(greenButton, brownButton, blueButton, selectedStyle, defaultStyle);

			// Button click listeners with visual selection
			brownButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// Update selection style
					brownButton.setStyle(selectedStyle);
					blueButton.setStyle(defaultStyle);
					greenButton.setStyle(defaultStyle);

					if (previousScene instanceof HealthSnakeGameScene) {
						HealthSnakeGameScene gameScene = (HealthSnakeGameScene) previousScene;
						SnakePlayer gamePlayer = gameScene.getPlayer(); // Retrieves the player
						GameConfig.getInstance().setSnakeColor(SnakeColor.BROWN);
						gamePlayer.setSnakeColor(SnakeColor.BROWN); // Apply brown color
					}

					System.out.println("Snake color selected: BROWN");
				}
			});

			blueButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// Update selection style
					blueButton.setStyle(selectedStyle);
					brownButton.setStyle(defaultStyle);
					greenButton.setStyle(defaultStyle);

					if (previousScene instanceof HealthSnakeGameScene) {
					    HealthSnakeGameScene gameScene = (HealthSnakeGameScene) previousScene;
					    SnakePlayer gamePlayer = gameScene.getPlayer(); // Retrieves the player
					    GameConfig.getInstance().setSnakeColor(SnakeColor.BLUE);
					    gamePlayer.setSnakeColor(SnakeColor.BLUE); // Apply blue color
					}

					System.out.println("Snake color selected: BLUE");
				}
			});

			greenButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// Update selection style
					greenButton.setStyle(selectedStyle);
					brownButton.setStyle(defaultStyle);
					blueButton.setStyle(defaultStyle);

					if (previousScene instanceof HealthSnakeGameScene) {
						HealthSnakeGameScene gameScene = (HealthSnakeGameScene) previousScene;
						SnakePlayer gamePlayer = gameScene.getPlayer(); // Retrieves the player
						GameConfig.getInstance().setSnakeColor(SnakeColor.GREEN);
						gamePlayer.setSnakeColor(SnakeColor.GREEN); // Apply green color
					}
					System.out.println("Snake color selected: GREEN");
				}
			});

			// Control Mode Toggle Button
			TextButton controlModeButton = new TextButton("Control: " + ioManager.getControlMode(), skin);
			controlModeButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					ControlMode current = ioManager.getControlMode();
					ControlMode next = (current == ControlMode.KEYBOARD) ? ControlMode.MOUSE : ControlMode.KEYBOARD;
					ioManager.setControlMode(next);
					controlModeButton.setText("Control: " + next);
				}
			});

			// Layout using Table
			Table table = new Table();
			table.setFillParent(true);
			table.center();

			// Audio settings
			table.add(musicLabel).padBottom(10).row();
			table.add(musicSlider).width(300).padBottom(40).expandX().center().row();
			table.add(soundLabel).padBottom(10).row();
			table.add(soundSlider).width(300).padBottom(40).expandX().center().row();

			// Control mode
			table.add(controlModeButton).padBottom(30).center().row();

			// Snake color selection
			table.add(new Label("Snake Color:", skin)).padBottom(10).row();

			// Color button table
			Table buttonTable = new Table();
			buttonTable.add(brownButton).pad(10).width(150).height(50);
			buttonTable.add(blueButton).pad(10).width(150).height(50);
			buttonTable.add(greenButton).pad(10).width(150).height(50);

			table.add(buttonTable).colspan(3).center().row();

			// Back button
			TextButton backButton = new TextButton("Back", skin);
			backButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					resumeGame();
				}
			});
			table.add(backButton).padTop(30).width(200).height(60).row();

			stage.addActor(table);

			System.out.println("[SnakeSettingScene] Setting menu loaded.");
		} catch (Exception e) {
			System.err.println("[SnakeSettingScene] Error loading background: " + e.getMessage());
		}

		// Play settings sound
		ioManager.getAudio().playSound(AssetPaths.PAUSE_SOUND);
	}

	private void applySnakeColorStyle(TextButton greenButton, TextButton brownButton, TextButton blueButton,
	        TextButton.TextButtonStyle selectedStyle, TextButton.TextButtonStyle defaultStyle) {
	    switch (snakeColor) {
	        case GREEN:
	            greenButton.setStyle(selectedStyle);
	            brownButton.setStyle(defaultStyle);
	            blueButton.setStyle(defaultStyle);
	            break;

	        case BROWN:
	            brownButton.setStyle(selectedStyle);
	            greenButton.setStyle(defaultStyle);
	            blueButton.setStyle(defaultStyle);
	            break;

	        case BLUE:
	            blueButton.setStyle(selectedStyle);
	            greenButton.setStyle(defaultStyle);
	            brownButton.setStyle(defaultStyle);
	            break;

	        default:
	            // Optional: handle unexpected value
	            break;
	    }
	}
	
	private void drawSettingsOverlay(SpriteBatch batch) {
	    GlyphLayout settingTitle = new GlyphLayout();
	    settingTitle.setText(font, "SETTINGS");

	    font.getData().setScale(0.3f);
	    font.setColor(Color.WHITE);
	    font.draw(batch, "SETTINGS", (viewport.getWorldWidth() - settingTitle.width) / 2,
	              Gdx.graphics.getHeight() - 100);

	    GlyphLayout layout = new GlyphLayout();
	    layout.setText(font, "Press Esc key to return to main menu");

	    font.draw(batch, "Press Esc key to return to main menu",
	              (viewport.getWorldWidth() - layout.width) / 2, 80);
	}


	@Override
	public void update(float deltaTime) {

		stage.act(deltaTime);

		// Resume Game(ESCAPE KEY)
		if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.ESCAPE)) {
			resumeGame();
			return;
		}
	}
	
	//Resume Game
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

		drawSettingsOverlay(batch);

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
		if (stage != null)
			stage.dispose();

		if (font != null) {
			font.dispose();
		}
		System.out.println("[SnakeSettingScene] Resources disposed.");
	}
}
