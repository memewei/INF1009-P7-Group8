package io.github.some_example_name.lwjgl3.abstract_engine.ui;

/**
 * Class to maintain all asset paths in one place to avoid hardcoding
 * and make asset management easier.
 */
public class AssetPaths {
    // UI Assets
    public static final String UI_SKIN = "uiskin.json";
    public static final String SOUND_SLIDER = "sound_slider.png";
    public static final String SOUND_BAR = "sound_bar.png";
    
    // Textures
    public static final String BACKGROUND = "snake_background.png";
    public static final String TITLE = "health_snake_title.png";
    public static final String INSTRUCTIONS = "instructions.png";
    public static final String PAUSE_MENU = "pause_menu.png";
    public static final String LEVEL_TRANSITION = "level_transition.png";
    public static final String GAME_OVER = "game_over.png";
    public static final String VICTORY = "victory.png";
    public static final String TROPHY = "victory.png";
    public static final String POPUP_BACKGROUND = "popup_background.png";
    
    // Snake Textures
    public static final String SNAKE_HEAD = "snake_head.png";
    public static final String SNAKE_BODY = "snake_body.png";
    public static final String ENEMY_HEAD = "enemy_head.png";
    public static final String ENEMY_BODY = "enemy_body.png";
    public static final String SNAKE_SKULL = "snake_skull.png";
    
    // Food Textures
    public static final String[] HEALTHY_FOOD = {
        "healthy_1.png",
        "healthy_2.png",
        "healthy_3.png",
        "healthy_4.png",
        "healthy_5.png"
    };
    
    public static final String[] UNHEALTHY_FOOD = {
        "unhealthy_1.png",
        "unhealthy_2.png",
        "unhealthy_3.png",
        "unhealthy_4.png",
        "unhealthy_5.png"
    };
    
    // Font
    public static final String GAME_FONT = "game_font.fnt";
    
    // Audio
    public static final String MENU_MUSIC = "menu_music.mp3";
    public static final String GAME_MUSIC = "game_music.mp3";
    public static final String VICTORY_MUSIC = "victory_music.mp3";
    public static final String SAD_MUSIC = "sad_music.mp3";
    
    // Sound Effects
    public static final String MENU_MOVE = "menu_move.mp3";
    public static final String MENU_SELECT = "menu_select.mp3";
    public static final String HEALTHY_FOOD_SOUND = "healthy_food.mp3";
    public static final String UNHEALTHY_FOOD_SOUND = "unhealthy_food.mp3";
    public static final String COLLISION_SOUND = "collision.mp3";
    public static final String GAME_OVER_SOUND = "game_over.mp3";
    public static final String PAUSE_SOUND = "pause.mp3";
    public static final String LEVEL_UP_SOUND = "level_up.mp3";
    public static final String HIT_SOUND = "hit_sound.mp3";
    
    // Private constructor to prevent instantiation
    private AssetPaths() {
        // No instantiation needed for this constants class
    }
}