package io.github.some_example_name.lwjgl3.abstract_engine.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * UI Factory class that creates UI elements to avoid duplication
 * and follow the Factory pattern for better OOP design.
 */
public class UIFactory {
    // Singleton instance of the factory
    private static UIFactory instance;
    
    private Skin skin;
    private Texture sliderKnobTexture;
    private Texture sliderBarTexture;
    
    // Private constructor for singleton pattern
    private UIFactory() {
        initializeResources();
    }
    
    /**
     * Gets the singleton instance of the UIFactory
     */
    public static UIFactory getInstance() {
        if (instance == null) {
            instance = new UIFactory();
        }
        return instance;
    }
    
    /**
     * Initialize UI resources
     */
    private void initializeResources() {
        try {
            skin = new Skin(Gdx.files.internal(AssetPaths.UI_SKIN));
            sliderKnobTexture = new Texture(Gdx.files.internal(AssetPaths.SOUND_SLIDER));
            sliderBarTexture = new Texture(Gdx.files.internal(AssetPaths.SOUND_BAR));
        } catch (Exception e) {
            System.err.println("[UIFactory] Error loading UI resources: " + e.getMessage());
        }
    }
    
    /**
     * Create a slider with label for volume control
     */
    public Table createVolumeSlider(String labelText, float initialValue, final VolumeChangeListener listener) {
        Table sliderTable = new Table();
        
        // Set up slider style
        SliderStyle sliderStyle = new SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(new TextureRegion(sliderBarTexture));
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(sliderKnobTexture));
        
        // Create slider
        Slider slider = new Slider(0, 100, 1, false, sliderStyle);
        slider.setValue(initialValue);
        slider.setSize(200, 20);
        
        // Create and style label
        final Label volumeLabel = new Label(labelText + ": " + (int)initialValue, skin);
        
