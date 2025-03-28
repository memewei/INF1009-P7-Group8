package io.github.some_example_name.lwjgl3.application_classes.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.GameState;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;
import io.github.some_example_name.lwjgl3.abstract_engine.ui.AssetPaths;
import io.github.some_example_name.lwjgl3.application_classes.entity.FoodEntityFactory;
import io.github.some_example_name.lwjgl3.application_classes.game.SnakeColor;
import io.github.some_example_name.lwjgl3.abstract_engine.config.GameConfig;
import io.github.some_example_name.lwjgl3.abstract_engine.control.ControlMode;

public class HealthSnakeMenuScene extends Scene {
	private Texture backgroundTexture;
	private Texture titleTexture;
	private SpriteBatch batch;
	private SceneManager sceneManager;
	private EntityManager entityManager;
	private MovementManager movementManager;
	private IOManager ioManager;
	private BitmapFont font;

	// Background animation elements
	private Array<MenuSnake> backgroundSnakes;
	private Array<MenuFood> backgroundFood;
	private float spawnTimer = 0f;
	private float spawnInterval = 3f; // Spawn a new snake every 3 seconds
	private int maxBackgroundSnakes = 8;

	private String[] menuItems = { "Start Game", "How to Play", "Settings", "Exit" };

	private int selectedItem = 0;
	private float timeElapsed;
	private boolean showingInstructions = false;
	private boolean showingSettings = false;
	private Texture instructionsTexture;
	private Texture soundSlider;
	private Texture soundBar;

	private Viewport viewport;
	private Stage stage;

	private Skin skin;

	public HealthSnakeMenuScene(SpriteBatch batch, SceneManager sceneManager, EntityManager entityManager,
			MovementManager movementManager, IOManager ioManager) {
		this.batch = batch;
		this.sceneManager = sceneManager;
		this.entityManager = entityManager;
		this.movementManager = movementManager;
		this.ioManager = ioManager;
		this.timeElapsed = 0;

		font = new BitmapFont(Gdx.files.internal(AssetPaths.GAME_FONT));
		font.setColor(Color.WHITE);
		font.getData().setScale(0.3f);

		// Initialize background animation arrays
		backgroundSnakes = new Array<MenuSnake>();
		backgroundFood = new Array<MenuFood>();
	}

	@Override
	public void initialize() {
		try {
			// Load all required textures
			backgroundTexture = new Texture(Gdx.files.internal(AssetPaths.BACKGROUND));
			titleTexture = new Texture(Gdx.files.internal(AssetPaths.TITLE));
			instructionsTexture = new Texture(Gdx.files.internal(AssetPaths.INSTRUCTIONS));
			soundSlider = new Texture(Gdx.files.internal(AssetPaths.SOUND_SLIDER));
			soundBar = new Texture(Gdx.files.internal(AssetPaths.SOUND_BAR));
			skin = new Skin(Gdx.files.internal(AssetPaths.UI_SKIN));
			font = new BitmapFont(Gdx.files.internal(AssetPaths.GAME_FONT));

			// Stop any currently playing music first
			ioManager.getAudio().stopMusic();

			// Then start menu music
			ioManager.getAudio().playMusic(AssetPaths.MENU_MUSIC);

			// Initial input processor is always the dynamic input
			Gdx.input.setInputProcessor(ioManager.getDynamicInput());

			System.out.println("[HealthSnakeMenuScene] Textures loaded successfully.");
		} catch (Exception e) {
			System.err.println("[HealthSnakeMenuScene] Error loading textures: " + e.getMessage());
			// Fallback textures or placeholder handling
		}

		// Create initial background snakes and food
		createInitialBackgroundElements();
	}

	private void createInitialBackgroundElements() {
		// Clear any existing background elements
		backgroundSnakes.clear();
		backgroundFood.clear();

		// Create several snakes for initial background
		for (int i = 0; i < maxBackgroundSnakes / 2; i++) {
			addRandomBackgroundSnake();
		}

		// Add some food items
		for (int i = 0; i < 10; i++) {
			addRandomBackgroundFood();
		}
	}