        // Add listener to update label text and call volume callback
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider)actor;
                float value = slider.getValue();
                volumeLabel.setText(labelText + ": " + (int)value);
                
                // Normalize value from 0-100 to 0-1
                float normalizedValue = value / 100f;
                listener.onVolumeChanged(normalizedValue);
            }
        });
        
        // Add components to table
        sliderTable.add(volumeLabel).padBottom(10).row();
        sliderTable.add(slider).width(300).padBottom(40).expandX().center();
        
        return sliderTable;
    }
    
    /**
     * Create a toggle button for control mode
     */
    public TextButton createControlToggleButton(String initialMode, final ControlModeChangeListener listener) {
        TextButton toggleButton = new TextButton("Control: " + initialMode, skin);
        
        toggleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listener.onControlModeChanged();
            }
        });
        
        return toggleButton;
    }
    
    /**
     * Create a color selection button group
     */
    public Table createColorButtonGroup(String selectedColor, final ColorSelectedListener listener) {
        TextButton.TextButtonStyle defaultStyle = skin.get(TextButton.TextButtonStyle.class);
        
        // Create a selected style with yellow text for highlighting
        TextButton.TextButtonStyle selectedStyle = new TextButton.TextButtonStyle(defaultStyle);
        selectedStyle.fontColor = Color.YELLOW;
        
        // Create the buttons
        final TextButton redButton = new TextButton("Red Snake", skin);
        final TextButton blueButton = new TextButton("Blue Snake", skin);
        final TextButton greenButton = new TextButton("Green Snake", skin);
        
        // Set initial selection
        if ("red".equals(selectedColor)) {
            redButton.setStyle(selectedStyle);
        } else if ("blue".equals(selectedColor)) {
            blueButton.setStyle(selectedStyle);
        } else {
            greenButton.setStyle(selectedStyle);
        }
        
        // Add click listeners
        redButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Update visual selection
                redButton.setStyle(selectedStyle);
                blueButton.setStyle(defaultStyle);
                greenButton.setStyle(defaultStyle);
                // Notify listener
                listener.onColorSelected("red");
            }
        });
        
        blueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Update visual selection
                blueButton.setStyle(selectedStyle);
                redButton.setStyle(defaultStyle);
                greenButton.setStyle(defaultStyle);
                // Notify listener
                listener.onColorSelected("blue");
            }
        });
        
        greenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Update visual selection
                greenButton.setStyle(selectedStyle);
                redButton.setStyle(defaultStyle);
                blueButton.setStyle(defaultStyle);
                // Notify listener
                listener.onColorSelected("green");
            }
        });
        
        // Layout buttons in a table
        Table buttonTable = new Table();
        buttonTable.add(redButton).pad(10).width(150).height(50);
        buttonTable.add(blueButton).pad(10).width(150).height(50);
        buttonTable.add(greenButton).pad(10).width(150).height(50);
        
        return buttonTable;
    }
    
    /**
     * Create a settings panel table
     */
    public Table createSettingsPanel(String title, 
                                     float musicVolume, 
                                     float soundVolume, 
                                     String controlMode,
                                     String selectedColor,
                                     final VolumeChangeListener musicListener,
                                     final VolumeChangeListener soundListener,
                                     final ControlModeChangeListener controlListener,
                                     final ColorSelectedListener colorListener,
                                     final Runnable backAction) {
        
        // Create the settings UI table
        Table settingsTable = new Table();
        settingsTable.setFillParent(true);
        settingsTable.center();
        
        // Title
        Label settingsTitle = new Label(title, skin);
        settingsTitle.setFontScale(1.5f);
        settingsTable.add(settingsTitle).colspan(2).padBottom(40).row();
        
        // Volume sliders
        Table musicSlider = createVolumeSlider("Music Volume", musicVolume, musicListener);
        Table soundSlider = createVolumeSlider("Sound Volume", soundVolume, soundListener);
        
        settingsTable.add(musicSlider).row();
        settingsTable.add(soundSlider).row();
        
        // Control mode toggle
        TextButton controlToggle = createControlToggleButton(controlMode, controlListener);
        settingsTable.add(controlToggle).padBottom(30).center().row();
        
        // Snake color selection
        settingsTable.add(new Label("Snake Color:", skin)).padBottom(10).row();
        Table colorButtons = createColorButtonGroup(selectedColor, colorListener);
        settingsTable.add(colorButtons).colspan(3).center().row();
        
        // Back button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                backAction.run();
            }
        });
        settingsTable.add(backButton).padTop(30).width(200).height(60).row();
        
        return settingsTable;
    }
    
    /**
     * Create a menu button list with selected item highlighting
     */
    public Table createMenuButtonList(String[] menuItems, int selectedIndex, final MenuItemSelectedListener listener) {
        Table menuTable = new Table();
        
        TextButton.TextButtonStyle defaultStyle = skin.get(TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle selectedStyle = new TextButton.TextButtonStyle(defaultStyle);
        selectedStyle.fontColor = Color.YELLOW;
        
        for (int i = 0; i < menuItems.length; i++) {
            final int index = i;
            TextButton button = new TextButton(menuItems[i], i == selectedIndex ? selectedStyle : defaultStyle);
            
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    listener.onMenuItemSelected(index);
                }
            });
            
            menuTable.add(button).pad(10).width(200).height(50).row();
        }
        
        return menuTable;
    }
    
    /**
     * Dispose of resources
     */
    public void dispose() {
        if (sliderKnobTexture != null) {
            sliderKnobTexture.dispose();
        }
        if (sliderBarTexture != null) {
            sliderBarTexture.dispose();
        }
    }
    
    // Interface for volume change callbacks
    public interface VolumeChangeListener {
        void onVolumeChanged(float volume);
    }
    
    // Interface for control mode change callbacks
    public interface ControlModeChangeListener {
        void onControlModeChanged();
    }
    
    // Interface for color selection callbacks
    public interface ColorSelectedListener {
        void onColorSelected(String color);
    }
    
    // Interface for menu item selection
    public interface MenuItemSelectedListener {
        void onMenuItemSelected(int index);
    }
}