	private void addRandomBackgroundSnake() {
		if (backgroundSnakes.size >= maxBackgroundSnakes)
			return;

		// Create a snake starting off-screen
		float startX, startY;
		float direction;

		// Decide which edge to start from
		int edge = MathUtils.random(3); // 0=left, 1=right, 2=top, 3=bottom

		switch (edge) {
		case 0: // Left edge
			startX = -50;
			startY = MathUtils.random(0, Gdx.graphics.getHeight());
			direction = MathUtils.random(-MathUtils.PI / 4, MathUtils.PI / 4); // Moving rightward
			break;
		case 1: // Right edge
			startX = Gdx.graphics.getWidth() + 50;
			startY = MathUtils.random(0, Gdx.graphics.getHeight());
			direction = MathUtils.random(3 * MathUtils.PI / 4, 5 * MathUtils.PI / 4); // Moving leftward
			break;
		case 2: // Top edge
			startX = MathUtils.random(0, Gdx.graphics.getWidth());
			startY = Gdx.graphics.getHeight() + 50;
			direction = MathUtils.random(5 * MathUtils.PI / 4, 7 * MathUtils.PI / 4); // Moving downward
			break;
		default: // Bottom edge
			startX = MathUtils.random(0, Gdx.graphics.getWidth());
			startY = -50;
			direction = MathUtils.random(MathUtils.PI / 4, 3 * MathUtils.PI / 4); // Moving upward
			break;
		}

		// Randomly choose snake type (player or enemy)
		boolean isPlayerType = MathUtils.randomBoolean(0.3f); // 30% chance for player-like snake

		Texture headTexture = new Texture(
				Gdx.files.internal(isPlayerType ? AssetPaths.SNAKE_HEAD : AssetPaths.ENEMY_HEAD));
		Texture bodyTexture = new Texture(
				Gdx.files.internal(isPlayerType ? AssetPaths.SNAKE_BODY : AssetPaths.ENEMY_BODY));

		// Random length between 5-15 segments
		int length = MathUtils.random(5, 15);

		// Random speed between 40-100
		float speed = MathUtils.random(40f, 100f);

		MenuSnake snake = new MenuSnake(startX, startY, direction, speed, headTexture, bodyTexture, length);
		backgroundSnakes.add(snake);
	}

	private void addRandomBackgroundFood() {
		float x = MathUtils.random(50, Gdx.graphics.getWidth() - 50);
		float y = MathUtils.random(50, Gdx.graphics.getHeight() - 50);

		boolean isHealthy = MathUtils.randomBoolean(0.7f); // 70% chance for healthy food

		String texturePath = FoodEntityFactory.getRandomTexturePath(isHealthy);
		Texture foodTexture = new Texture(texturePath);
		float size = isHealthy ? 20f : 30f;

		MenuFood food = new MenuFood(x, y, foodTexture, size);
		backgroundFood.add(food);
	}

	@Override
	public void update(float deltaTime) {
		timeElapsed += deltaTime;

		// Update background animation
		updateBackgroundAnimation(deltaTime);

		if (showingInstructions) {
			// If instructions are showing, pressing any key returns to menu
			if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.ANY_KEY)) {
				showingInstructions = false;
				Gdx.input.setInputProcessor(ioManager.getDynamicInput());
			}
			return;
		}

		if (showingSettings) {
			// Update UI when showing settings
			stage.act(deltaTime);

			// If settings are showing, pressing ESCAPE returns to menu
			if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.ESCAPE)) {
				showingSettings = false;
				Gdx.input.setInputProcessor(ioManager.getDynamicInput());
			}
			return;
		}

		// Menu navigation
		if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.UP)) {
			selectedItem = (selectedItem - 1 + menuItems.length) % menuItems.length;
			ioManager.getAudio().playSound(AssetPaths.MENU_MOVE);
		} else if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.DOWN)) {
			selectedItem = (selectedItem + 1) % menuItems.length;
			ioManager.getAudio().playSound(AssetPaths.MENU_MOVE);
		}

		// Menu selection
		if (ioManager.getDynamicInput().isKeyJustPressed(Input.Keys.ENTER)) {
			handleMenuSelection();
		}
	}

	private void updateBackgroundAnimation(float deltaTime) {
		// Spawn timer for new snakes
		spawnTimer += deltaTime;
		if (spawnTimer > spawnInterval && backgroundSnakes.size < maxBackgroundSnakes) {
			addRandomBackgroundSnake();
			spawnTimer = 0;
		}

		// Update all background snakes
		Array<MenuSnake> snakesToRemove = new Array<MenuSnake>();

		for (MenuSnake snake : backgroundSnakes) {
			snake.update(deltaTime);

			// Check if snake is off-screen and should be removed
			if (snake.isOffScreen(100)) {
				snakesToRemove.add(snake);
			}

			// Check for collision with food
			for (MenuFood food : backgroundFood) {
				if (food.isActive && Vector2.dst(snake.x, snake.y, food.x, food.y) < (snake.bodySize + food.size) / 2) {
					// Snake eats food
					snake.addSegment();
					food.isActive = false;
				}
			}
		}

		// Remove off-screen snakes
		for (MenuSnake snake : snakesToRemove) {
			snake.dispose();
			backgroundSnakes.removeValue(snake, true);
		}

		// Remove eaten food and add new food to maintain a good amount
		Array<MenuFood> foodToRemove = new Array<MenuFood>();
		for (MenuFood food : backgroundFood) {
			if (!food.isActive) {
				foodToRemove.add(food);
			}
		}

		// Remove inactive food
		for (MenuFood food : foodToRemove) {
			food.dispose();
			backgroundFood.removeValue(food, true);
			// Add a new food item to replace the eaten one
			addRandomBackgroundFood();
		}
	}

	private void handleMenuSelection() {
		ioManager.getAudio().playSound(AssetPaths.MENU_SELECT);

		switch (selectedItem) {
		case 0: // Start Game
			System.out.println("[HealthSnakeMenuScene] Starting game...");
			// Stop menu music before transitioning to game
			ioManager.getAudio().stopMusic();

			// Dispose background animation resources
			disposeBackgroundElements();

			sceneManager.changeScene(new HealthSnakeGameScene(batch, entityManager, movementManager,
					sceneManager.getWorld(), sceneManager, ioManager, true), GameState.RUNNING);
			break;

		case 1: // How to Play
			showingInstructions = true;
			break;

		case 2: // Settings
			System.out.println("[HealthSnakeMenuScene] Opening settings...");
			showingSettings = true;

			if (stage == null) {
				createSettingsUI();
			} else {
				stage.clear();
				// Re-create the settings UI
				createSettingsUI();
			}

			// Set input processor to stage for UI interaction
			Gdx.input.setInputProcessor(stage);
			break;

		case 3: // Exit
			System.out.println("[HealthSnakeMenuScene] Exiting game...");
			// Dispose resources before exiting
			disposeBackgroundElements();
			Gdx.app.exit();
			break;

		}
	}

	// Helper method to create the settings UI
	private void createSettingsUI() {
		// Initialize viewport and stage for UI
		if (stage == null) {
			// Ensures correct resizing
			viewport = new ScreenViewport();
			stage = new Stage(viewport);
		}

		// Set up slider style
		SliderStyle sliderStyle = new SliderStyle();
		sliderStyle.background = new TextureRegionDrawable(new TextureRegion(soundBar));
		sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(soundSlider));

		// Music Slider
		Slider musicSlider = new Slider(0, 100, 1, false, sliderStyle);
		musicSlider.setValue(ioManager.getAudio().getMusicVolume()); // get volume from ioManager
		musicSlider.setSize(200, 20);

		// Music Label to display slider value
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
		soundSlider.setValue(ioManager.getAudio().getSoundVolume()); // get volume from ioManager
		soundSlider.setSize(200, 20);

		// Sound Label to display slider value
		Label soundLabel = new Label("Sound Volume: " + (int) soundSlider.getValue(), skin);
		soundSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				soundLabel.setText("Sound Volume: " + (int) soundSlider.getValue());
				float v = soundSlider.getValue() / 100;
				ioManager.getAudio().setSoundVolume(v);
			}
		});

		// Control Mode Toggle Button
		final TextButton controlToggle = new TextButton("Control: " + ioManager.getControlMode(), skin);
		controlToggle.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ControlMode current = ioManager.getControlMode();
				ControlMode next = (current == ControlMode.KEYBOARD) ? ControlMode.MOUSE : ControlMode.KEYBOARD;
				ioManager.setControlMode(next);
				controlToggle.setText("Control: " + next);
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

				GameConfig.getInstance().setSnakeColor(SnakeColor.BROWN); // Apply brown color
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

				GameConfig.getInstance().setSnakeColor(SnakeColor.BLUE); // Apply blue color
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

				GameConfig.getInstance().setSnakeColor(SnakeColor.GREEN); // Apply green color
				System.out.println("Snake color selected: GREEN");
			}
		});

		// Create the settings UI table
		Table settingsTable = new Table();
		settingsTable.setFillParent(true);
		settingsTable.center();

		// Title
		// Label settingsTitle = new Label("SETTINGS", skin);
		// settingsTitle.setFontScale(1.5f);
		// settingsTable.add(settingsTitle).colspan(2).padBottom(40).row();

		// Audio settings
		settingsTable.add(musicLabel).padBottom(10).row();
		settingsTable.add(musicSlider).width(300).padBottom(40).expandX().center().row();
		settingsTable.add(soundLabel).padBottom(10).row();
		settingsTable.add(soundSlider).width(300).padBottom(40).expandX().center().row();

		// Control mode
		settingsTable.add(controlToggle).padBottom(30).center().row();

		// Snake color selection
		settingsTable.add(new Label("Snake Color:", skin)).padBottom(10).row();

		// Color button table
		Table buttonTable = new Table();
		buttonTable.add(brownButton).pad(10).width(150).height(50);
		buttonTable.add(blueButton).pad(10).width(150).height(50);
		buttonTable.add(greenButton).pad(10).width(150).height(50);

		settingsTable.add(buttonTable).colspan(3).center().row();

		// Back button
		TextButton backButton = new TextButton("Back to Main Menu", skin);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showingSettings = false;
				Gdx.input.setInputProcessor(ioManager.getDynamicInput()); // Reset input processor
			}
		});
		settingsTable.add(backButton).padTop(30).width(200).height(60).row();

		// Add the settings table to the stage
		stage.clear();
		stage.addActor(settingsTable);
	}

	@Override
	public void render(SpriteBatch batch) {
		if (showingSettings) {
			// Update and render UI stage
			stage.act(Gdx.graphics.getDeltaTime());
			stage.draw();

			// Ensure viewport resizes properly
			viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

			// Draw overlay text
			batch.begin();
			drawSettingsOverlay(batch);
			batch.end();
			return;
		}
		batch.begin();

		// Draw background
		if (backgroundTexture != null) {
			batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}

		// Draw animated background elements
		if (!showingInstructions && !showingSettings) {
			// Draw background food
			for (MenuFood food : backgroundFood) {
				if (food.isActive) {
					batch.draw(food.texture, food.x - food.size / 2, food.y - food.size / 2, food.size, food.size);
				}
			}

			// Draw background snakes
			for (MenuSnake snake : backgroundSnakes) {
				// Draw body segments
				for (Vector2 segment : snake.bodySegments) {
					batch.draw(snake.bodyTexture, segment.x - snake.bodySize / 2, segment.y - snake.bodySize / 2,
							snake.bodySize, snake.bodySize);
				}

				// Draw head
				batch.draw(snake.headTexture, snake.x - snake.bodySize / 2, snake.y - snake.bodySize / 2,
						snake.bodySize / 2, // origin x
						snake.bodySize / 2, // origin y
						snake.bodySize, snake.bodySize, 1, 1, // scale x, y
						snake.direction * MathUtils.radiansToDegrees, // rotation
						0, 0, // srcX, srcY
						snake.headTexture.getWidth(), snake.headTexture.getHeight(), false, false); // flip x, y
			}
		}

		if (showingInstructions) {
			// Show instructions screen
			if (instructionsTexture != null) {
				batch.draw(instructionsTexture, (Gdx.graphics.getWidth() - instructionsTexture.getWidth()) / 2,
						(Gdx.graphics.getHeight() - instructionsTexture.getHeight()) / 2);
			}

			// Draw "Press any key to return" text
			font.draw(batch, "Press any key to return", Gdx.graphics.getWidth() / 2 - 150, 80);
		}else {
			// Draw title
			if (titleTexture != null) {
				batch.draw(titleTexture, (Gdx.graphics.getWidth() - titleTexture.getWidth()) / 2,
						Gdx.graphics.getHeight() - titleTexture.getHeight() - 20);
			}

			// Draw menu items
			float menuY = Gdx.graphics.getHeight() / 2 + 50;
			float menuSpacing = 60;

			for (int i = 0; i < menuItems.length; i++) {
				// Highlight selected item
				if (i == selectedItem) {
					// Pulsing effect for selected item
					float pulse = (float) Math.sin(timeElapsed * 5) * 0.2f + 0.8f;
					font.setColor(1f, pulse, pulse, 1f);
					font.draw(batch, "> " + menuItems[i] + " <", Gdx.graphics.getWidth() / 2 - 150,
							menuY - i * menuSpacing);
					font.setColor(Color.WHITE); // Reset color
				} else {
					font.draw(batch, menuItems[i], Gdx.graphics.getWidth() / 2 - 100, menuY - i * menuSpacing);
				}
			}

			// Draw controls hint
			font.getData().setScale(0.3f);
			font.draw(batch, "Arrow Keys: Navigate | Enter: Select", Gdx.graphics.getWidth() / 2 - 225, 50);
		}

		batch.end();
	}

	private void applySnakeColorStyle(TextButton greenButton, TextButton brownButton, TextButton blueButton,
			TextButton.TextButtonStyle selectedStyle, TextButton.TextButtonStyle defaultStyle) {
		switch (GameConfig.getInstance().getSnakeColor()) {
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

		font.draw(batch, "Press Esc key to return to main menu", (viewport.getWorldWidth() - layout.width) / 2, 80);
	}

	private void disposeBackgroundElements() {
		// Dispose all snake textures
		for (MenuSnake snake : backgroundSnakes) {
			snake.dispose();
		}
		backgroundSnakes.clear();

		// Dispose all food textures
		for (MenuFood food : backgroundFood) {
			food.dispose();
		}
		backgroundFood.clear();
	}

	@Override
	public void dispose() {
		if (backgroundTexture != null) {
			backgroundTexture.dispose();
		}
		if (titleTexture != null) {
			titleTexture.dispose();
		}
		if (instructionsTexture != null) {
			instructionsTexture.dispose();
		}
		if (font != null) {
			font.dispose();
		}
		// Dispose UI-related textures
		if (soundSlider != null) {
			soundSlider.dispose();
		}
		if (soundBar != null) {
			soundBar.dispose();
		}

		// Dispose the UI stage
		if (stage != null)
			stage.dispose();

		// Dispose background animation resources
		disposeBackgroundElements();
	}

	// Inner class to represent a background menu snake
	private class MenuSnake {
		float x, y;
		float direction;
		float speed;
		float bodySize = 20f;
		float segmentSpacing = 15f;
		Texture headTexture;
		Texture bodyTexture;
		Array<Vector2> bodySegments;
		float directionChangeTimer = 0f;
		float directionChangeInterval;

		public MenuSnake(float x, float y, float direction, float speed, Texture headTexture, Texture bodyTexture,
				int length) {
			this.x = x;
			this.y = y;
			this.direction = direction;
			this.speed = speed;
			this.headTexture = headTexture;
			this.bodyTexture = bodyTexture;
			this.bodySegments = new Array<Vector2>(length);
			this.directionChangeInterval = MathUtils.random(1.5f, 4.0f); // Random interval for direction changes

			// Add initial body segments
			for (int i = 0; i < length; i++) {
				float xPos = x - (i + 1) * segmentSpacing * MathUtils.cos(direction);
				float yPos = y - (i + 1) * segmentSpacing * MathUtils.sin(direction);
				bodySegments.add(new Vector2(xPos, yPos));
			}
		}

		public void update(float deltaTime) {
			// Random direction changes
			directionChangeTimer += deltaTime;
			if (directionChangeTimer >= directionChangeInterval) {
				// Change direction by a random amount
				direction += MathUtils.random(-0.5f, 0.5f);
				directionChangeTimer = 0;
				// Set a new random interval for the next change
				directionChangeInterval = MathUtils.random(1.5f, 4.0f);
			}

			// Store previous head position
			Vector2 prevHeadPos = new Vector2(x, y);

			// Update head position
			float moveX = MathUtils.cos(direction) * speed * deltaTime;
			float moveY = MathUtils.sin(direction) * speed * deltaTime;
			x += moveX;
			y += moveY;

			// Update body segments
			Vector2 prevPos = prevHeadPos;
			for (Vector2 segment : bodySegments) {
				Vector2 currentPos = new Vector2(segment);

				// Calculate direction to previous segment
				Vector2 dir = new Vector2(prevPos).sub(currentPos);
				float dist = dir.len();

				// Only move if distance exceeds segment spacing
				if (dist > segmentSpacing) {
					dir.nor();
					segment.x += dir.x * (dist - segmentSpacing);
					segment.y += dir.y * (dist - segmentSpacing);
				}

				prevPos = new Vector2(segment);
			}
		}

		public boolean isOffScreen(float buffer) {
			return x < -buffer || x > Gdx.graphics.getWidth() + buffer || y < -buffer
					|| y > Gdx.graphics.getHeight() + buffer;
		}

		public void addSegment() {
			// Add a new segment at the end of the snake
			if (bodySegments.size > 0) {
				Vector2 lastSegment = bodySegments.get(bodySegments.size - 1);
				bodySegments.add(new Vector2(lastSegment));
			} else {
				// If no segments exist yet, position behind the head
				float oppositeDirection = direction + MathUtils.PI;
				float newX = x + MathUtils.cos(oppositeDirection) * segmentSpacing;
				float newY = y + MathUtils.sin(oppositeDirection) * segmentSpacing;
				bodySegments.add(new Vector2(newX, newY));
			}
		}

		public void dispose() {
			if (headTexture != null) {
				headTexture.dispose();
			}
			if (bodyTexture != null) {
				bodyTexture.dispose();
			}

		}
	}

	// Inner class to represent background food items
	private class MenuFood {
		float x, y;
		Texture texture;
		float size;
		boolean isActive = true;

		public MenuFood(float x, float y, Texture texture, float size) {
			this.x = x;
			this.y = y;
			this.texture = texture;
			this.size = size;
		}

		public void dispose() {
			if (texture != null) {
				texture.dispose();
			}
		}
	}
}
